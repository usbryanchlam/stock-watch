package dev.bryanlam.stockwatch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockDataApiResponse {
    private Double c;   // Current price
    private Double d;   // Change
    private Double dp;  // Percent change
    private Double h;   // High price of the day
    private Double l;   // Low price of the day
    private Double o;   // Open price of the day
    private Double pc;  // Previous close price
}
