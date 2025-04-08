package dev.bryanlam.stockwatch.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import dev.bryanlam.stockwatch.model.StockAlert;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockAlertRepository extends MongoRepository<StockAlert, String> {
    List<StockAlert> findByUserIdAndIsActiveTrue(String userId);
    List<StockAlert> findByStockSymbolAndIsActiveTrue(String stockSymbol);
    List<StockAlert> findByIsActiveTrueAndIsTriggeredFalse();
    Optional<StockAlert> findByUserIdAndStockSymbol(String userId, String stockSymbol);
    void deleteByUserId(String userId);
}
