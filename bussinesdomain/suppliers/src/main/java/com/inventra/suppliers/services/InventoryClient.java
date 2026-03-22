package com.inventra.suppliers.services;
import com.inventra.suppliers.dtos.ProductDTO;

import java.util.List;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class InventoryClient {

    private final WebClient inventoryWebClient;

    public List<ProductDTO> getProductsByIds(List<Long> ids) {

    return inventoryWebClient.post()
            .uri("/api/products/batch")
            .bodyValue(ids)
            .retrieve()
            .onStatus(status -> status.isError(), response ->
                    response.bodyToMono(String.class)
                            .map(body -> new RuntimeException("Error calling Inventory: " + body))
            )
            .bodyToFlux(ProductDTO.class)
            .collectList()
            .block();
    }


}