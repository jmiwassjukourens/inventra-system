package com.inventra.catalog.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventra.catalog.dtos.StockMovementDTO;
import com.inventra.catalog.exceptions.NotFoundException;
import com.inventra.catalog.model.Stock;
import com.inventra.catalog.model.StockMovement;
import com.inventra.catalog.repositories.StockMovementRepository;
import com.inventra.catalog.repositories.StockRepository;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final StockMovementRepository movementRepository;

    @Override
    @Transactional
    public void processMovement(StockMovementDTO dto) {

        Stock stock = stockRepository.findByProductId(dto.getProductId())
                .orElseThrow(() -> new NotFoundException("Stock not found for productId: " + dto.getProductId()));

        if ("OUT".equalsIgnoreCase(dto.getType())) {

            if (stock.getQuantity() < dto.getQuantity()) {
                throw new RuntimeException("Not enough stock");
            }

            stock.setQuantity(stock.getQuantity() - dto.getQuantity());

        } else if ("IN".equalsIgnoreCase(dto.getType())) {

            stock.setQuantity(stock.getQuantity() + dto.getQuantity());

        } else {
            throw new IllegalArgumentException("Invalid movement type");
        }

        stockRepository.save(stock);

        StockMovement movement = StockMovement.builder()
                .productId(dto.getProductId())
                .quantity(dto.getQuantity())
                .type(dto.getType())
                .referenceId(dto.getReferenceId())
                .build();

        movementRepository.save(movement);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getAvailableStock(Long productId) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Stock not found"));

        return stock.getQuantity();
    }
}