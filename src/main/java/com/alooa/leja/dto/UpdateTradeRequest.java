package com.alooa.leja.dto;

import com.alooa.leja.model.Direction;
import com.alooa.leja.model.Session;
import com.alooa.leja.validation.ValueOfEnum;
import jakarta.validation.constraints.Pattern;

public record UpdateTradeRequest(
        String symbol,

        @ValueOfEnum(enumClass = Direction.class, message = "Direction must be 'BUY' or 'SELL'")
        String direction,

        @ValueOfEnum(enumClass = Session.class, message = "Session must be 'NY', 'ASIA', or 'LONDON'")
        String session,

        @Pattern(regexp = "^\\d+(\\.\\d+)?$", message = "Position size must be a valid positive number")
        String positionSize,

        @Pattern(regexp = "^-?\\d+(\\.\\d+)?$", message = "PnL must be a valid number")
        String pnl,
        String reason
) {
}
