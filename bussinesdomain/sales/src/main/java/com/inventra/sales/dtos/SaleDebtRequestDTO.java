package com.inventra.sales.dtos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SaleDebtRequestDTO {
    private Long saleId;
    private Long customerId;
    private BigDecimal amount;
}
