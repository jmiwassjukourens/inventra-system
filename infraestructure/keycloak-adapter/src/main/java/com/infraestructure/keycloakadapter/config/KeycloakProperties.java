package com.infraestructure.keycloakadapter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak")
public record KeycloakProperties(
        String serverUrl,
        String realm,
        String clientId,
        String clientSecret
) {
    public String tokenEndpoint() {
        return "%s/realms/%s/protocol/openid-connect/token".formatted(serverUrl, realm);
    }

    public String logoutEndpoint() {
        return "%s/realms/%s/protocol/openid-connect/logout".formatted(serverUrl, realm);
    }

    public String userinfoEndpoint() {
        return "%s/realms/%s/protocol/openid-connect/userinfo".formatted(serverUrl, realm);
    }
}
