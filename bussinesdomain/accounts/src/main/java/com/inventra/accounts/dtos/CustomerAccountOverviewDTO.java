package com.inventra.accounts.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CustomerAccountOverviewDTO {
    private AccountResponseDTO account;
    private List<SaleSummaryDTO> recentSales;
}
