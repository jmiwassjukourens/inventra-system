package com.inventra.purchases.dtos;

import lombok.Data;

@Data
public class StockMovementDTO {
    private Long productId;
    private Integer quantity;
    private String type;
    private Long referenceId;
}