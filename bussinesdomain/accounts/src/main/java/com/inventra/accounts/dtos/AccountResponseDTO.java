package com.inventra.accounts.dtos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountResponseDTO {
    private Long id;
    private Long customerId;
    private BigDecimal currentBalance;
}
