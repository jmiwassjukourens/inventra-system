package com.inventra.accounts.dtos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AccountMovementResponseDTO {
    private Long id;
    private String type;
    private BigDecimal amount;
    private LocalDateTime occurredAt;
    private String referenceType;
    private Long referenceId;
}
