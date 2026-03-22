package com.inventra.suppliers.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventra.suppliers.model.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}