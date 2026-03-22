package com.inventra.accounts.dtos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponseDTO {
    private Long id;
    private Long accountId;
    private BigDecimal amount;
    private String method;
    private LocalDateTime paidAt;
    private String notes;
}
