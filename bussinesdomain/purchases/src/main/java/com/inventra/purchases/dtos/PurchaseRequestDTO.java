package com.inventra.purchases.dtos;


import lombok.Data;

import java.util.List;

@Data
public class PurchaseRequestDTO {
    private Long supplierId;
    private List<PurchaseItemDTO> items;
}