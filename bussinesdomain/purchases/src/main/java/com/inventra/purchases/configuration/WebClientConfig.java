package com.inventra.purchases.configuration;

import io.netty.channel.ChannelOption;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    /**
     * {@code @LoadBalanced} must be on {@link WebClient.Builder}. Building {@link WebClient} from a plain
     * {@code WebClient.builder()} skips the load-balancer filter and hostnames like {@code catalog} are sent to DNS.
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient inventoryWebClient(@LoadBalanced WebClient.Builder builder) {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(15))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5_000);
        return builder.clone()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl("http://catalog")
                .build();
    }
}