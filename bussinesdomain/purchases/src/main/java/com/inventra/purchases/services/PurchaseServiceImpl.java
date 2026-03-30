package com.inventra.purchases.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventra.purchases.dtos.PurchaseItemDTO;
import com.inventra.purchases.dtos.PurchaseRequestDTO;
import com.inventra.purchases.dtos.PurchaseResponseDTO;
import com.inventra.purchases.dtos.StockMovementDTO;
import com.inventra.purchases.exceptions.NotFoundException;
import com.inventra.purchases.model.PurchaseOrder;
import com.inventra.purchases.model.PurchaseOrderItem;
import com.inventra.purchases.dtos.PurchaseSummaryResponseDTO;
import com.inventra.purchases.repositories.PurchaseOrderItemRepository;
import com.inventra.purchases.repositories.PurchaseOrderRepository;
import com.inventra.purchases.repositories.PurchaseOrderSpecifications;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseOrderRepository purchaseRepository;
    private final PurchaseOrderItemRepository itemRepository;
    private final InventoryClient inventoryClient;

    @Override
    @Transactional
    public PurchaseResponseDTO create(PurchaseRequestDTO request) {
        System.out.println("Creating purchase order for supplierId: " + request.getSupplierId() + " with " + request.getItems().size() + " items.");

        // 1. Create order
        PurchaseOrder order = PurchaseOrder.builder()
                .supplierId(request.getSupplierId())
                .orderDate(LocalDateTime.now())
                .status("CREATED")
                .totalAmount(BigDecimal.ZERO)
                .build();

        order = purchaseRepository.save(order);

        BigDecimal total = BigDecimal.ZERO;

        // 2. Process items
        for (PurchaseItemDTO itemDTO : request.getItems()) {

            PurchaseOrderItem item = PurchaseOrderItem.builder()
                    .purchaseOrderId(order.getId())
                    .productId(itemDTO.getProductId())
                    .quantity(itemDTO.getQuantity())
                    .unitPrice(itemDTO.getUnitPrice())
                    .build();

            itemRepository.save(item);

            // Calculate total
            total = total.add(
                    itemDTO.getUnitPrice()
                            .multiply(BigDecimal.valueOf(itemDTO.getQuantity()))
            );

            // 3. Create stock movement (IN)
            StockMovementDTO movement = new StockMovementDTO();
            movement.setProductId(itemDTO.getProductId());
            movement.setQuantity(itemDTO.getQuantity());
            movement.setType("IN"); // purchase always increases stock
            movement.setReferenceId(order.getId());

            // 4. Call inventory service (IMPORTANT: fail fast)
            try {
                System.out.println("Creating stock movement for productId: " + itemDTO.getProductId() + " quantity: " + itemDTO.getQuantity());
                inventoryClient.createStockMovement(movement);
                
            } catch (Exception e) {
                // rollback purchase if inventory fails
                throw new RuntimeException(
                        "Failed to update stock for productId: " + itemDTO.getProductId() + ": "    + e.getMessage(),
                        e
                );
            }
        }

        // 5. Finalize order
        order.setTotalAmount(total);
        order.setStatus("RECEIVED");

        purchaseRepository.save(order);

        return mapToResponse(order, request.getItems());
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseResponseDTO getById(Long id) {

        PurchaseOrder order = purchaseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Purchase not found"));

        List<PurchaseOrderItem> items = itemRepository.findByPurchaseOrderId(id);

        List<PurchaseItemDTO> dtoItems = items.stream().map(i -> {
            PurchaseItemDTO dto = new PurchaseItemDTO();
            dto.setProductId(i.getProductId());
            dto.setQuantity(i.getQuantity());
            dto.setUnitPrice(i.getUnitPrice());
            return dto;
        }).toList();

        return mapToResponse(order, dtoItems);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseSummaryResponseDTO> search(LocalDateTime from, LocalDateTime to, Pageable pageable) {
        Specification<PurchaseOrder> spec =
                PurchaseOrderSpecifications.withOrderDateBetween(from, to);

        return purchaseRepository.findAll(spec, pageable)
                .map(this::mapSummary);
    }

    private PurchaseSummaryResponseDTO mapSummary(PurchaseOrder order) {
        return PurchaseSummaryResponseDTO.builder()
                .id(order.getId())
                .supplierId(order.getSupplierId())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .build();
    }

    private PurchaseResponseDTO mapToResponse(PurchaseOrder order, List<PurchaseItemDTO> items) {
        return PurchaseResponseDTO.builder()
                .id(order.getId())
                .supplierId(order.getSupplierId())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .items(items)
                .build();
    }
}