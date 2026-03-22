package com.inventra.catalog.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventra.catalog.dtos.StockMovementDTO;
import com.inventra.catalog.dtos.StockMovementResponseDTO;
import com.inventra.catalog.exceptions.BadRequestException;
import com.inventra.catalog.exceptions.NotFoundException;
import com.inventra.catalog.model.Stock;
import com.inventra.catalog.model.StockMovement;
import com.inventra.catalog.repositories.StockMovementRepository;
import com.inventra.catalog.repositories.StockMovementSpecifications;
import com.inventra.catalog.repositories.StockRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

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
                throw new BadRequestException("Not enough stock");
            }

            stock.setQuantity(stock.getQuantity() - dto.getQuantity());

        } else if ("IN".equalsIgnoreCase(dto.getType())) {

            stock.setQuantity(stock.getQuantity() + dto.getQuantity());

        } else {
            throw new BadRequestException("Invalid movement type");
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

    @Override
    @Transactional(readOnly = true)
    public Page<StockMovementResponseDTO> searchMovements(LocalDateTime from, LocalDateTime to, String type,
                                                          Long productId, Pageable pageable) {
        Specification<StockMovement> spec =
                StockMovementSpecifications.withFilters(from, to, type, productId);
        return movementRepository.findAll(spec, pageable).map(this::mapMovement);
    }

    private StockMovementResponseDTO mapMovement(StockMovement m) {
        return StockMovementResponseDTO.builder()
                .id(m.getId())
                .productId(m.getProductId())
                .quantity(m.getQuantity())
                .type(m.getType())
                .referenceId(m.getReferenceId())
                .date(m.getDate())
                .build();
    }
}