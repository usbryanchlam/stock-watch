package dev.bryanlam.stockwatch.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchStockApiResponse {
    private Integer count;
    private List<StockInfoApiResponse> result;
}


