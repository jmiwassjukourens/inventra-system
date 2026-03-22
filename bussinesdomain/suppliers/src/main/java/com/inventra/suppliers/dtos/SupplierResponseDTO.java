package com.inventra.suppliers.dtos;

import lombok.Data;
import lombok.Builder;
@Data
@Builder
public class SupplierResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private boolean active;
}