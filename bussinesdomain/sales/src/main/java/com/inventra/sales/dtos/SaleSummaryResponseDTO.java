package com.inventra.sales.dtos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class SaleSummaryResponseDTO {
    private Long id;
    private Long customerId;
    private LocalDateTime saleDate;
    private BigDecimal totalAmount;
    private String status;
    private String paymentStatus;
}
