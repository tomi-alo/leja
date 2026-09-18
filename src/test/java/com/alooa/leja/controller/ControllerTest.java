package com.alooa.leja.controller;

import com.alooa.leja.dto.TradeResponse;
import com.alooa.leja.model.Direction;
import com.alooa.leja.model.Session;
import com.alooa.leja.service.TradeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;

@WebMvcTest(TradeController.class)
class TradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TradeService tradeService;

    @Test
    void getAllTrades_ShouldReturnOkAndTradeList() throws Exception {
        TradeResponse trade1 = new TradeResponse(
                1L,
                "AAPL",
                Direction.BUY,
                Session.NY,
                1.0,
                150.0,
                "Good setup",
                "WIN"
        );

        TradeResponse trade2 = new TradeResponse(
                2L,
                "GOOGL",
                Direction.SELL,
                Session.LONDON,
                2.0,
                -100.0,
                "Bad setup",
                "LOSS"
        );

        when(tradeService.getAllTrades()).thenReturn(List.of(trade1, trade2));

        mockMvc.perform(get("/api/v1/trades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].symbol").value("AAPL"))
                .andExpect(jsonPath("$[0].direction").value("BUY"));
    }

    @Test
    void getTradeById_ShouldReturnOkAndTrade() throws Exception {
        TradeResponse trade = new TradeResponse(
                1L,
                "AAPL",
                Direction.BUY,
                Session.NY,
                1.0,
                150.0,
                "Good setup",
                "WIN"
        );

        when(tradeService.getTradeById(1L)).thenReturn(trade);

        mockMvc.perform(get("/api/v1/trades/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.symbol").value("AAPL"))
                .andExpect(jsonPath("$.direction").value("BUY"));
    }

    @Test
    void postTrade_ShouldReturnCreatedAndTrade() throws Exception {
        TradeResponse created = new TradeResponse(
                3L,
                "TSLA",
                Direction.BUY,
                Session.NY,
                1.0,
                200.0,
                "Great setup",
                "WIN"
        );

        when(tradeService.createTrade(any())).thenReturn(created);

        String json = """
            {
              "symbol": "TSLA",
              "direction": "BUY",
              "session": "NY",
              "positionSize": "1.0",
              "pnl": "200.0",
              "reason": "Great setup"
            }
            """;

        mockMvc.perform(post("/api/v1/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.symbol").value("TSLA"))
                .andExpect(jsonPath("$.direction").value("BUY"));

        verify(tradeService).createTrade(any());
    }

    @Test
    void putTrade_ShouldReturnOkAndUpdatedTrade() throws Exception {
        TradeResponse updated = new TradeResponse(
                1L,
                "AAPL",
                Direction.SELL,
                Session.NY,
                1.0,
                100.0,
                "Updated reason",
                "LOSS"
        );

        when(tradeService.updateTrade(any(), any())).thenReturn(updated);

        String json = """
            {
              "symbol": "AAPL",
              "direction": "SELL",
              "session": "NY",
              "positionSize": "1.0",
              "pnl": "100.0",
              "reason": "Updated reason"
            }
            """;

        mockMvc.perform(put("/api/v1/trades/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.symbol").value("AAPL"))
                .andExpect(jsonPath("$.direction").value("SELL"));

        verify(tradeService).updateTrade(any(), any());
    }

    @Test
    void deleteTrade_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/trades/1"))
                .andExpect(status().isNoContent());

        verify(tradeService).deleteTrade(1L);
    }
}