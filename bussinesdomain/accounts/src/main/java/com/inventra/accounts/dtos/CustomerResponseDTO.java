package com.inventra.accounts.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CustomerResponseDTO {
    private Long id;
    private String name;
    private String taxId;
    private String email;
    private String phone;
    private Boolean active;
    private LocalDateTime createdAt;
}
