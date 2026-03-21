package com.inventra.catalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventra.catalog.model.Product;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);
}
