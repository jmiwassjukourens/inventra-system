package com.inventra.catalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventra.catalog.model.StockMovement;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
}