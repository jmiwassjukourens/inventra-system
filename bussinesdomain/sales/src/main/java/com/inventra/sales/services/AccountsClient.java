package com.inventra.sales.services;

import com.inventra.sales.dtos.SaleDebtRequestDTO;
import com.inventra.sales.exceptions.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class AccountsClient {

    private final WebClient webClient;

    public AccountsClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://accounts").build();
    }

    public void registerSaleDebt(SaleDebtRequestDTO dto) {

        try {

            webClient.post()
                    .uri("/api/internal/account-movements/sale-debt")
                    .bodyValue(dto)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class)
                                    .defaultIfEmpty("")
                                    .map(body -> new ExternalServiceException(
                                            "Accounts error: " + response.statusCode() + " " + body)))
                    .toBodilessEntity()
                    .block();

        } catch (WebClientResponseException e) {
            throw new ExternalServiceException(
                    "Accounts error: " + e.getStatusCode() + " " + e.getResponseBodyAsString());

        } catch (Exception e) {
            throw new ExternalServiceException("Accounts unavailable" + e.getMessage());
        }
    }
}