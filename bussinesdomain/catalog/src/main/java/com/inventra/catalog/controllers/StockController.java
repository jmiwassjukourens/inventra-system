package com.inventra.catalog.controllers;

import com.inventra.catalog.dtos.StockMovementDTO;
import com.inventra.catalog.dtos.StockMovementResponseDTO;
import com.inventra.catalog.services.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@Tag(name = "Stock", description = "Stock levels and movements")
public class StockController {

    private final StockService stockService;

    @Operation(summary = "List stock movements with optional filters")
    @GetMapping("/movements")
    public ResponseEntity<Page<StockMovementResponseDTO>> listMovements(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) @Parameter(description = "IN or OUT") String type,
            @RequestParam(required = false) Long productId,
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(stockService.searchMovements(from, to, type, productId, pageable));
    }

    @Operation(summary = "Register a stock movement")
    @PostMapping("/movements")
    public ResponseEntity<Void> processMovement(@RequestBody StockMovementDTO dto) {
        stockService.processMovement(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Available quantity for a product")
    @GetMapping("/{productId}")
    public ResponseEntity<Integer> getStock(
            @Parameter(description = "Product id") @PathVariable Long productId) {
        return ResponseEntity.ok(stockService.getAvailableStock(productId));
    }
}