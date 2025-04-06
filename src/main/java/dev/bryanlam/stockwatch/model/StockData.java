package dev.bryanlam.stockwatch.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "stockData")
public class StockData {
    @Id
    private String id;
    private String symbol;
    private String companyName;
    private Double currentPrice;
    private Double previousClose;
    private Double percentChange;
    private LocalDateTime lastUpdated = LocalDateTime.now();
}
