package com.alooa.leja.dto;

import com.alooa.leja.model.Direction;
import com.alooa.leja.model.Session;
import com.alooa.leja.validation.ValueOfEnum;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record UpdateTradeRequest(
        @Size(max = 32, message = "Symbol must be at most 32 characters")
        String symbol,

        @ValueOfEnum(enumClass = Direction.class, message = "Direction must be 'BUY' or 'SELL'")
        String direction,

        @ValueOfEnum(enumClass = Session.class, message = "Session must be 'NY', 'ASIA', or 'LONDON'")
        String session,

        // Up to 4 decimal places for lots/contracts
        @Pattern(regexp = "^(?!0(\\.0+)?$)\\d+(\\.\\d{1,4})?$", message = "Position size must be a valid positive number")
        String positionSize,

        @Pattern(regexp = "^(?!0(\\.0+)?$)\\d+(\\.\\d{1,4})?$", message = "Contract size must be a valid positive number")
        String contractSize,

        // Up to 8 decimal places for prices
        @Pattern(regexp = "^(?!0(\\.0+)?$)\\d+(\\.\\d{1,8})?$", message = "Entry price must be a valid positive number")
        String entryPrice,

        @Pattern(regexp = "^(?!0(\\.0+)?$)\\d+(\\.\\d{1,8})?$", message = "Exit price must be a valid positive number")
        String exitPrice,

        @Pattern(regexp = "^(?!0(\\.0+)?$)\\d+(\\.\\d{1,8})?$", message = "Stop loss must be a valid positive number")
        String stopLoss,

        @Pattern(regexp = "^(?!0(\\.0+)?$)\\d+(\\.\\d{1,8})?$", message = "Take profit must be a valid positive number")
        String takeProfit,

        @Size(max = 255, message = "Reason must be at most 255 characters")
        String reason,
        Instant executedAt
) {
}
