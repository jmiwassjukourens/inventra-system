package com.inventra.suppliers.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDTO {
    private Long id;
    private String name;
    private String sku;
    private BigDecimal price;
}
