package com.inventra.sales.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.inventra.sales.dtos.StockMovementDTO;
import com.inventra.sales.exceptions.ExternalServiceException;

@Component
@RequiredArgsConstructor
public class InventoryClient {

    private final WebClient inventoryWebClient;

    public void createStockMovement(StockMovementDTO dto) {

        try {
            inventoryWebClient.post()
                    .uri("/api/stocks/movements")
                    .bodyValue(dto)
                    .retrieve()
                    .onStatus(status -> status.isError(), response ->
                            response.bodyToMono(String.class)
                                    .map(body -> new ExternalServiceException(body))
                    )
                    .toBodilessEntity()
                    .block();

        } catch (Exception e) {
            throw new ExternalServiceException("Inventory service unavailable");
        }
    }
}