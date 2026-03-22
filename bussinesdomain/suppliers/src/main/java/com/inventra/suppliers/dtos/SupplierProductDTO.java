package com.inventra.suppliers.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SupplierProductDTO {
    private Long supplierId;
    private Long productId;
    private BigDecimal purchasePrice;
    private Integer deliveryTimeDays;
}