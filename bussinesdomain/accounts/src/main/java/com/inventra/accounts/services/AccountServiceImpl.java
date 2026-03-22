package com.inventra.accounts.services;

import com.inventra.accounts.dtos.*;
import com.inventra.accounts.exceptions.BadRequestException;
import com.inventra.accounts.exceptions.NotFoundException;
import com.inventra.accounts.model.*;
import com.inventra.accounts.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final AccountMovementRepository movementRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentAllocationRepository allocationRepository;
    private final SalesClient salesClient;

    @Override
    @Transactional
    public AccountResponseDTO getOrCreateAccount(Long customerId) {
        ensureCustomer(customerId);
        Account account = accountRepository.findByCustomerId(customerId)
                .orElseGet(() -> accountRepository.save(Account.builder()
                        .customerId(customerId)
                        .currentBalance(BigDecimal.ZERO)
                        .build()));
        return mapAccount(account);
    }

    @Override
    @Transactional
    public AccountResponseDTO getBalance(Long customerId) {
        return getOrCreateAccount(customerId);
    }

    @Override
    @Transactional
    public Page<AccountMovementResponseDTO> getStatement(Long customerId, LocalDateTime from, LocalDateTime to,
                                                         Pageable pageable) {
        AccountResponseDTO ensured = getOrCreateAccount(customerId);
        Account account = accountRepository.findById(ensured.getId()).orElseThrow();
        Page<AccountMovement> page;
        if (from != null && to != null) {
            page = movementRepository.findByAccountIdAndOccurredAtBetween(account.getId(), from, to, pageable);
        } else {
            page = movementRepository.findByAccountId(account.getId(), pageable);
        }
        return page.map(this::mapMovement);
    }

    @Override
    @Transactional
    public PaymentResponseDTO recordPayment(Long customerId, PaymentRequestDTO dto) {
        getOrCreateAccount(customerId);
        Account account = accountRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new NotFoundException("Account not found for customer"));

        if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Payment amount must be positive");
        }

        Payment payment = Payment.builder()
                .accountId(account.getId())
                .amount(dto.getAmount())
                .method(dto.getMethod())
                .paidAt(dto.getPaidAt() != null ? dto.getPaidAt() : LocalDateTime.now())
                .notes(dto.getNotes())
                .build();
        payment = paymentRepository.save(payment);

        BigDecimal remaining = dto.getAmount();
        List<AccountMovement> debts = movementRepository.findByAccountIdAndTypeOrderByOccurredAtAsc(
                account.getId(), MovementType.DEBT);

        for (AccountMovement debt : debts) {
            if (debt.getReferenceType() != ReferenceType.SALE) {
                continue;
            }
            BigDecimal allocated = allocationRepository.sumAllocatedByDebtMovementId(debt.getId());
            BigDecimal open = debt.getAmount().subtract(allocated);
            if (open.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            BigDecimal part = open.min(remaining);
            if (part.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            allocationRepository.save(PaymentAllocation.builder()
                    .paymentId(payment.getId())
                    .debtMovementId(debt.getId())
                    .amount(part)
                    .build());
            allocationRepository.flush();

            BigDecimal newOpen = open.subtract(part);
            if (newOpen.compareTo(BigDecimal.ZERO) == 0) {
                salesClient.markSalePaid(debt.getReferenceId());
            }
            remaining = remaining.subtract(part);
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
        }

        movementRepository.save(AccountMovement.builder()
                .accountId(account.getId())
                .type(MovementType.PAYMENT)
                .amount(payment.getAmount())
                .referenceType(ReferenceType.PAYMENT)
                .referenceId(payment.getId())
                .build());

        account.setCurrentBalance(account.getCurrentBalance().subtract(payment.getAmount()));
        accountRepository.save(account);

        return mapPayment(payment);
    }

    @Override
    @Transactional
    public void registerSaleDebt(SaleDebtRequestDTO dto) {
        if (movementRepository.findByReferenceTypeAndReferenceId(ReferenceType.SALE, dto.getSaleId()).isPresent()) {
            return;
        }
        ensureCustomer(dto.getCustomerId());
        Account account = accountRepository.findByCustomerId(dto.getCustomerId())
                .orElseGet(() -> accountRepository.save(Account.builder()
                        .customerId(dto.getCustomerId())
                        .currentBalance(BigDecimal.ZERO)
                        .build()));

        movementRepository.save(AccountMovement.builder()
                .accountId(account.getId())
                .type(MovementType.DEBT)
                .amount(dto.getAmount())
                .referenceType(ReferenceType.SALE)
                .referenceId(dto.getSaleId())
                .build());

        account.setCurrentBalance(account.getCurrentBalance().add(dto.getAmount()));
        accountRepository.save(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PendingDebtRowDTO> getPendingDebts(BigDecimal minBalance, Pageable pageable) {
        BigDecimal min = minBalance != null ? minBalance : BigDecimal.ZERO;
        return accountRepository.findPendingDebts(min, pageable)
                .map(account -> {
                    Customer c = customerRepository.findById(account.getCustomerId()).orElse(null);
                    String name = c != null ? c.getName() : "?";
                    return PendingDebtRowDTO.builder()
                            .accountId(account.getId())
                            .customerId(account.getCustomerId())
                            .customerName(name)
                            .currentBalance(account.getCurrentBalance())
                            .build();
                });
    }

    @Override
    @Transactional
    public CustomerAccountOverviewDTO getOverview(Long customerId) {
        AccountResponseDTO account = getOrCreateAccount(customerId);
        List<SaleSummaryDTO> sales = salesClient.findSalesByCustomer(customerId);
        return CustomerAccountOverviewDTO.builder()
                .account(account)
                .recentSales(sales)
                .build();
    }

    private void ensureCustomer(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new NotFoundException("Customer not found");
        }
    }

    private AccountResponseDTO mapAccount(Account a) {
        return AccountResponseDTO.builder()
                .id(a.getId())
                .customerId(a.getCustomerId())
                .currentBalance(a.getCurrentBalance())
                .build();
    }

    private AccountMovementResponseDTO mapMovement(AccountMovement m) {
        return AccountMovementResponseDTO.builder()
                .id(m.getId())
                .type(m.getType().name())
                .amount(m.getAmount())
                .occurredAt(m.getOccurredAt())
                .referenceType(m.getReferenceType().name())
                .referenceId(m.getReferenceId())
                .build();
    }

    private PaymentResponseDTO mapPayment(Payment p) {
        return PaymentResponseDTO.builder()
                .id(p.getId())
                .accountId(p.getAccountId())
                .amount(p.getAmount())
                .method(p.getMethod())
                .paidAt(p.getPaidAt())
                .notes(p.getNotes())
                .build();
    }
}
