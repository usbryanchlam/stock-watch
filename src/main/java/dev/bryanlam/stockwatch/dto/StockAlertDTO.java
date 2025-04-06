package dev.bryanlam.stockwatch.dto;

import java.time.LocalDateTime;

import dev.bryanlam.stockwatch.model.AlertCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockAlertDTO {
    private String id;
    private String userId;
    private String stockSymbol;
    private Double targetPrice;
    private AlertCondition condition; // ABOVE, BELOW
    private Boolean isActive = true;
    private Boolean isTriggered = false;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime triggeredAt;
}
