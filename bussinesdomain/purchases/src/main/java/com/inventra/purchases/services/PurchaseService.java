package com.inventra.purchases.services;

import com.inventra.purchases.dtos.PurchaseRequestDTO;
import com.inventra.purchases.dtos.PurchaseResponseDTO;

public interface PurchaseService {

    PurchaseResponseDTO create(PurchaseRequestDTO request);

    PurchaseResponseDTO getById(Long id);
}