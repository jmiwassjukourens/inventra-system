package com.inventra.catalog.services;

import com.inventra.catalog.dtos.StockMovementDTO;

public interface StockService {

    void processMovement(StockMovementDTO dto);

    Integer getAvailableStock(Long productId);
}
