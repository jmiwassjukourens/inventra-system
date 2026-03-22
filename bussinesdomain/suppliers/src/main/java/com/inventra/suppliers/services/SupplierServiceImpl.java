package com.inventra.suppliers.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventra.suppliers.dtos.ProductDTO;
import com.inventra.suppliers.dtos.SupplierProductDTO;
import com.inventra.suppliers.dtos.SupplierRequestDTO;
import com.inventra.suppliers.dtos.SupplierResponseDTO;
import com.inventra.suppliers.exceptions.NotFoundException;
import com.inventra.suppliers.model.Supplier;
import com.inventra.suppliers.model.SupplierProduct;
import com.inventra.suppliers.repositories.SupplierProductRepository;
import com.inventra.suppliers.repositories.SupplierRepository;

import java.util.List;

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