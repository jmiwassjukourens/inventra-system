package com.inventra.catalog.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.inventra.catalog.dtos.ProductRequestDTO;
import com.inventra.catalog.dtos.ProductResponseDTO;
import com.inventra.catalog.exceptions.NotFoundException;
import com.inventra.catalog.model.Product;
import com.inventra.catalog.repositories.ProductRepository;

import jakarta.transaction.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor 
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;


    @Override
    @Transactional
    public ProductResponseDTO create(ProductRequestDTO dto) {
        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .sku(dto.getSku())
                .price(dto.getPrice())
                .categoryId(dto.getCategoryId())
                .active(dto.isActive())
                .build();

        return mapToDTO(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponseDTO getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));

        return mapToDTO(product);
    }

    @Override
    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategoryId(dto.getCategoryId());
        product.setActive(dto.isActive());

        return mapToDTO(productRepository.save(product));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    private ProductResponseDTO mapToDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .sku(product.getSku())
                .price(product.getPrice())
                .categoryId(product.getCategoryId())
                .active(product.isActive())
                .build();
    }

    @Override
    public List<ProductResponseDTO> getAll() {  
        return productRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }
}