package com.inventra.accounts.controllers;

import com.inventra.accounts.dtos.CustomerRequestDTO;
import com.inventra.accounts.dtos.CustomerResponseDTO;
import com.inventra.accounts.services.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer CRUD")
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "Create customer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(@Valid @RequestBody CustomerRequestDTO dto) {
        return ResponseEntity.ok(customerService.create(dto));
    }

    @Operation(summary = "List customers")
    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAll() {
        return ResponseEntity.ok(customerService.getAll());
    }

    @Operation(summary = "Get customer by id")
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getById(
            @Parameter(description = "Customer id") @PathVariable Long id) {
        return ResponseEntity.ok(customerService.getById(id));
    }

    @Operation(summary = "Update customer")
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDTO dto) {
        return ResponseEntity.ok(customerService.update(id, dto));
    }

    @Operation(summary = "Delete customer")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
