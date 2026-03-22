package com.inventra.accounts.services;

import com.inventra.accounts.dtos.CustomerRequestDTO;
import com.inventra.accounts.dtos.CustomerResponseDTO;

import java.util.List;

public interface CustomerService {

    CustomerResponseDTO create(CustomerRequestDTO dto);

    CustomerResponseDTO update(Long id, CustomerRequestDTO dto);

    CustomerResponseDTO getById(Long id);

    List<CustomerResponseDTO> getAll();

    void delete(Long id);
}
