package com.alooa.leja.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TradeTest {
    @Test
    void constructor_shouldSanitizeSymbol() {
        Trade trade = new Trade(
                "eurusd    ",
                Direction.BUY,
                Session.NY,
                new BigDecimal("1.0"),
                new BigDecimal("100000"),
                new BigDecimal("1.08500"),
                new BigDecimal("1.08750"),
                new BigDecimal("1.08200"),
                new BigDecimal("1.09000"),
                "liq sweep",
                null
        );
        assertEquals("EURUSD", trade.getSymbol());
    }

    @Test
    void setSymbol_shouldSanitizeSymbol() {
        Trade trade = new Trade(
                "EURUSD",
                Direction.BUY,
                Session.NY,
                new BigDecimal("1.0"),
                new BigDecimal("100000"),
                new BigDecimal("1.08500"),
                new BigDecimal("1.08750"),
                new BigDecimal("1.08200"),
                new BigDecimal("1.09000"),
                "liq sweep",
                null
        );
        trade.setSymbol("  gbpusd ");
        assertEquals("GBPUSD", trade.getSymbol());
    }

    @Test
    void constructor_shouldGetCorrectOutcome() {
        Trade trade = new Trade(
                "eurusd    ",
                Direction.BUY,
                Session.NY,
                new BigDecimal("1.0"),
                new BigDecimal("1"),
                new BigDecimal("10"),
                new BigDecimal("9"),
                new BigDecimal("8"),
                new BigDecimal("12"),
                "liq sweep",
                null
        );
        assertEquals("LOSS", trade.getOutcome());
    }
}
