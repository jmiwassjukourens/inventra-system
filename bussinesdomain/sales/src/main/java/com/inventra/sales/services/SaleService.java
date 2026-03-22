package com.inventra.sales.services;

import com.inventra.sales.dtos.SaleRequestDTO;
import com.inventra.sales.dtos.SaleResponseDTO;
import com.inventra.sales.dtos.SaleSummaryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleService {

    SaleResponseDTO createSale(SaleRequestDTO request);

    SaleResponseDTO getById(Long id);

    Page<SaleSummaryResponseDTO> search(LocalDateTime from, LocalDateTime to, String paymentStatus, Pageable pageable);

    List<SaleSummaryResponseDTO> findByCustomerId(Long customerId);

    void markSalePaid(Long saleId);
}