package dev.bryanlam.stockwatch.service;

import org.springframework.security.core.Authentication;

import dev.bryanlam.stockwatch.dto.TokenResponse;

public interface AuthService {
    public TokenResponse generateTokenResponse(Authentication authentication);
}
