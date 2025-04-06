package dev.bryanlam.stockwatch.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "alerts")
public class StockAlert {
    @Id
    private String id;
    private String userId;
    private String stockSymbol;
    private Double targetPrice;
    private AlertCondition condition; // ABOVE, BELOW
    private Boolean isActive = true;
    private Boolean isTriggered = false;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime triggeredAt;
}