package dev.bryanlam.stockwatch.security;

import java.util.UUID;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CsrfTokenProvider {
    
    public ResponseCookie createCsrfTokenAndCookie() {
        // Generate CSRF token
        String csrfToken = UUID.randomUUID().toString();
        return ResponseCookie.from("XSRF-TOKEN", csrfToken)
            .httpOnly(false) // must be readable by frontend
            // .secure(true)
            .path("/")
            .sameSite("Lax")
            .build();
    }

    public ResponseCookie invalidateCsrfTokenCookie() {
        return ResponseCookie.from("XSRF-TOKEN", "")
            .httpOnly(false)
            // .secure(true)
            .path("/")
            .maxAge(0)
            .sameSite("Lax")
            .build();
    }
}
