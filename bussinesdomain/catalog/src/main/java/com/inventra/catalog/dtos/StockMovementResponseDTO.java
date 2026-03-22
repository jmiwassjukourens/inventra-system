package com.inventra.catalog.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StockMovementResponseDTO {
    private Long id;
    private Long productId;
    private Integer quantity;
    private String type;
    private Long referenceId;
    private LocalDateTime date;
}
