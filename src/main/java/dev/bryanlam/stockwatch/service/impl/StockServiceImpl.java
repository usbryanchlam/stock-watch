package dev.bryanlam.stockwatch.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import dev.bryanlam.stockwatch.dto.CompanyProfileApiResponse;
import dev.bryanlam.stockwatch.dto.SearchStockApiResponse;
import dev.bryanlam.stockwatch.dto.SearchStockResultDTO;
import dev.bryanlam.stockwatch.dto.StockDataApiResponse;
import dev.bryanlam.stockwatch.dto.StockDataDTO;
import dev.bryanlam.stockwatch.dto.StockInfoApiResponse;
import dev.bryanlam.stockwatch.exception.ResourceNotFoundException;
import dev.bryanlam.stockwatch.model.StockData;
import dev.bryanlam.stockwatch.repository.StockDataRepository;
import dev.bryanlam.stockwatch.service.StockService;

@Service
public class StockServiceImpl implements StockService {

    private StockDataRepository stockDataRepository;

    private static ModelMapper modelMapper = new ModelMapper();
    
    @Value("${app.stockapi.key}")
    private String API_KEY;

    @Value("${app.stockapi.endpoint.quote}")
    private String QUOTE_ENDPOINT;

    @Value("${app.stockapi.endpoint.profile}")
    private String COMPANY_PROFILE_ENDPOINT;
    
    @Value("${app.stockapi.endpoint.search}")
    private String SEARCH_ENDPOINT;

    private RestTemplate restTemplate;

    @Autowired
    public StockServiceImpl (StockDataRepository stockDataRepository, RestTemplate restTemplate) {
        this.stockDataRepository = stockDataRepository;
        this.restTemplate = restTemplate;
    }
    
    @Override
    public StockDataDTO getStockData(String symbol) {
        // Check if we have recent data in the database
        Optional<StockData> existingData = stockDataRepository.findBySymbol(symbol);
        
        if (existingData.isPresent() && 
            existingData.get().getLastUpdated().plusMinutes(1).isAfter(LocalDateTime.now())) {
            // Data is less than 1 minutes old, return it
            return getStockDataDTO(existingData.get());
        }
        
        // Fetch fresh data from API
        StockData freshData = fetchStockDataFromApi(symbol);
        
        // Update existing record or create new one
        if (existingData.isPresent()) {
            StockData data = existingData.get();
            data.setCurrentPrice(freshData.getCurrentPrice());
            data.setPreviousClose(freshData.getPreviousClose());
            data.setPercentChange(freshData.getPercentChange());
            data.setLastUpdated(LocalDateTime.now());
            return getStockDataDTO(stockDataRepository.save(data));
        } else {
            freshData.setLastUpdated(LocalDateTime.now());
            CompanyProfileApiResponse companyProfile = fetchCompanyProfileFromApi(symbol);
            if (companyProfile != null)
                freshData.setCompanyName(companyProfile.getName());
            return getStockDataDTO(stockDataRepository.save(freshData));
        }
    }

    @Override
    public List<StockDataDTO> getListOfStockData(List<String> listOfSymbol) {
        List<StockDataDTO> resultList = new ArrayList<>();

        for (String symbol: listOfSymbol) {
            resultList.add(getStockData(symbol));
        }

        return resultList;
    }

    @Override
    public List<SearchStockResultDTO> searchStock(String queryText) {
        List<SearchStockResultDTO> resultList = new ArrayList<>();
        List<StockData> listOfStockData = fetchSearchStockFromApi(queryText);

        for (StockData stockData: listOfStockData) {
            SearchStockResultDTO searchStockResultDto = new SearchStockResultDTO();
            searchStockResultDto.setSymbol(stockData.getSymbol());
            searchStockResultDto.setName(stockData.getCompanyName());
            CompanyProfileApiResponse companyProfile = fetchCompanyProfileFromApi(stockData.getSymbol());
            if (companyProfile != null) {
                searchStockResultDto.setLogo(companyProfile.getLogo());
                searchStockResultDto.setIndustry(companyProfile.getFinnhubIndustry());
                resultList.add(searchStockResultDto);
            }
        }

        return resultList;
    }

    private List<StockData> fetchSearchStockFromApi(String queryText) {
        String url = SEARCH_ENDPOINT + queryText + API_KEY;

        SearchStockApiResponse response = restTemplate.getForObject(url, SearchStockApiResponse.class);

        List<StockData> stockDataList = null;

        if (response != null && response.getCount() > 0) {
            stockDataList = new ArrayList<>();
            for (StockInfoApiResponse item: response.getResult()) {
                if ("Common Stock".equals(item.getType())) {
                    StockData stockData = new StockData();
                    stockData.setCompanyName(item.getDescription());
                    stockData.setSymbol(item.getSymbol());
                    stockDataList.add(stockData);
                }
            }
        }
        else {
            throw new ResourceNotFoundException("SearchStock", "query", queryText);
        }

        return stockDataList;
    }
    
    private StockData fetchStockDataFromApi(String symbol) {
        // This is a placeholder. Implement actual API call based on your chosen provider
        String url = QUOTE_ENDPOINT + symbol + API_KEY;
        
        // Make API call using RestTemplate
        // This is simplified. In a real app, you would handle errors, parse JSON, etc.
        StockDataApiResponse response = restTemplate.getForObject(url, StockDataApiResponse.class);
        
        StockData data = null;
        if (response != null) {
            data = new StockData();
            data.setSymbol(symbol);
            data.setCurrentPrice(response.getC());
            data.setPreviousClose(response.getPc());
            data.setPercentChange(response.getDp());
        }
        else {
            throw new ResourceNotFoundException("StockData", "symbol", symbol);
        }
        
        return data;
    }

    private CompanyProfileApiResponse fetchCompanyProfileFromApi(String symbol) {
        String url = COMPANY_PROFILE_ENDPOINT + symbol + API_KEY;

        CompanyProfileApiResponse response = restTemplate.getForObject(url, CompanyProfileApiResponse.class);
        
        if (response != null && response.getName() != null) {
            return response;
        }
        else {
            return null;
        }
    }

    private StockDataDTO getStockDataDTO(StockData stockData) {
        return(modelMapper.map(stockData, StockDataDTO.class));
    }
}
