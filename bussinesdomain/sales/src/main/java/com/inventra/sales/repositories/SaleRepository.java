package com.inventra.sales.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventra.sales.model.Sale;

public interface SaleRepository extends JpaRepository<Sale, Long> {
}