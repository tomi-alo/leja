package com.alooa.leja.model;

import com.alooa.leja.exception.InvalidTradeException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TradeTest {

    @Test
    void constructor_shouldSanitizeSymbol() {
        Trade trade = buy("eurusd    ", "110", "90", "120");

        assertEquals("EURUSD", trade.getSymbol());
    }

    @Test
    void setSymbol_shouldSanitizeSymbol() {
        Trade trade = buy("EURUSD", "110", "90", "120");

        trade.setSymbol("  gbpusd ");

        assertEquals("GBPUSD", trade.getSymbol());
    }

    @Test
    void outcome_shouldBeWinLossOrBreakEven() {
        assertEquals("WIN", buy("EURUSD", "110", "90", "120").getOutcome());
        assertEquals("LOSS", buy("EURUSD", "90", "80", "120").getOutcome());
        assertEquals("BREAK EVEN", buy("EURUSD", "100", "90", "120").getOutcome());
    }

    @Test
    void pnl_shouldBeExitMinusEntryTimesSize() {
        assertEquals(new BigDecimal("10.00"), buy("EURUSD", "110", "90", "120").getPnL());
    }

    @Test
    void checkPriceLevels_shouldNameOnlyTheStopForABuy() {
        Trade trade = buy("EURUSD", "110", "105", "120");

        InvalidTradeException ex = assertThrows(InvalidTradeException.class, trade::checkPriceLevels);

        assertEquals(List.of("Stop loss must be below the entry for a buy"), ex.getErrors());
    }

    @Test
    void checkPriceLevels_shouldNameOnlyTheTargetForABuy() {
        Trade trade = buy("EURUSD", "110", "90", "100");

        InvalidTradeException ex = assertThrows(InvalidTradeException.class, trade::checkPriceLevels);

        assertEquals(List.of("Take profit must be above the entry for a buy"), ex.getErrors());
    }

    @Test
    void checkPriceLevels_shouldNameBothLevelsForABuy() {
        Trade trade = buy("EURUSD", "110", "105", "90");

        InvalidTradeException ex = assertThrows(InvalidTradeException.class, trade::checkPriceLevels);

        assertEquals(List.of(
                "Stop loss must be below the entry for a buy",
                "Take profit must be above the entry for a buy"
        ), ex.getErrors());
    }

    @Test
    void checkPriceLevels_shouldNameOnlyTheStopForASell() {
        Trade trade = sell("90", "90", "80");

        InvalidTradeException ex = assertThrows(InvalidTradeException.class, trade::checkPriceLevels);

        assertEquals(List.of("Stop loss must be above the entry for a sell"), ex.getErrors());
    }

    @Test
    void checkPriceLevels_shouldAllowAValidBuyAndSell() {
        buy("EURUSD", "110", "90", "120").checkPriceLevels();
        sell("90", "110", "80").checkPriceLevels();
    }

    private Trade buy(String symbol, String exit, String stop, String target) {
        return new Trade(
                symbol,
                Direction.BUY,
                Session.NY,
                new BigDecimal("1"),
                new BigDecimal("1"),
                new BigDecimal("100"),
                new BigDecimal(exit),
                new BigDecimal(stop),
                new BigDecimal(target),
                "liq sweep",
                null
        );
    }

    private Trade sell(String exit, String stop, String target) {
        return new Trade(
                "EURUSD",
                Direction.SELL,
                Session.LONDON,
                new BigDecimal("1"),
                new BigDecimal("1"),
                new BigDecimal("100"),
                new BigDecimal(exit),
                new BigDecimal(stop),
                new BigDecimal(target),
                "liq sweep",
                null
        );
    }
}
