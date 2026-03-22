package com.inventra.sales.services;

import com.inventra.sales.dtos.StockMovementDTO;
import com.inventra.sales.exceptions.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response ->
                            response.bodyToMono(String.class)
                                    .defaultIfEmpty("")
                                    .map(body -> new ExternalServiceException(
                                            "Catalog error: " + response.statusCode() + " " + body)))
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
