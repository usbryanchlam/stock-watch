package dev.bryanlam.stockwatch.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import dev.bryanlam.stockwatch.dto.StockAlertDTO;
import dev.bryanlam.stockwatch.dto.StockDataDTO;
import dev.bryanlam.stockwatch.model.AlertCondition;
import dev.bryanlam.stockwatch.model.StockAlert;
import dev.bryanlam.stockwatch.model.StockData;
import dev.bryanlam.stockwatch.repository.StockAlertRepository;
import dev.bryanlam.stockwatch.service.AlertService;
import dev.bryanlam.stockwatch.service.EmailService;
import dev.bryanlam.stockwatch.service.StockService;

@Service
public class AlertServiceImpl implements AlertService {
    
    private StockAlertRepository stockAlertRepository;
    
    private StockService stockService;
    
    private EmailService emailService;

    private static ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public AlertServiceImpl(StockAlertRepository stockAlertRepository, StockService stockService, EmailService emailService) {
        this.stockAlertRepository = stockAlertRepository;
        this.stockService = stockService;
        this.emailService = emailService;
    }
    
    @Override
    public StockAlertDTO createAlert(StockAlertDTO stockAlertDto) {     
        return getStockAlertDTO(stockAlertRepository.save(getStockAlertEntity(stockAlertDto)));
    }

    @Override
    public StockAlertDTO updateAlert(StockAlertDTO stockAlertDto) {
        stockAlertRepository.findById(stockAlertDto.getId())
            .orElseThrow(() -> new RuntimeException("Alert not found"));

        return getStockAlertDTO(stockAlertRepository.save(getStockAlertEntity(stockAlertDto)));
    }

    @Override
    public StockAlertDTO findByUserIdAndStockSymbol(String userId, String stockSymbol) {
        Optional<StockAlert> stockAlert = stockAlertRepository.findByUserIdAndStockSymbol(userId, stockSymbol);

        if (stockAlert.isPresent()) {
            return getStockAlertDTO(stockAlert.get());
        }
        else
            return null;
    }
    
    @Override
    public List<StockAlertDTO> getUserAlerts(String userId) {
        return getListOfStockAlertDTO(stockAlertRepository.findByUserIdAndIsActiveTrue(userId));
    }

    @Override
    public void deleteAlert(String alertId, String userId) {
        StockAlert alert = stockAlertRepository.findById(alertId)
            .orElseThrow(() -> new RuntimeException("Alert not found"));
            
        if (!alert.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to alert");
        }
        
        stockAlertRepository.delete(alert);
    }

    @Override
    public void deactivateAlert(String alertId, String userId) {
        StockAlert alert = stockAlertRepository.findById(alertId)
            .orElseThrow(() -> new RuntimeException("Alert not found"));
            
        if (!alert.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to alert");
        }
        
        alert.setIsActive(false);
        stockAlertRepository.save(alert);
    }
    
    @Override
    // Run every 5 minutes
    @Scheduled(fixedRate = 300000)
    public void checkAlerts() {
        List<StockAlert> activeAlerts = stockAlertRepository.findByIsActiveTrueAndIsTriggeredFalse();
        
        for (StockAlert alert : activeAlerts) {
            StockDataDTO stockDataDto = stockService.getStockData(alert.getStockSymbol());
            boolean isTriggered = false;
            
            if ((alert.getCondition() == AlertCondition.ABOVE && 
                stockDataDto.getCurrentPrice() >= alert.getTargetPrice()) || 
                (alert.getCondition() == AlertCondition.BELOW && 
                stockDataDto.getCurrentPrice() <= alert.getTargetPrice())) {
                isTriggered = true;
            } 
            
            if (isTriggered) {
                // Mark as triggered
                alert.setIsTriggered(true);
                alert.setTriggeredAt(LocalDateTime.now());
                
                
                // Send notification
                emailService.sendAlertNotification(alert, getStockDataEntity(stockDataDto));

                stockAlertRepository.save(alert);
            }
        }
    }

    private StockAlert getStockAlertEntity(StockAlertDTO stockAlertDto) {
        return(modelMapper.map(stockAlertDto, StockAlert.class));
    }

    private StockAlertDTO getStockAlertDTO(StockAlert stockAlert) {
        return(modelMapper.map(stockAlert, StockAlertDTO.class));
    }

    private List<StockAlertDTO> getListOfStockAlertDTO(List<StockAlert> listOfStockAlert) {
        List<StockAlertDTO> resultList = new ArrayList<>();

        for (StockAlert stockAlert: listOfStockAlert) {
            resultList.add(getStockAlertDTO(stockAlert));
        }
        return resultList;
    }

    private StockData getStockDataEntity(StockDataDTO stockDataDto) {
        return(modelMapper.map(stockDataDto, StockData.class));
    }
}
