package com.inventra.accounts.dtos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PendingDebtRowDTO {
    private Long accountId;
    private Long customerId;
    private String customerName;
    private BigDecimal currentBalance;
}
