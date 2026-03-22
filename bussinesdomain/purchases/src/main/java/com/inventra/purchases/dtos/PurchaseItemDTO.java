package com.inventra.purchases.dtos;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseItemDTO {
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice;
}