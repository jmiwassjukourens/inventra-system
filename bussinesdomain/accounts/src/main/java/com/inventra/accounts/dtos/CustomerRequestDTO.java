package com.inventra.accounts.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerRequestDTO {
    @NotBlank
    private String name;
    private String taxId;
    private String email;
    private String phone;
    private Boolean active;
}
