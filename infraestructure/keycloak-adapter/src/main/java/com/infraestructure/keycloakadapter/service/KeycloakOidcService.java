package com.infraestructure.keycloakadapter.service;

import com.infraestructure.keycloakadapter.config.KeycloakProperties;
import com.infraestructure.keycloakadapter.dto.LoginRequest;
import com.infraestructure.keycloakadapter.dto.LogoutRequest;
import com.infraestructure.keycloakadapter.dto.RefreshTokenRequest;
import com.infraestructure.keycloakadapter.dto.TokenResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class KeycloakOidcService {

    private final RestClient restClient;
    private final KeycloakProperties keycloak;

    public KeycloakOidcService(RestClient keycloakRestClient, KeycloakProperties keycloak) {
        this.restClient = keycloakRestClient;
        this.keycloak = keycloak;
    }

    public TokenResponse login(LoginRequest request) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", keycloak.clientId());
        addClientSecretIfPresent(form);
        form.add("username", request.username());
        form.add("password", request.password());
        return postTokenForm(form);
    }

    public TokenResponse refresh(RefreshTokenRequest request) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "refresh_token");
        form.add("client_id", keycloak.clientId());
        addClientSecretIfPresent(form);
        form.add("refresh_token", request.refreshToken());
        return postTokenForm(form);
    }

    public void logout(LogoutRequest request) {
        if (!StringUtils.hasText(request.refreshToken())) {
            throw new IllegalArgumentException("refresh_token is required for logout");
        }
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", keycloak.clientId());
        addClientSecretIfPresent(form);
        form.add("refresh_token", request.refreshToken());
        try {
            restClient.post()
                    .uri(keycloak.logoutEndpoint())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException ex) {
            throw new KeycloakAdapterException("Keycloak logout failed: HTTP " + ex.getStatusCode().value(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> userinfo(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header must be a Bearer token");
        }
        try {
            return restClient.get()
                    .uri(keycloak.userinfoEndpoint())
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                    .retrieve()
                    .body(Map.class);
        } catch (RestClientResponseException ex) {
            throw new KeycloakAdapterException("Keycloak userinfo failed: HTTP " + ex.getStatusCode().value(), ex);
        }
    }

    public List<String> realmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null) {
            return Collections.emptyList();
        }
        Object roles = realmAccess.get("roles");
        if (roles instanceof List<?> list) {
            List<String> out = new ArrayList<>();
            for (Object r : list) {
                if (r != null) {
                    out.add(r.toString());
                }
            }
            return List.copyOf(out);
        }
        return Collections.emptyList();
    }

    private TokenResponse postTokenForm(MultiValueMap<String, String> form) {
        try {
            return restClient.post()
                    .uri(keycloak.tokenEndpoint())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(TokenResponse.class);
        } catch (RestClientResponseException ex) {
            throw new KeycloakAdapterException("Keycloak token request failed: HTTP " + ex.getStatusCode().value(), ex);
        }
    }

    private void addClientSecretIfPresent(MultiValueMap<String, String> form) {
        if (StringUtils.hasText(keycloak.clientSecret())) {
            form.add("client_secret", keycloak.clientSecret());
        }
    }
}
