package com.alooa.leja.dto;

import com.alooa.leja.model.Direction;
import com.alooa.leja.model.Session;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateTradeRequest(
        @NotBlank(message = "Symbol is required")
        String symbol,

        @NotNull(message = "Direction is required")
        Direction direction,

        @NotNull(message = "Direction is required")
        Session session,

        @NotNull(message = "Position size is required")
        @Positive(message = "Position size must be positive")
        Double positionSize,

        @NotNull(message = "PnL is required")
        Double pnl,

        String reason

) {
}
