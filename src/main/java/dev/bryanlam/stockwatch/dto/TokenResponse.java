package dev.bryanlam.stockwatch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {

    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private String email;
    private String name;
    
}
