package com.alooa.leja.model;
import com.alooa.leja.model.trades.Direction;
import com.alooa.leja.model.trades.Session;
import com.alooa.leja.model.trades.Trade;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TradeTest {
    @Test
    void constructor_shouldSanitizeSymbol() {
        Trade trade = new Trade(
                "eurusd    ",
                Direction.BUY,
                Session.NY,
                1.0,
                150.0,
                "liq sweep"
        );
        assertEquals("EURUSD", trade.getSymbol());
    }

    @Test
    void constructor_shouldGetCorrectOutcome() {
        Trade trade = new Trade(
                "eurusd    ",
                Direction.BUY,
                Session.NY,
                1.0,
                -150.0,
                "liq sweep"
        );
        assertEquals("LOSS", trade.getOutcome());
    }
}
