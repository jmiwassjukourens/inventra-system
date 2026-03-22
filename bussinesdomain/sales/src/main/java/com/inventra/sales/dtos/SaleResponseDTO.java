package com.inventra.sales.dtos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SaleResponseDTO {
    private Long id;
    private Long customerId;
    private LocalDateTime saleDate;
    private BigDecimal totalAmount;
    private String status;
    private List<SaleItemDTO> items;
}
