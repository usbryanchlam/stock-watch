package dev.bryanlam.stockwatch.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import dev.bryanlam.stockwatch.dto.ApiResponse;
import dev.bryanlam.stockwatch.dto.TokenResponse;
import dev.bryanlam.stockwatch.service.impl.AuthServiceImpl;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthServiceImpl authService;

    @GetMapping("/oauth2-success")
    // public ResponseEntity<ApiResponse<TokenResponse>> handleOAuth2Success(
    public ResponseEntity<Void> handleOAuth2Success(
            @RequestParam String token) {
        // This endpoint serves as a redirect target after OAuth2 authentication
        // The token is passed as a query parameter by GoogleOAuth2SuccessHandler
        String redirectUrl = UriComponentsBuilder.fromUriString("http://localhost:5173/login/callback")
                .queryParam("token", token)
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", redirectUrl);
        
        
        // return new ResponseEntity<>(ApiResponse.success(
        //     new TokenResponse(
        //         token,
        //         "Bearer",
        //         3600000L, // 1 hour - this should match your JWT configuration
        //         null,     // These will be extracted from the token by the client
        //         null      // These will be extracted from the token by the client
        //     ),
        //     "Authentication successful"
        // ), headers, HttpStatus.FOUND);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/token")
    public ResponseEntity<ApiResponse<TokenResponse>> getToken(
            @AuthenticationPrincipal OAuth2AuthenticationToken authentication) {
        
        TokenResponse tokenResponse = authService.generateTokenResponse(authentication);
        return ResponseEntity.ok(ApiResponse.success(tokenResponse, "Token generated successfully"));
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Void>> validateToken() {
        // If this point is reached, the token is valid (checked by JwtAuthenticationFilter)
        return ResponseEntity.ok(ApiResponse.success(null, "Token is valid"));
    }
}
