package com.inventra.suppliers.services;



import java.util.List;

import com.inventra.suppliers.dtos.ProductDTO;
import com.inventra.suppliers.dtos.ProductSupplierReportRowDTO;
import com.inventra.suppliers.dtos.SupplierProductDTO;
import com.inventra.suppliers.dtos.SupplierRequestDTO;
import com.inventra.suppliers.dtos.SupplierResponseDTO;

public interface SupplierService {

    SupplierResponseDTO create(SupplierRequestDTO dto);

    List<SupplierResponseDTO> getAll();

    SupplierResponseDTO getById(Long id);

    void delete(Long id);

    void addProductToSupplier(SupplierProductDTO dto);

    List<ProductDTO> getProductsBySupplier(Long supplierId);

    List<ProductSupplierReportRowDTO> getProductsWithSuppliersReport();
}