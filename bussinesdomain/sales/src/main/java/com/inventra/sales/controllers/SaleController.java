package com.inventra.sales.controllers;


import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inventra.sales.dtos.SaleRequestDTO;
import com.inventra.sales.dtos.SaleResponseDTO;
import com.inventra.sales.services.SaleService;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@Tag(name = "Sales", description = "API de ventas")
public class SaleController {

    private final SaleService saleService;

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
