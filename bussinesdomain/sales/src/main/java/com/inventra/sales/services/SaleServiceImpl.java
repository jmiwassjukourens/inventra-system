package com.inventra.sales.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventra.sales.dtos.*;
import com.inventra.sales.exceptions.BadRequestException;
import com.inventra.sales.exceptions.NotFoundException;
import com.inventra.sales.model.Sale;
import com.inventra.sales.model.SaleItem;
import com.inventra.sales.repositories.SaleItemRepository;
import com.inventra.sales.repositories.SaleRepository;
import com.inventra.sales.repositories.SaleSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final InventoryClient inventoryClient;
    private final AccountsClient accountsClient;

    @Override
    @Transactional
    public SaleResponseDTO createSale(SaleRequestDTO request) {

        // 1. Crear venta en estado inicial
        String paymentStatus = request.getCustomerId() != null ? "UNPAID" : "PAID";
        Sale sale = Sale.builder()
                .customerId(request.getCustomerId())
                .saleDate(LocalDateTime.now())
                .status("PENDING")
                .totalAmount(BigDecimal.ZERO)
                .paymentStatus(paymentStatus)
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

        if (sale.getCustomerId() != null) {
            accountsClient.registerSaleDebt(SaleDebtRequestDTO.builder()
                    .saleId(sale.getId())
                    .customerId(sale.getCustomerId())
                    .amount(total)
                    .build());
        }

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

    @Override
    @Transactional(readOnly = true)
    public Page<SaleSummaryResponseDTO> search(LocalDateTime from, LocalDateTime to, String paymentStatus,
                                               Pageable pageable) {
        Specification<Sale> spec = SaleSpecifications.withFilters(from, to, paymentStatus);
        return saleRepository.findAll(spec, pageable).map(this::mapSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleSummaryResponseDTO> findByCustomerId(Long customerId) {
        return saleRepository.findByCustomerIdOrderBySaleDateDesc(customerId).stream()
                .map(this::mapSummary)
                .toList();
    }

    @Override
    @Transactional
    public void markSalePaid(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new NotFoundException("Sale not found"));
        if ("PAID".equalsIgnoreCase(sale.getPaymentStatus())) {
            throw new BadRequestException("Sale already marked as paid");
        }
        sale.setPaymentStatus("PAID");
        saleRepository.save(sale);
    }

    private SaleSummaryResponseDTO mapSummary(Sale sale) {
        return SaleSummaryResponseDTO.builder()
                .id(sale.getId())
                .customerId(sale.getCustomerId())
                .saleDate(sale.getSaleDate())
                .totalAmount(sale.getTotalAmount())
                .status(sale.getStatus())
                .paymentStatus(sale.getPaymentStatus())
                .build();
    }

    private SaleResponseDTO mapToResponse(Sale sale, List<SaleItemDTO> items) {
        return SaleResponseDTO.builder()
                .id(sale.getId())
                .customerId(sale.getCustomerId())
                .saleDate(sale.getSaleDate())
                .totalAmount(sale.getTotalAmount())
                .status(sale.getStatus())
                .paymentStatus(sale.getPaymentStatus())
                .items(items)
                .build();
    }
}