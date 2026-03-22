package com.inventra.catalog.services;

import com.inventra.catalog.dtos.StockMovementDTO;
import com.inventra.catalog.dtos.StockMovementResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface StockService {

    void processMovement(StockMovementDTO dto);

    Integer getAvailableStock(Long productId);

    Page<StockMovementResponseDTO> searchMovements(LocalDateTime from, LocalDateTime to, String type, Long productId,
                                                   Pageable pageable);
}
