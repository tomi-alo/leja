package com.alooa.leja.mapper;

import com.alooa.leja.dto.CreateTradeRequest;
import com.alooa.leja.dto.TradeResponse;
import com.alooa.leja.model.Direction;
import com.alooa.leja.model.Session;
import com.alooa.leja.model.Trade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TradeMapperTest {

    private TradeMapper tradeMapper;

    @BeforeEach
    void setUp() {
        tradeMapper = new TradeMapper();
    }

    @Test
    void toEntity_shouldMakeCorrectly(){
        CreateTradeRequest request = new CreateTradeRequest(
                "eurusd",
                "SELL",
                "ASIA",
                "2.0",
                "40.0",
                null
        );
        Trade trade = tradeMapper.toEntity(request);

        assertEquals("EURUSD", trade.getSymbol());
        assertEquals(Direction.SELL, trade.getDirection());
        assertEquals(Session.ASIA, trade.getSession());
        assertEquals(2.0, trade.getPositionSize());
        assertEquals(40.0, trade.getPnl());
        assertNull(trade.getReason());
    }

    @Test
    void toResponse_shouldReturnCorrectResponse(){
        Trade trade = new Trade(
                "eurusd    ",
                Direction.BUY,
                Session.NY,
                1.0,
                0.0,
                "liq sweep"
        );

        TradeResponse tradeResponse = tradeMapper.toResponse(trade);

        assertNull(tradeResponse.id());
        assertEquals("EURUSD", tradeResponse.symbol());
        assertEquals(Direction.BUY, tradeResponse.direction());
        assertEquals(Session.NY, tradeResponse.session());
        assertEquals(1.0, tradeResponse.positionSize());
        assertEquals(0.0, tradeResponse.pnl());
        assertEquals("liq sweep", tradeResponse.reason());
        assertEquals("BREAK EVEN", tradeResponse.outcome());

    }
}
