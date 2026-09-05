package com.alooa.leja.mapper;

import com.alooa.leja.dto.CreateTradeRequest;
import com.alooa.leja.dto.TradeResponse;
import com.alooa.leja.model.Direction;
import com.alooa.leja.model.Session;
import com.alooa.leja.model.Trade;
import org.springframework.stereotype.Component;

@Component
public class TradeMapper {

    public Trade toEntity(CreateTradeRequest request) {
        Direction direction = request.direction() != null
                ? Direction.valueOf(request.direction().trim().toUpperCase())
                : null;

        Session session = request.session() != null
                ? Session.valueOf(request.session().trim().toUpperCase())
                : null;

        Double positionSize = request.positionSize() != null
                ? Double.parseDouble(request.positionSize().trim())
                : null;

        Double pnl = request.pnl() != null
                ? Double.parseDouble(request.pnl().trim())
                : null;

        return new Trade(
                request.symbol(),
                direction,
                session,
                positionSize,
                pnl,
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
