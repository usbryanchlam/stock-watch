package dev.bryanlam.stockwatch.service;

import java.util.List;

import dev.bryanlam.stockwatch.dto.SearchStockResultDTO;
import dev.bryanlam.stockwatch.dto.StockDataDTO;

public interface StockService {
 
    public StockDataDTO getStockData(String symbol);
    public List<StockDataDTO> getListOfStockData(List<String> listOfSymbol);
    public List<SearchStockResultDTO> searchStock(String queryText);

}
