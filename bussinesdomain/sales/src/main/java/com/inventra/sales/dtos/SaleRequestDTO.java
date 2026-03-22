package com.inventra.sales.dtos;

import lombok.Data;

import java.util.List;

@Data
public class SaleRequestDTO {
    private Long customerId;
    private List<SaleItemDTO> items;
}