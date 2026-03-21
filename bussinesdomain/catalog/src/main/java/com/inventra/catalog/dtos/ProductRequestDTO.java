package com.inventra.catalog.dtos;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequestDTO {
    private String name;
    private String description;
    private String sku;
    private BigDecimal price;
    private Long categoryId;
    private boolean active;
}