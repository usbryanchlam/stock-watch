package dev.bryanlam.stockwatch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockInfoApiResponse {
    private String description;
    private String displaySymbol;
    private String symbol;
    private String type;
}
