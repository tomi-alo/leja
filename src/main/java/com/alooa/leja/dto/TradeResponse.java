package com.alooa.leja.dto;

import com.alooa.leja.model.Direction;
import com.alooa.leja.model.Session;

import java.math.BigDecimal;
import java.time.Instant;

public record TradeResponse(
        Long id,
        String symbol,
        Direction direction,
        Session session,
        BigDecimal positionSize,
        BigDecimal contractSize,
        BigDecimal entryPrice,
        BigDecimal exitPrice,
        BigDecimal stopLoss,
        BigDecimal takeProfit,
        BigDecimal pnl,
        BigDecimal riskToRewardRatio,
        String reason,
        String outcome,
        Instant executedAt
) {
}
