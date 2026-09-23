package com.alooa.leja.dto;

import com.alooa.leja.model.Direction;
import com.alooa.leja.model.Session;
import com.alooa.leja.validation.ValueOfEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.Instant;

public record CreateTradeRequest(
        @NotBlank(message = "Symbol is required")
        String symbol,

        @NotNull(message = "Direction is required")
        @ValueOfEnum(enumClass = Direction.class, message = "Direction must be 'BUY' or 'SELL'")
        String direction,

        @NotNull(message = "Session is required")
        @ValueOfEnum(enumClass = Session.class, message = "Session must be 'NY', 'ASIA', or 'LONDON'")
        String session,

        // Up to 4 decimal places for lots/contracts
        @NotNull(message = "Position size is required")
        @Pattern(regexp = "^(?!0(\\.0+)?$)\\d+(\\.\\d{1,4})?$", message = "Position size must be a valid positive number")
        String positionSize,

        @NotNull(message = "Contract size is required")
        @Pattern(regexp = "^(?!0(\\.0+)?$)\\d+(\\.\\d{1,4})?$", message = "Contract size must be a valid positive number")
        String contractSize,

        // Up to 8 decimal places for prices
        @NotNull(message = "Entry price is required")
        @Pattern(regexp = "^(?!0(\\.0+)?$)\\d+(\\.\\d{1,8})?$", message = "Entry price must be a valid positive number")
        String entryPrice,

        @NotNull(message = "Exit price is required")
        @Pattern(regexp = "^(?!0(\\.0+)?$)\\d+(\\.\\d{1,8})?$", message = "Exit price must be a valid positive number")
        String exitPrice,

        @NotNull(message = "Stop loss is required")
        @Pattern(regexp = "^(?!0(\\.0+)?$)\\d+(\\.\\d{1,8})?$", message = "Stop loss must be a valid positive number")
        String stopLoss,

        @NotNull(message = "Take profit is required")
        @Pattern(regexp = "^(?!0(\\.0+)?$)\\d+(\\.\\d{1,8})?$", message = "Take profit must be a valid positive number")
        String takeProfit,

        String reason,

        Instant executedAt
) {
}