package com.inventra.sales.configuration;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    private static HttpClient httpClient() {
        return HttpClient.create()
                .responseTimeout(Duration.ofSeconds(15))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5_000);
    }

    @Bean
    public WebClient inventoryWebClient(@Value("${inventra.catalog.base-url}") String baseUrl) {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    public WebClient accountsWebClient(@Value("${inventra.accounts.base-url}") String baseUrl) {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .baseUrl(baseUrl)
                .build();
    }
}
