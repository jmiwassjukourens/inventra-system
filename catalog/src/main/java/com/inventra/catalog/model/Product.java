package com.inventra.catalog.model;

import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Producto del sistema")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del producto", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Nombre del producto", example = "Notebook Lenovo")
    private String name;

    @Column(length = 500)
    @Schema(description = "Descripción del producto")
    private String description;

    @Column(nullable = false, unique = true)
    @Schema(description = "SKU único del producto", example = "SKU-12345")
    private String sku;

    @Column(nullable = false)
    @Schema(description = "Precio del producto", example = "1500.00")
    private BigDecimal price;

    @Column(name = "category_id")
    @Schema(description = "ID de la categoría")
    private Long categoryId;

    @Column(nullable = false)
    @Schema(description = "Indica si el producto está activo")
    private boolean active;
}