package com.inventra.catalog.services;

import java.util.List;

import com.inventra.catalog.dtos.ProductRequestDTO;
import com.inventra.catalog.dtos.ProductResponseDTO;

public interface ProductService {

    ProductResponseDTO create(ProductRequestDTO dto);

    ProductResponseDTO getById(Long id);

    List<ProductResponseDTO> getAll();

    ProductResponseDTO update(Long id, ProductRequestDTO dto);

    void delete(Long id);
}
