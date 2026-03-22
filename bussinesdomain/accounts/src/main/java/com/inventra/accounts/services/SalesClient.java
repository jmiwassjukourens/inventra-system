package com.inventra.accounts.services;

import com.inventra.accounts.dtos.SaleSummaryDTO;
import com.inventra.accounts.exceptions.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SalesClient {

    private final WebClient salesWebClient;

    public List<SaleSummaryDTO> findSalesByCustomer(Long customerId) {
        try {
            return salesWebClient.get()
                    .uri("/api/sales/by-customer/{customerId}", customerId)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response ->
                            response.bodyToMono(String.class)
                                    .defaultIfEmpty("")
                                    .map(body -> new ExternalServiceException(
                                            "Sales service error: " + response.statusCode() + " " + body)))
                    .bodyToMono(new ParameterizedTypeReference<List<SaleSummaryDTO>>() {
                    })
                    .block();
        } catch (WebClientResponseException e) {
            throw new ExternalServiceException("Sales service error: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
        } catch (ExternalServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ExternalServiceException("Sales service unavailable: " + e.getMessage());
        }
    }

    public void markSalePaid(Long saleId) {
        try {
            salesWebClient.post()
                    .uri("/api/internal/sales/{id}/mark-paid", saleId)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response ->
                            response.bodyToMono(String.class)
                                    .defaultIfEmpty("")
                                    .map(body -> new ExternalServiceException(
                                            "Sales service error: " + response.statusCode() + " " + body)))
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            throw new ExternalServiceException("Sales service error: " + e.getStatusCode());
        } catch (ExternalServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ExternalServiceException("Sales service unavailable: " + e.getMessage());
        }
    }
}
