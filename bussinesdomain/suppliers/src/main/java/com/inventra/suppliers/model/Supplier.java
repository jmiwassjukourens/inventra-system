package com.inventra.suppliers.model;

import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "suppliers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Proveedor del sistema")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Nombre del proveedor")
    private String name;

    private String email;
    private String phone;
    private String address;

    @Column(nullable = false)
    private boolean active;
}
