package com.inventra.catalog.dtos;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String sku;
    private BigDecimal price;
    private Long categoryId;
    private boolean active;
}
