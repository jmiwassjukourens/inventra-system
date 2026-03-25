package com.inventra.suppliers.controllers;

import com.inventra.suppliers.dtos.*;
import com.inventra.suppliers.services.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.media.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@Tag(name = "Suppliers", description = "API para gestión de proveedores y sus productos")
public class SupplierController {

    private final SupplierService supplierService;

    @Operation(summary = "Crear un nuevo proveedor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Proveedor creado correctamente",
                    content = @Content(schema = @Schema(implementation = SupplierResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<SupplierResponseDTO> create(
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del proveedor",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SupplierRequestDTO.class))
            )
            SupplierRequestDTO dto) {

        return ResponseEntity.ok(supplierService.create(dto));
    }

    @Operation(summary = "Reporte: productos con datos de proveedor (enriquecido desde catálogo)")
    @GetMapping("/report/products")
    public ResponseEntity<List<ProductSupplierReportRowDTO>> productsWithSuppliersReport() {
        return ResponseEntity.ok(supplierService.getProductsWithSuppliersReport());
    }

    @Operation(summary = "Obtener todos los proveedores")
    @ApiResponse(responseCode = "200", description = "Lista de proveedores",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = SupplierResponseDTO.class))))
    @GetMapping
    public ResponseEntity<List<SupplierResponseDTO>> getAll() {
        return ResponseEntity.ok(supplierService.getAll());
    }

    @Operation(summary = "Obtener proveedor por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Proveedor encontrado",
                    content = @Content(schema = @Schema(implementation = SupplierResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> getById(
            @Parameter(description = "ID del proveedor", example = "1")
            @PathVariable Long id) {

        return ResponseEntity.ok(supplierService.getById(id));
    }

    @Operation(summary = "Eliminar proveedor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Proveedor eliminado"),
            @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del proveedor", example = "1")
            @PathVariable Long id) {

        supplierService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Asignar un producto a un proveedor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relación creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping("/products")
    public ResponseEntity<Void> addProduct(
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Relación proveedor-producto",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SupplierProductDTO.class))
            )
            SupplierProductDTO dto) {
        supplierService.addProductToSupplier(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Obtener productos de un proveedor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos del proveedor",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    })
    @GetMapping("/{id}/products")
    public ResponseEntity<List<ProductDTO>> getProducts(
            @Parameter(description = "ID del proveedor", example = "1")
            @PathVariable Long id) {

        return ResponseEntity.ok(supplierService.getProductsBySupplier(id));
    }
}