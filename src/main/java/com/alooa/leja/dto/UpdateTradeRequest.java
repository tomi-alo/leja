package com.alooa.leja.dto;

import com.alooa.leja.model.Direction;
import com.alooa.leja.model.Session;

public record UpdateTradeRequest(
        String symbol,
        Direction direction,
        Session session,
        Double positionSize,
        Double pnl,
        String reason
) {
}
