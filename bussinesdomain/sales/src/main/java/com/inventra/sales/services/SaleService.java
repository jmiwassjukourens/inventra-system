package com.inventra.sales.services;

import com.inventra.sales.dtos.SaleRequestDTO;
import com.inventra.sales.dtos.SaleResponseDTO;

public interface SaleService {

    SaleResponseDTO createSale(SaleRequestDTO request);

    SaleResponseDTO getById(Long id);
}