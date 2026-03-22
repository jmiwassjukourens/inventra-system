package com.inventra.suppliers.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventra.suppliers.model.SupplierProduct;

import java.util.List;

public interface SupplierProductRepository extends JpaRepository<SupplierProduct, Long> {

    List<SupplierProduct> findBySupplierId(Long supplierId);

    List<SupplierProduct> findByProductId(Long productId);
}