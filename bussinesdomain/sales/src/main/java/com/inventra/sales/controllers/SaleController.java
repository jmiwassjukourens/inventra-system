package com.inventra.sales.controllers;


import com.inventra.sales.dtos.SaleRequestDTO;
import com.inventra.sales.dtos.SaleResponseDTO;
import com.inventra.sales.dtos.SaleSummaryResponseDTO;
import com.inventra.sales.services.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@Tag(name = "Sales", description = "API de ventas")
public class SaleController {

    private final SaleService saleService;

    @Operation(summary = "Listar ventas con filtros opcionales")
    @GetMapping
    public ResponseEntity<Page<SaleSummaryResponseDTO>> search(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) String paymentStatus,
            @PageableDefault(size = 20, sort = "saleDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(saleService.search(from, to, paymentStatus, pageable));
    }

    @Operation(summary = "Ventas por cliente")
    @GetMapping("/by-customer/{customerId}")
    public ResponseEntity<List<SaleSummaryResponseDTO>> byCustomer(
            @Parameter(description = "Customer id (Accounts service)") @PathVariable Long customerId) {
        return ResponseEntity.ok(saleService.findByCustomerId(customerId));
    }

    @Operation(summary = "Crear una venta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venta creada"),
            @ApiResponse(responseCode = "400", description = "Error en datos"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @PostMapping
    public ResponseEntity<SaleResponseDTO> create(
            @RequestBody SaleRequestDTO request) {

        return ResponseEntity.ok(saleService.createSale(request));
    }

    @Operation(summary = "Obtener venta por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venta encontrada"),
            @ApiResponse(responseCode = "404", description = "No encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SaleResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.getById(id));
    }
}
