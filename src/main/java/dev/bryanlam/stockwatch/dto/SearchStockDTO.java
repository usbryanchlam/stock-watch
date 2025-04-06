package dev.bryanlam.stockwatch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchStockDTO {
    private String companyName;
    private String symbol;
}
