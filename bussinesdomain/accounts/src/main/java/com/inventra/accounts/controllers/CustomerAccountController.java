package com.inventra.accounts.controllers;

import com.inventra.accounts.dtos.*;
import com.inventra.accounts.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/customers/{customerId}")
@RequiredArgsConstructor
@Tag(name = "Customer accounts", description = "Current account, statement, payments")
public class CustomerAccountController {

    private final AccountService accountService;

    @Operation(summary = "Get or create current account for customer")
    @GetMapping("/account")
    public ResponseEntity<AccountResponseDTO> getAccount(
            @Parameter(description = "Customer id") @PathVariable Long customerId) {
        return ResponseEntity.ok(accountService.getOrCreateAccount(customerId));
    }

    @Operation(summary = "Get customer account balance")
    @GetMapping("/account/balance")
    public ResponseEntity<AccountResponseDTO> getBalance(@PathVariable Long customerId) {
        return ResponseEntity.ok(accountService.getBalance(customerId));
    }

    @Operation(summary = "Account statement (movements) with optional date range")
    @GetMapping("/account/statement")
    public ResponseEntity<Page<AccountMovementResponseDTO>> getStatement(
            @PathVariable Long customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @PageableDefault(size = 20, sort = "occurredAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(accountService.getStatement(customerId, from, to, pageable));
    }

    @Operation(summary = "Register a payment against the customer account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment recorded"),
            @ApiResponse(responseCode = "400", description = "Invalid amount"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @PostMapping("/payments")
    public ResponseEntity<PaymentResponseDTO> recordPayment(
            @PathVariable Long customerId,
            @Valid @RequestBody PaymentRequestDTO dto) {
        return ResponseEntity.ok(accountService.recordPayment(customerId, dto));
    }

    @Operation(summary = "Overview: balance plus sales from Sales service")
    @GetMapping("/account/overview")
    public ResponseEntity<CustomerAccountOverviewDTO> getOverview(@PathVariable Long customerId) {
        return ResponseEntity.ok(accountService.getOverview(customerId));
    }
}
