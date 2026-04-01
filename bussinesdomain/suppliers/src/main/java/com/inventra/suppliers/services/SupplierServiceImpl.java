package com.inventra.suppliers.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventra.suppliers.dtos.ProductDTO;
import com.inventra.suppliers.dtos.ProductSupplierReportRowDTO;
import com.inventra.suppliers.dtos.SupplierProductDTO;
import com.inventra.suppliers.dtos.SupplierRequestDTO;
import com.inventra.suppliers.dtos.SupplierResponseDTO;
import com.inventra.suppliers.exceptions.NotFoundException;
import com.inventra.suppliers.model.Supplier;
import com.inventra.suppliers.model.SupplierProduct;
import com.inventra.suppliers.repositories.SupplierProductRepository;
import com.inventra.suppliers.repositories.SupplierRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierProductRepository supplierProductRepository;
    private final InventoryClient inventoryClient;

    @Override
    @Transactional
    public SupplierResponseDTO create(SupplierRequestDTO dto) {

        Supplier supplier = Supplier.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .active(dto.isActive())
                .build();

        return mapToDTO(supplierRepository.save(supplier));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponseDTO> getAll() {
        return supplierRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponseDTO getById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Supplier not found"));

        return mapToDTO(supplier);
    }

    @Override
    @Transactional
    public SupplierResponseDTO update(Long id, SupplierRequestDTO dto) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Supplier not found"));
        supplier.setName(dto.getName());
        supplier.setEmail(dto.getEmail());
        supplier.setPhone(dto.getPhone());
        supplier.setAddress(dto.getAddress());
        supplier.setActive(dto.isActive());
        return mapToDTO(supplierRepository.save(supplier));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        supplierRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void addProductToSupplier(SupplierProductDTO dto) {

        SupplierProduct relation = SupplierProduct.builder()
                .supplierId(dto.getSupplierId())
                .productId(dto.getProductId())
                .purchasePrice(dto.getPurchasePrice())
                .deliveryTimeDays(dto.getDeliveryTimeDays())
                .build();

        supplierProductRepository.save(relation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsBySupplier(Long supplierId) {

        List<Long> productIds = supplierProductRepository.findBySupplierId(supplierId)
                .stream()
                .map(SupplierProduct::getProductId)
                .toList();

        return inventoryClient.getProductsByIds(productIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSupplierReportRowDTO> getProductsWithSuppliersReport() {
        Map<Long, Supplier> suppliersById = supplierRepository.findAll().stream()
                .collect(Collectors.toMap(Supplier::getId, Function.identity()));
        Map<Long, ProductDTO> productsById = inventoryClient.getAllProducts().stream()
                .filter(p -> p.getId() != null)
                .collect(Collectors.toMap(ProductDTO::getId, Function.identity(), (a, b) -> a));

        return supplierProductRepository.findAll().stream()
                .map(sp -> {
                    Supplier s = suppliersById.get(sp.getSupplierId());
                    ProductDTO p = productsById.get(sp.getProductId());
                    return ProductSupplierReportRowDTO.builder()
                            .productId(sp.getProductId())
                            .productName(p != null ? p.getName() : null)
                            .productSku(p != null ? p.getSku() : null)
                            .supplierId(sp.getSupplierId())
                            .supplierName(s != null ? s.getName() : null)
                            .purchasePrice(sp.getPurchasePrice())
                            .deliveryTimeDays(sp.getDeliveryTimeDays())
                            .build();
                })
                .toList();
    }

    private SupplierResponseDTO mapToDTO(Supplier supplier) {
        return SupplierResponseDTO.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .address(supplier.getAddress())
                .active(supplier.isActive())
                .build();
    }
}