package com.infraestructure.keycloakadapter.web;

import com.infraestructure.keycloakadapter.dto.LoginRequest;
import com.infraestructure.keycloakadapter.dto.LogoutRequest;
import com.infraestructure.keycloakadapter.dto.RefreshTokenRequest;
import com.infraestructure.keycloakadapter.dto.TokenResponse;
import com.infraestructure.keycloakadapter.service.KeycloakOidcService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final KeycloakOidcService keycloakOidcService;

    public AuthController(KeycloakOidcService keycloakOidcService) {
        this.keycloakOidcService = keycloakOidcService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(keycloakOidcService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(keycloakOidcService.refresh(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody(required = false) LogoutRequest request) {
        if (request == null) {
            request = new LogoutRequest(null);
        }
        keycloakOidcService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/userinfo")
    public ResponseEntity<Map<String, Object>> userinfo(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        return ResponseEntity.ok(keycloakOidcService.userinfo(authorization));
    }

    @GetMapping("/roles")
    public ResponseEntity<List<String>> roles(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(keycloakOidcService.realmRoles(jwt));
    }
}
