package com.inventra.purchases.services;

import com.inventra.purchases.dtos.StockMovementDTO;
import com.inventra.purchases.exceptions.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
public class InventoryClient {

    
     private final WebClient.Builder webClientBuilder;

    public void createStockMovement(StockMovementDTO dto) {
        try {

                webClientBuilder
                        .baseUrl("http://catalog")
                        .build()
                        .post()
                        .uri("/api/stocks/movements")
                        .bodyValue(dto)
                        .retrieve()
                        .toBodilessEntity()
                        .block();
        } catch (WebClientResponseException e) {
            throw new ExternalServiceException("Catalog error: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
        } catch (ExternalServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ExternalServiceException("Catalog unavailable: " + e.getMessage());
        }
    }
}
