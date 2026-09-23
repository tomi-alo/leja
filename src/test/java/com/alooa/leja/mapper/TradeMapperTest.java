package com.alooa.leja.mapper;

import com.alooa.leja.dto.CreateTradeRequest;
import com.alooa.leja.dto.TradeResponse;
import com.alooa.leja.model.Direction;
import com.alooa.leja.model.Session;
import com.alooa.leja.model.Trade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TradeMapperTest {

    private TradeMapper tradeMapper;

    @BeforeEach
    void setUp() {
        tradeMapper = new TradeMapper();
    }

    @Test
    void toEntity_shouldMakeCorrectly() {
        CreateTradeRequest request = new CreateTradeRequest(
                "eurusd",
                "SELL",
                "ASIA",
                "2.0",
                "40.0",
                "1.09000",
                "1.08000",
                "1.10000",
                "1.07000",
                null,
                null
        );
        Trade trade = tradeMapper.toEntity(request);

        assertEquals("EURUSD", trade.getSymbol());
        assertEquals(Direction.SELL, trade.getDirection());
        assertEquals(Session.ASIA, trade.getSession());
        assertEquals(new BigDecimal("2.0"), trade.getPositionSize());
        assertEquals(new BigDecimal("40.0"), trade.getContractSize());
        assertNull(trade.getReason());
    }

    @Test
    void toResponse_shouldReturnCorrectResponse() {
        Trade trade = new Trade(
                "eurusd    ",
                Direction.BUY,
                Session.NY,
                new BigDecimal("1.0"),
                new BigDecimal("1"),
                new BigDecimal("1.10"),
                new BigDecimal("1.10"),
                new BigDecimal("1.00"),
                new BigDecimal("1.20"),
                "liq sweep",
                null
        );

        TradeResponse tradeResponse = tradeMapper.toResponse(trade);

        assertNull(tradeResponse.id());
        assertEquals("EURUSD", tradeResponse.symbol());
        assertEquals(Direction.BUY, tradeResponse.direction());
        assertEquals(Session.NY, tradeResponse.session());
        assertEquals(new BigDecimal("1.0"), tradeResponse.positionSize());
        assertEquals(0, tradeResponse.pnl().compareTo(BigDecimal.ZERO));
        assertEquals("liq sweep", tradeResponse.reason());
        assertEquals("BREAK EVEN", tradeResponse.outcome());
    }
}
