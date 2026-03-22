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
import com.inventra.catalog.repositories.ProductRepository;
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
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void processMovement(StockMovementDTO dto) {

        Stock stock;

        if ("OUT".equalsIgnoreCase(dto.getType())) {
            stock = stockRepository.findByProductId(dto.getProductId()).orElse(null);
            int available = stock == null ? 0 : stock.getQuantity();
            if (available < dto.getQuantity()) {
                throw new BadRequestException("Not enough stock");
            }
            if (stock == null) {
                // No row and available (0) >= quantity only when quantity is 0
                persistMovement(dto);
                return;
            }
            stock.setQuantity(stock.getQuantity() - dto.getQuantity());

        } else if ("IN".equalsIgnoreCase(dto.getType())) {
            stock = stockRepository.findByProductId(dto.getProductId())
                    .orElseGet(() -> ensureStockRowForNewInbound(dto.getProductId()));
            stock.setQuantity(stock.getQuantity() + dto.getQuantity());

        } else {
            throw new BadRequestException("Invalid movement type");
        }

        stockRepository.save(stock);

        persistMovement(dto);
    }

    /**
     * First IN for a product: create a stock row at 0, then increment. Requires a catalog product.
     */
    private Stock ensureStockRowForNewInbound(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new NotFoundException("Product not found with id: " + productId);
        }
        return stockRepository.save(Stock.builder()
                .productId(productId)
                .quantity(0)
                .build());
    }

    private void persistMovement(StockMovementDTO dto) {
        movementRepository.save(StockMovement.builder()
                .productId(dto.getProductId())
                .quantity(dto.getQuantity())
                .type(dto.getType())
                .referenceId(dto.getReferenceId())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getAvailableStock(Long productId) {
        return stockRepository.findByProductId(productId)
                .map(Stock::getQuantity)
                .orElse(0);
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