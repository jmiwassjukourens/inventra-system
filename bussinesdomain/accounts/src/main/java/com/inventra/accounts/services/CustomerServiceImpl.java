package com.inventra.accounts.services;

import com.inventra.accounts.dtos.CustomerRequestDTO;
import com.inventra.accounts.dtos.CustomerResponseDTO;
import com.inventra.accounts.exceptions.NotFoundException;
import com.inventra.accounts.model.Customer;
import com.inventra.accounts.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public CustomerResponseDTO create(CustomerRequestDTO dto) {
        Customer c = Customer.builder()
                .name(dto.getName())
                .taxId(dto.getTaxId())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .active(dto.getActive() != null ? dto.getActive() : Boolean.TRUE)
                .build();
        return map(customerRepository.save(c));
    }

    @Override
    @Transactional
    public CustomerResponseDTO update(Long id, CustomerRequestDTO dto) {
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
        c.setName(dto.getName());
        c.setTaxId(dto.getTaxId());
        c.setEmail(dto.getEmail());
        c.setPhone(dto.getPhone());
        if (dto.getActive() != null) {
            c.setActive(dto.getActive());
        }
        return map(customerRepository.save(c));
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDTO getById(Long id) {
        return customerRepository.findById(id)
                .map(this::map)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getAll() {
        return customerRepository.findAll().stream().map(this::map).toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new NotFoundException("Customer not found");
        }
        customerRepository.deleteById(id);
    }

    private CustomerResponseDTO map(Customer c) {
        return CustomerResponseDTO.builder()
                .id(c.getId())
                .name(c.getName())
                .taxId(c.getTaxId())
                .email(c.getEmail())
                .phone(c.getPhone())
                .active(c.getActive())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
