/*package com.inventra.catalog.services;

import com.inventra.catalog.dtos.ProductRequestDTO;
import com.inventra.catalog.dtos.ProductResponseDTO;
import com.inventra.catalog.exceptions.NotFoundException;
import com.inventra.catalog.model.Product;
import com.inventra.catalog.model.Stock;
import com.inventra.catalog.repositories.ProductRepository;
import com.inventra.catalog.repositories.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductRequestDTO request;

    @BeforeEach
    void setUp() {
        request = new ProductRequestDTO();
        request.setName("Test name");
        request.setDescription("Test desc");
        request.setSku("SKU-123");
        request.setPrice(BigDecimal.valueOf(12.50));
        request.setCategoryId(5L);
        request.setActive(true);
    }

    @Test
    void shouldCreateProduct() {
        Product saved = Product.builder()
                .id(1L)
                .name(request.getName())
                .description(request.getDescription())
                .sku(request.getSku())
                .price(request.getPrice())
                .categoryId(request.getCategoryId())
                .active(request.isActive())
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(saved);
        when(stockRepository.save(any(Stock.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponseDTO result = productService.create(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test name");
        assertThat(result.getSku()).isEqualTo("SKU-123");

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
        assertThat(captor.getValue().getName()).isEqualTo("Test name");

        ArgumentCaptor<Stock> stockCaptor = ArgumentCaptor.forClass(Stock.class);
        verify(stockRepository).save(stockCaptor.capture());
        assertThat(stockCaptor.getValue().getProductId()).isEqualTo(1L);
        assertThat(stockCaptor.getValue().getQuantity()).isZero();
    }

    @Test
    void shouldGetByIdWhenFound() {
        Product existing = Product.builder()
                .id(2L)
                .name("Existing")
                .description("desc")
                .sku("SKU-222")
                .price(BigDecimal.valueOf(1.0))
                .categoryId(1L)
                .active(false)
                .build();

        when(productRepository.findById(2L)).thenReturn(Optional.of(existing));

        ProductResponseDTO result = productService.getById(2L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("Existing");
    }

    @Test
    void shouldThrowNotFoundWhenGetByIdNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Product not found with id: 99");
    }

    @Test
    void shouldUpdateExistingProduct() {
        Product existing = Product.builder()
                .id(5L)
                .name("Old")
                .description("old desc")
                .sku("SKU-5")
                .price(BigDecimal.valueOf(20))
                .categoryId(2L)
                .active(true)
                .build();

        when(productRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductRequestDTO update = new ProductRequestDTO();
        update.setName("New");
        update.setDescription("new desc");
        update.setSku("SKU-5");
        update.setPrice(BigDecimal.valueOf(22));
        update.setCategoryId(3L);
        update.setActive(false);

        ProductResponseDTO updated = productService.update(5L, update);

        assertThat(updated.getName()).isEqualTo("New");
        assertThat(updated.isActive()).isFalse();

        verify(productRepository).save(existing);
    }

    @Test
    void shouldDeleteById() {
        doNothing().when(productRepository).deleteById(7L);

        productService.delete(7L);

        verify(productRepository).deleteById(7L);
    }

    @Test
    void shouldGetAllProducts() {
        Product p1 = Product.builder().id(10L).name("p1").sku("S1").price(BigDecimal.ONE).categoryId(1L).active(true).build();
        Product p2 = Product.builder().id(11L).name("p2").sku("S2").price(BigDecimal.TEN).categoryId(1L).active(false).build();

        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        var all = productService.getAll();

        assertThat(all).hasSize(2);
        assertThat(all.get(0).getId()).isEqualTo(10L);
        assertThat(all.get(1).getId()).isEqualTo(11L);
    }
       
}
  */