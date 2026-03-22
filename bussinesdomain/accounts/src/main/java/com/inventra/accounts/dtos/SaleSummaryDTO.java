package com.inventra.accounts.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SaleSummaryDTO {
    private Long id;
    private Long customerId;
    private LocalDateTime saleDate;
    private BigDecimal totalAmount;
    private String status;
    private String paymentStatus;
}
