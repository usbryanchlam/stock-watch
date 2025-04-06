package dev.bryanlam.stockwatch.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import dev.bryanlam.stockwatch.model.StockData;
import java.util.Optional;

@Repository
public interface StockDataRepository extends MongoRepository<StockData, String> {
    Optional<StockData> findBySymbol(String symbol);
}
