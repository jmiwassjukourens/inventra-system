package com.inventra.catalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventra.catalog.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
