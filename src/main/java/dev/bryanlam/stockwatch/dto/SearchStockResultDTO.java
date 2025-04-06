package dev.bryanlam.stockwatch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchStockResultDTO {
    private String name;        // Company name
    private String symbol;      // Company symbol/ticker as used on the listed exchange
    private String logo;        // Logo image
    private String industry; // Finnhub industry classification

}
