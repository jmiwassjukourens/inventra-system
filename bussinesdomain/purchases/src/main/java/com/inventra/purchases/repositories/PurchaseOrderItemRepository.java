package com.inventra.purchases.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventra.purchases.model.PurchaseOrderItem;

import java.util.List;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {
    List<PurchaseOrderItem> findByPurchaseOrderId(Long id);
}