package com.inventra.suppliers.dtos;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO para crear proveedor")
public class SupplierRequestDTO {

    @Schema(example = "Proveedor SRL")
    private String name;

    @Schema(example = "proveedor@mail.com")
    private String email;

    @Schema(example = "+5491123456789")
    private String phone;

    @Schema(example = "Av. Siempre Viva 742")
    private String address;

    @Schema(example = "true")
    private boolean active;
}