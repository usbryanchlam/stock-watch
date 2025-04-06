package dev.bryanlam.stockwatch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    
    @Value("${app.jwt.secret}")
    private String jwtSecret;
    
    @Value("${app.jwt.expiration}")
    private int jwtExpirationInMs;
    
    @Value("${app.jwt.issuer}")
    private String issuer;

    public String getJwtSecret() {
        return jwtSecret;
    }

    public int getJwtExpirationInMs() {
        return jwtExpirationInMs;
    }
    
    public String getIssuer() {
        return issuer;
    }
}
