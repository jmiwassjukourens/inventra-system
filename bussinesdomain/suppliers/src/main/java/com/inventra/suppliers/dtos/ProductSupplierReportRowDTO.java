package com.inventra.suppliers.dtos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductSupplierReportRowDTO {
    private Long productId;
    private String productName;
    private String productSku;
    private Long supplierId;
    private String supplierName;
    private BigDecimal purchasePrice;
    private Integer deliveryTimeDays;
}
