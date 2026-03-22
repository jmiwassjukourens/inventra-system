package com.inventra.sales.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventra.sales.model.SaleItem;

import java.util.List;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {
    List<SaleItem> findBySaleId(Long saleId);
}
