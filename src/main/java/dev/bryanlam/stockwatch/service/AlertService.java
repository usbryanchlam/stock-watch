package dev.bryanlam.stockwatch.service;

import java.util.List;

import dev.bryanlam.stockwatch.dto.StockAlertDTO;

public interface AlertService {

    public StockAlertDTO createAlert(StockAlertDTO stockAlertDto);
    
    public StockAlertDTO updateAlert(StockAlertDTO stockAlertDto);
    
    public StockAlertDTO findByUserIdAndStockSymbol(String userId, String stockSymbol);

    public List<StockAlertDTO> getUserAlerts(String userId);
    
    public void deleteAlert(String alertId, String userId);
    
    public void deactivateAlert(String alertId, String userId);
    
    public void checkAlerts();

}
