package dev.bryanlam.stockwatch.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockDataDTO {
    private String id;
    private String symbol;
    private String companyName;
    private Double currentPrice;
    private Double previousClose;
    private Double percentChange;
    private LocalDateTime lastUpdated;
}
