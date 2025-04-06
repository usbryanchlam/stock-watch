package dev.bryanlam.stockwatch.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import dev.bryanlam.stockwatch.dto.TokenResponse;
import dev.bryanlam.stockwatch.security.JwtTokenProvider;
import dev.bryanlam.stockwatch.security.UserPrincipal;
import dev.bryanlam.stockwatch.service.AuthService;


@Service
public class AuthServiceImpl implements AuthService {
    
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    public AuthServiceImpl (JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    public TokenResponse generateTokenResponse(Authentication authentication) {
        String jwt = tokenProvider.generateToken(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        return new TokenResponse(
            jwt,
            "Bearer",
            tokenProvider.getExpirationTime(),
            userPrincipal.getEmail(),
            userPrincipal.getName()
        );
    }
}
