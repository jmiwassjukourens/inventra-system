package com.inventra.accounts.services;

import com.inventra.accounts.dtos.SaleSummaryDTO;
import com.inventra.accounts.exceptions.ExternalServiceException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Component
public class SalesClient {

    private final WebClient webClient;

    public SalesClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://sales").build();
    }

    public List<SaleSummaryDTO> findSalesByCustomer(Long customerId) {

        try {

            return webClient.get()
                    .uri("/api/sales/by-customer/{customerId}", customerId)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class)
                                    .defaultIfEmpty("")
                                    .map(body -> new ExternalServiceException(
                                            "Sales error: " + response.statusCode() + " " + body)))
                    .bodyToMono(new ParameterizedTypeReference<List<SaleSummaryDTO>>() {})
                    .block();

        } catch (WebClientResponseException e) {
            throw new ExternalServiceException(
                    "Sales error: " + e.getStatusCode() + " " + e.getResponseBodyAsString());

        } catch (Exception e) {
            throw new ExternalServiceException("Sales unavailable" + e.getMessage());
        }
    }

    public void markSalePaid(Long saleId) {

        try {

            webClient.post()
                    .uri("/api/internal/sales/{id}/mark-paid", saleId)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class)
                                    .defaultIfEmpty("")
                                    .map(body -> new ExternalServiceException(
                                            "Sales error: " + response.statusCode() + " " + body)))
                    .toBodilessEntity()
                    .block();

        } catch (WebClientResponseException e) {
            throw new ExternalServiceException("Sales error: " + e.getStatusCode());

        } catch (Exception e) {
            throw new ExternalServiceException("Sales unavailable " + e.getMessage());
        }
    }
}