package com.inventra.sales.configuration;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
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
    @LoadBalanced
    public WebClient inventoryWebClient(@Value("catalog") String baseUrl) {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    @LoadBalanced
    public WebClient accountsWebClient(@Value("accounts") String baseUrl) {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .baseUrl(baseUrl)
                .build();
    }
}
