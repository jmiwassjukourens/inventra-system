package com.inventra.purchases.controllers;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inventra.purchases.dtos.PurchaseRequestDTO;
import com.inventra.purchases.dtos.PurchaseResponseDTO;
import com.inventra.purchases.dtos.PurchaseSummaryResponseDTO;
import com.inventra.purchases.services.PurchaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
@Tag(name = "Purchases", description = "API de compras a proveedores")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @Operation(summary = "Listar órdenes de compra por rango de fechas")
    @GetMapping
    public ResponseEntity<Page<PurchaseSummaryResponseDTO>> search(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @PageableDefault(size = 20, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(purchaseService.search(from, to, pageable));
    }

    @Operation(summary = "Crear una orden de compra")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compra registrada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error externo")
    })
    @PostMapping
    public ResponseEntity<PurchaseResponseDTO> create(
            @RequestBody PurchaseRequestDTO request) {

                System.out.println("Received purchase request: " + request);
        return ResponseEntity.ok(purchaseService.create(request));
    }

    @Operation(summary = "Obtener compra por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compra encontrada"),
            @ApiResponse(responseCode = "404", description = "No encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.getById(id));
    }
}
