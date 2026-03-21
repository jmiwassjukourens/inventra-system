package com.inventra.catalog.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inventra.catalog.dtos.StockMovementDTO;
import com.inventra.catalog.services.StockService;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PostMapping("/movements")
    public ResponseEntity<Void> processMovement(@RequestBody StockMovementDTO dto) {
        stockService.processMovement(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Integer> getStock(@PathVariable Long productId) {
        return ResponseEntity.ok(stockService.getAvailableStock(productId));
    }
}