package com.alooa.leja.dto.trades;

import com.alooa.leja.model.trades.Direction;
import com.alooa.leja.model.trades.Session;

public record TradeResponse(
        Long id,
        String symbol,
        Direction direction,
        Session session,
        Double positionSize,
        Double pnl,
        String reason,
        String outcome
) {
}
