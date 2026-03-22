package com.inventra.accounts.services;

import com.inventra.accounts.dtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface AccountService {

    AccountResponseDTO getOrCreateAccount(Long customerId);

    AccountResponseDTO getBalance(Long customerId);

    Page<AccountMovementResponseDTO> getStatement(Long customerId, LocalDateTime from, LocalDateTime to, Pageable pageable);

    PaymentResponseDTO recordPayment(Long customerId, PaymentRequestDTO dto);

    void registerSaleDebt(SaleDebtRequestDTO dto);

    Page<PendingDebtRowDTO> getPendingDebts(BigDecimal minBalance, Pageable pageable);

    CustomerAccountOverviewDTO getOverview(Long customerId);
}
