package dev.bryanlam.stockwatch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.bryanlam.stockwatch.dto.ApiResponse;
import dev.bryanlam.stockwatch.dto.SearchStockResultDTO;
import dev.bryanlam.stockwatch.dto.StockAlertDTO;
import dev.bryanlam.stockwatch.dto.StockDataDTO;
import dev.bryanlam.stockwatch.dto.UserDTO;
import dev.bryanlam.stockwatch.exception.ResourceNotFoundException;
import dev.bryanlam.stockwatch.security.JwtTokenProvider;
import dev.bryanlam.stockwatch.service.impl.AlertServiceImpl;
import dev.bryanlam.stockwatch.service.impl.StockServiceImpl;
import dev.bryanlam.stockwatch.service.impl.UserServiceImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@CrossOrigin(origins = "http://localhost:5173")     // Change it to production site later
@RequestMapping("/api")
public class ResourceController {

    private UserServiceImpl userService;

    private StockServiceImpl stockService;

    private AlertServiceImpl alertService;

    private JwtTokenProvider tokenProvider;

    @Autowired
    public ResourceController(UserServiceImpl userService, StockServiceImpl stockService, AlertServiceImpl alertService, JwtTokenProvider tokenProvider) {
        this.userService = userService;
        this.stockService = stockService;
        this.alertService = alertService;
        this.tokenProvider = tokenProvider;
    }

    @GetMapping("/users/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser() {
        UserDTO userDto = userService.getCurrentUser();
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", userDto.getId());
        userInfo.put("name", userDto.getName());
        userInfo.put("email", userDto.getEmail());
        userInfo.put("picture", userDto.getPicture());
        userInfo.put("watchedStocks", new ArrayList<>(userDto.getWatchedStocks()));
        
        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }

    @DeleteMapping("/users/me/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteByUserIdAndEmail(@PathVariable String id, @RequestBody UserDTO inUserDto) {
        UserDTO userDto = userService.getCurrentUser();

        if (userDto.getId().equals(id) && userDto.getEmail().equals(inUserDto.getEmail())) {
            alertService.deleteByUserId(userDto.getId());
            userService.delete(userDto);
            ResponseCookie jwtCookie = tokenProvider.invalidateJWTCookie();

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(ApiResponse.success(null));
        }
        else {
            throw new ResourceNotFoundException("Not authorized to delete user!");
        }

    }

    @GetMapping("/stocks")
    public ResponseEntity<ApiResponse<List<SearchStockResultDTO>>> searchStock(@RequestParam("query") String queryText) {
        List<SearchStockResultDTO> listOfSearchStockResultDto = stockService.searchStock(queryText);

        return ResponseEntity.ok(ApiResponse.success(listOfSearchStockResultDto));
    }

    @GetMapping("/stocks/{symbol}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStockData(@PathVariable String symbol) {
        StockDataDTO stockDataDto = stockService.getStockData(symbol);

        Map<String, Object> stockData = new HashMap<>();
        stockData.put("symbol", stockDataDto.getSymbol());
        stockData.put("companyName", stockDataDto.getCompanyName());
        stockData.put("currentPrice", stockDataDto.getCurrentPrice());
        stockData.put("previousClose", stockDataDto.getPreviousClose());
        stockData.put("percentChange", stockDataDto.getPercentChange());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime lastUpdated = stockDataDto.getLastUpdated();
        String formattedDateTime = lastUpdated.format(formatter);
        stockData.put("lastUpdated", formattedDateTime);

        return ResponseEntity.ok(ApiResponse.success(stockData));
    }

    @GetMapping("/stock-alerts/{symbol}")
    public ResponseEntity<ApiResponse<StockAlertDTO>> getAlertBySymbol(@PathVariable String symbol) {
        UserDTO userDto = userService.getCurrentUser();

        StockAlertDTO stockAlertDto = alertService.findByUserIdAndStockSymbol(userDto.getId(), symbol);
        
        return ResponseEntity.ok(ApiResponse.success(stockAlertDto));
    }

    @GetMapping("/users/me/watch-list")
    public ResponseEntity<ApiResponse<List<StockDataDTO>>> getWatchedStocks() {
        UserDTO userDto = userService.getCurrentUser();
        List<String> symbols = new ArrayList<>(userDto.getWatchedStocks());
        List<StockDataDTO> listOfStockDataDto = stockService.getListOfStockData(symbols);

        return ResponseEntity.ok(ApiResponse.success(listOfStockDataDto));
    }
    

    @PostMapping("/users/me/watch-list")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addToWatchList(@RequestBody StockDataDTO stockDataDto) {
        UserDTO userDto = userService.getCurrentUser();

        String symbol = stockDataDto.getSymbol();

        if (!"".equals(symbol)) {
            userDto.getWatchedStocks().add(symbol);
            userService.save(userDto);
            return getStockData(symbol);
        }
        else {
            throw new ResourceNotFoundException("symbol cannot be empty!");
        }
    }

    @DeleteMapping("/users/me/watch-list/{symbol}")
    public ResponseEntity<ApiResponse<Void>> removeFromWatchedStocks(@PathVariable String symbol) {
        UserDTO userDto = userService.getCurrentUser();
        Set<String> symbols = userDto.getWatchedStocks();
        symbols.removeIf(s -> s.equals(symbol));
        userDto.setWatchedStocks(symbols);
        userService.save(userDto);

        StockAlertDTO stockAlertDTO = alertService.findByUserIdAndStockSymbol(userDto.getId(), symbol);
        if (stockAlertDTO != null) {
            alertService.deleteAlert(stockAlertDTO.getId(), userDto.getId());
        }

        return ResponseEntity.ok(ApiResponse.success(null, "The stock is removed from watch list successfully."));
    }

    @PostMapping("/stock-alerts")
    public ResponseEntity<ApiResponse<StockAlertDTO>> createStockAlert(@RequestBody StockAlertDTO stockAlertDto) {
        UserDTO currentUserDto = userService.getCurrentUser();

        stockAlertDto.setUserId(currentUserDto.getId());

        StockAlertDTO savedStockAlertDto = alertService.createAlert(stockAlertDto);

        return ResponseEntity.ok(ApiResponse.success(savedStockAlertDto));
    }

    @PutMapping("/stock-alerts/{id}")
    public ResponseEntity<ApiResponse<StockAlertDTO>> updateStockAlert(@PathVariable String id, @RequestBody StockAlertDTO stockAlertDto) {
        UserDTO currentUserDto = userService.getCurrentUser();

        stockAlertDto.setId(id);
        stockAlertDto.setUserId(currentUserDto.getId());
        stockAlertDto.setTriggeredAt(null);

        StockAlertDTO updatedStockAlertDto = alertService.updateAlert(stockAlertDto);

        return ResponseEntity.ok(ApiResponse.success(updatedStockAlertDto));
    }

}
