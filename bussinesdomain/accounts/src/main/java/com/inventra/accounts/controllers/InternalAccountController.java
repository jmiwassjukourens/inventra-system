package com.inventra.accounts.controllers;

import com.inventra.accounts.dtos.SaleDebtRequestDTO;
import com.inventra.accounts.services.AccountService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/account-movements")
@RequiredArgsConstructor
@Hidden
public class InternalAccountController {

    private final AccountService accountService;

    @Operation(summary = "Idempotent: register debt for a completed sale")
    @PostMapping("/sale-debt")
    public ResponseEntity<Void> registerSaleDebt(@Valid @RequestBody SaleDebtRequestDTO dto) {
        accountService.registerSaleDebt(dto);
        return ResponseEntity.noContent().build();
    }
}
