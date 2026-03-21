package com.inventra.catalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.inventra.catalog.model.Stock;

import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Stock> findByProductId(Long productId);
}
