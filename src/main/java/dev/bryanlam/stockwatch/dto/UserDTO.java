package dev.bryanlam.stockwatch.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String id;
    private String email;
    private String name;
    private String picture;
    private String provider; // "google", "facebook", "apple"
    private String providerId;
    private String role;
    private Set<String> watchedStocks = new HashSet<>();
}
