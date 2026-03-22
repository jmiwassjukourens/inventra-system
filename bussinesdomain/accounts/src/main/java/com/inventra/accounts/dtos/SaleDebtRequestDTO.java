package com.inventra.accounts.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaleDebtRequestDTO {
    @NotNull
    private Long saleId;

    @NotNull
    private Long customerId;

    @NotNull
    @DecimalMin(value = "0.01", inclusive = true)
    private BigDecimal amount;
}
