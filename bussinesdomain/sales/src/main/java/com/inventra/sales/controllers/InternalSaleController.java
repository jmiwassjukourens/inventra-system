package com.inventra.sales.controllers;

import com.inventra.sales.services.SaleService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/sales")
@RequiredArgsConstructor
@Hidden
public class InternalSaleController {

    private final SaleService saleService;

    @Operation(summary = "Marcar venta como pagada (Accounts)")
    @PostMapping("/{id}/mark-paid")
    public ResponseEntity<Void> markPaid(@PathVariable Long id) {
        saleService.markSalePaid(id);
        return ResponseEntity.noContent().build();
    }
}
