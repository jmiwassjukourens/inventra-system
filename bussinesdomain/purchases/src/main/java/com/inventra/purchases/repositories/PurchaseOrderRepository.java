package com.inventra.purchases.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventra.purchases.model.PurchaseOrder;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
}