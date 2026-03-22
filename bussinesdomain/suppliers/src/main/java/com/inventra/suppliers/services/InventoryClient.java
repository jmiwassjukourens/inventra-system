package com.inventra.suppliers.services;

import com.inventra.suppliers.dtos.ProductDTO;
import com.inventra.suppliers.exceptions.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class InventoryClient {

    private final WebClient inventoryWebClient;

    public List<ProductDTO> getAllProducts() {
        try {
            return inventoryWebClient.get()
                    .uri("/api/products")
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response ->
                            response.bodyToMono(String.class)
                                    .defaultIfEmpty("")
                                    .map(body -> new ExternalServiceException(
                                            "Catalog error: " + response.statusCode() + " " + body)))
                    .bodyToMono(new ParameterizedTypeReference<List<ProductDTO>>() {
                    })
                    .block();
        } catch (WebClientResponseException e) {
            throw new ExternalServiceException("Catalog error: " + e.getStatusCode());
        } catch (ExternalServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ExternalServiceException("Catalog unavailable: " + e.getMessage());
        }
    }

    public List<ProductDTO> getProductsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        Set<Long> want = new HashSet<>(ids);
        return getAllProducts().stream()
                .filter(p -> p.getId() != null && want.contains(p.getId()))
                .toList();
    }
}
