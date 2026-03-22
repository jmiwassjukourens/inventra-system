package com.inventra.purchases.dtos;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PurchaseResponseDTO {
    private Long id;
    private Long supplierId;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal totalAmount;
    private List<PurchaseItemDTO> items;
}