package com.inventra.purchases.services;

import com.inventra.purchases.dtos.PurchaseRequestDTO;
import com.inventra.purchases.dtos.PurchaseResponseDTO;
import com.inventra.purchases.dtos.PurchaseSummaryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface PurchaseService {

    PurchaseResponseDTO create(PurchaseRequestDTO request);

    PurchaseResponseDTO getById(Long id);

    Page<PurchaseSummaryResponseDTO> search(LocalDateTime from, LocalDateTime to, Pageable pageable);
}