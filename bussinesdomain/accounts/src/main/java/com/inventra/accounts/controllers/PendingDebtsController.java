package com.inventra.accounts.controllers;

import com.inventra.accounts.dtos.PendingDebtRowDTO;
import com.inventra.accounts.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts reporting", description = "Pending debts and aggregates")
public class PendingDebtsController {

    private final AccountService accountService;

    @Operation(summary = "Customers with positive balance (pending debts)")
    @GetMapping("/pending-debts")
    public ResponseEntity<Page<PendingDebtRowDTO>> getPendingDebts(
            @Parameter(description = "Minimum balance to include")
            @RequestParam(required = false) BigDecimal minBalance,
            @PageableDefault(size = 20, sort = "currentBalance", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(accountService.getPendingDebts(minBalance, pageable));
    }
}
