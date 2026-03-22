package com.inventra.suppliers.model;

import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Entity
@Table(name = "supplier_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Relación entre proveedor y producto")
public class SupplierProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private BigDecimal purchasePrice;

    private Integer deliveryTimeDays;
}
