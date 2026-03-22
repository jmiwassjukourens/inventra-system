package com.inventra.sales.dtos;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaleItemDTO {
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice;
}