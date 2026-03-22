package com.inventra.purchases.dtos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PurchaseSummaryResponseDTO {
    private Long id;
    private Long supplierId;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal totalAmount;
}
