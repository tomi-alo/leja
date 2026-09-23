package com.alooa.leja.mapper;

import com.alooa.leja.dto.CreateTradeRequest;
import com.alooa.leja.dto.TradeResponse;
import com.alooa.leja.model.Direction;
import com.alooa.leja.model.Session;
import com.alooa.leja.model.Trade;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TradeMapper {

    public Trade toEntity(CreateTradeRequest request) {
        Direction direction = request.direction() != null
                ? Direction.valueOf(request.direction().trim().toUpperCase())
                : null;

        Session session = request.session() != null
                ? Session.valueOf(request.session().trim().toUpperCase())
                : null;

        BigDecimal positionSize = request.positionSize() != null
                ? new BigDecimal(request.positionSize().trim())
                : null;

        BigDecimal contractSize = request.contractSize() != null
                ? new BigDecimal(request.contractSize().trim())
                : null;

        BigDecimal entryPrice = request.entryPrice() != null
                ? new BigDecimal(request.entryPrice().trim())
                : null;

        BigDecimal exitPrice = request.exitPrice() != null
                ? new BigDecimal(request.exitPrice().trim())
                : null;

        BigDecimal stopLoss = request.stopLoss() != null
                ? new BigDecimal(request.stopLoss().trim())
                : null;

        BigDecimal takeProfit = request.takeProfit() != null
                ? new BigDecimal(request.takeProfit().trim())
                : null;

        return new Trade(
                request.symbol(),
                direction,
                session,
                positionSize,
                contractSize,
                entryPrice,
                exitPrice,
                stopLoss,
                takeProfit,
                request.reason(),
                request.executedAt()
        );
    }

    public TradeResponse toResponse(Trade trade){
        return new TradeResponse(
                trade.getId(),
                trade.getSymbol(),
                trade.getDirection(),
                trade.getSession(),
                trade.getPositionSize(),
                trade.getContractSize(),
                trade.getEntryPrice(),
                trade.getExitPrice(),
                trade.getStopLoss(),
                trade.getTakeProfit(),
                trade.getPnL(),
                trade.getRiskToRewardRatio(),
                trade.getReason(),
                trade.getOutcome(),
                trade.getExecutedAt()
        );
    }
}
