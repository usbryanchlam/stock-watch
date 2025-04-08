package dev.bryanlam.stockwatch.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private int jwtExpirationInMs;

    @Value("${app.jwt.issuer}")
    private String issuer;

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .claim("sub", userPrincipal.getId())
                .claim("iss", (issuer))
                .claim("email", userPrincipal.getEmail())
                .claim("name", userPrincipal.getName())
                .claim("iat",(new Date()))
                .claim("exp", expiryDate)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public String getUserIdFromJWT(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }
    
    public String getEmailFromJWT(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("email", String.class);
    }

    public boolean validateToken(String authToken) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Jwts.parser().verifyWith(key).build().parseSignedClaims(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            logger.error("Invalid JWT token: {}", ex.getMessage());
        }
        return false;
    }
    
    public long getExpirationTime() {
        return jwtExpirationInMs;
    }

    public ResponseCookie createJWTCookie(String token) {
        return ResponseCookie.from("jwt", token)
        .httpOnly(true)
        // .secure(true)                // TODO: Set https for production
        .path("/")
        .maxAge(jwtExpirationInMs / 1000)
        .sameSite("Lax")       // Use Lax for redirects to external domains
        .build();
    }

    public ResponseCookie invalidateJWTCookie() {
        return ResponseCookie.from("jwt", "")
                .httpOnly(true)
                // .secure(true)        // TODO: Set https for production
                .path("/")
                .maxAge(0)  // Expire immediately
                .sameSite("Lax")
                .build();
    }
}
