package com.alooa.leja.dto;

import com.alooa.leja.model.Direction;
import com.alooa.leja.model.Session;
import com.alooa.leja.validation.ValueOfEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record CreateTradeRequest(
        @NotBlank(message = "Symbol is required")
        String symbol,

        @NotNull(message = "Direction is required")
        @ValueOfEnum(enumClass = Direction.class, message = "Direction must be 'BUY' or 'SELL'")
        String direction,

        @NotNull(message = "Session is required")
        @ValueOfEnum(enumClass = Session.class, message = "Session must be 'NY', 'ASIA', or 'LONDON'")
        String session,

        @NotNull(message = "Position size is required")
        @Pattern(regexp = "^\\d+(\\.\\d+)?$", message = "Position size must be a valid positive number")
        String positionSize,

        @NotNull(message = "PnL is required")
        @Pattern(regexp = "^-?\\d+(\\.\\d+)?$", message = "PnL must be a valid number")
        String pnl,

        String reason

) {
}
