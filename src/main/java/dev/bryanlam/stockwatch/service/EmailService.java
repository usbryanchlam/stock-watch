package dev.bryanlam.stockwatch.service;

import dev.bryanlam.stockwatch.model.StockAlert;
import dev.bryanlam.stockwatch.model.StockData;


public interface EmailService {
    
    public void sendAlertNotification(StockAlert alert, StockData stockData);

}
