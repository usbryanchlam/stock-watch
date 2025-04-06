package dev.bryanlam.stockwatch.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.HashSet;
import java.util.Set;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String email;
    private String name;
    private String picture;
    private String provider; // "google", "facebook", "apple"
    private String providerId;
    private String role;
    private Set<String> watchedStocks = new HashSet<>();
}
