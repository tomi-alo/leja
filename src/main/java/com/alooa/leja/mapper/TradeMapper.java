package com.alooa.leja.mapper;

import com.alooa.leja.dto.CreateTradeRequest;
import com.alooa.leja.dto.TradeResponse;
import com.alooa.leja.model.Trade;
import org.springframework.stereotype.Component;

@Component
public class TradeMapper {

    public Trade toEntity(CreateTradeRequest request) {
        return new Trade(
                request.symbol(),
                request.direction(),
                request.session(),
                request.positionSize(),
                request.pnl(),
                request.reason());
    }

    public TradeResponse toResponse(Trade trade){
        return new TradeResponse(
                trade.getId(),
                trade.getSymbol(),
                trade.getDirection(),
                trade.getSession(),
                trade.getPositionSize(),
                trade.getPnl(),
                trade.getReason(),
                trade.getOutcome()
        );
    }
}
