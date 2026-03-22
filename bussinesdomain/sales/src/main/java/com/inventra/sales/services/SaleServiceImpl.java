package com.inventra.sales.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventra.sales.dtos.SaleItemDTO;
import com.inventra.sales.dtos.SaleRequestDTO;
import com.inventra.sales.dtos.SaleResponseDTO;
import com.inventra.sales.dtos.StockMovementDTO;
import com.inventra.sales.exceptions.NotFoundException;
import com.inventra.sales.model.Sale;
import com.inventra.sales.model.SaleItem;
import com.inventra.sales.repositories.SaleItemRepository;
import com.inventra.sales.repositories.SaleRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final InventoryClient inventoryClient;

    @Override
    @Transactional
    public SaleResponseDTO createSale(SaleRequestDTO request) {

        // 1. Crear venta en estado inicial
        Sale sale = Sale.builder()
                .customerId(request.getCustomerId())
                .saleDate(LocalDateTime.now())
                .status("PENDING")
                .totalAmount(BigDecimal.ZERO)
                .build();

        sale = saleRepository.save(sale);

        BigDecimal total = BigDecimal.ZERO;

        // 2. Guardar items
        for (SaleItemDTO itemDTO : request.getItems()) {

            SaleItem item = SaleItem.builder()
                    .saleId(sale.getId())
                    .productId(itemDTO.getProductId())
                    .quantity(itemDTO.getQuantity())
                    .unitPrice(itemDTO.getUnitPrice())
                    .build();

            saleItemRepository.save(item);

            total = total.add(
                    itemDTO.getUnitPrice()
                            .multiply(BigDecimal.valueOf(itemDTO.getQuantity()))
            );

            // 3. Impactar stock
            StockMovementDTO movement = new StockMovementDTO();
            movement.setProductId(itemDTO.getProductId());
            movement.setQuantity(itemDTO.getQuantity());
            movement.setType("OUT");
            movement.setReferenceId(sale.getId());

            inventoryClient.createStockMovement(movement);
        }

        // 4. Finalizar venta
        sale.setTotalAmount(total);
        sale.setStatus("COMPLETED");

        saleRepository.save(sale);

        return mapToResponse(sale, request.getItems());
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponseDTO getById(Long id) {

        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sale not found"));

        List<SaleItem> items = saleItemRepository.findBySaleId(id);

        List<SaleItemDTO> dtoItems = items.stream().map(i -> {
            SaleItemDTO dto = new SaleItemDTO();
            dto.setProductId(i.getProductId());
            dto.setQuantity(i.getQuantity());
            dto.setUnitPrice(i.getUnitPrice());
            return dto;
        }).toList();

        return mapToResponse(sale, dtoItems);
    }

    private SaleResponseDTO mapToResponse(Sale sale, List<SaleItemDTO> items) {
        return SaleResponseDTO.builder()
                .id(sale.getId())
                .customerId(sale.getCustomerId())
                .saleDate(sale.getSaleDate())
                .totalAmount(sale.getTotalAmount())
                .status(sale.getStatus())
                .items(items)
                .build();
    }
}