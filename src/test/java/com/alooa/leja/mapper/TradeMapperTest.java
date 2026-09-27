package com.alooa.leja.mapper;

import com.alooa.leja.dto.CreateTradeRequest;
import com.alooa.leja.dto.TradeResponse;
import com.alooa.leja.model.Direction;
import com.alooa.leja.model.Session;
import com.alooa.leja.model.Trade;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TradeMapperTest {

    private final TradeMapper tradeMapper = new TradeMapper();

    @Test
    void toEntity_shouldParseNumbersAndSanitizeSymbol() {
        CreateTradeRequest request = new CreateTradeRequest(
                " eurusd ",
                "buy",
                "ny",
                "1.5",
                "100000",
                "1.08500",
                "1.08750",
                "1.08200",
                "1.09000",
                "liq sweep",
                Instant.parse("2026-09-01T14:30:00Z")
        );

        Trade trade = tradeMapper.toEntity(request);

        assertEquals("EURUSD", trade.getSymbol());
        assertEquals(Direction.BUY, trade.getDirection());
        assertEquals(Session.NY, trade.getSession());
        assertEquals(new BigDecimal("1.5"), trade.getPositionSize());
        assertEquals(new BigDecimal("100000"), trade.getContractSize());
        assertEquals(new BigDecimal("1.08500"), trade.getEntryPrice());
        assertEquals(Instant.parse("2026-09-01T14:30:00Z"), trade.getExecutedAt());
    }

    @Test
    void toEntity_shouldUseNowWhenExecutedAtIsMissing() {
        Trade trade = tradeMapper.toEntity(request());

        assertNotNull(trade.getExecutedAt());
    }

    @Test
    void toResponse_shouldIncludeCalculatedPnlAndOutcome() {
        Trade trade = tradeMapper.toEntity(request());
        trade.setId(1L);

        TradeResponse response = tradeMapper.toResponse(trade);

        assertEquals(1L, response.id());
        assertEquals(new BigDecimal("10.00"), response.pnl());
        assertEquals(new BigDecimal("2.00"), response.riskToRewardRatio());
        assertEquals("WIN", response.outcome());
        assertEquals("liq sweep", response.reason());
    }

    private CreateTradeRequest request() {
        return new CreateTradeRequest(
                "EURUSD",
                "BUY",
                "NY",
                "1",
                "1",
                "100",
                "110",
                "90",
                "120",
                "liq sweep",
                null
        );
    }
}
