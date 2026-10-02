package com.alooa.leja.controller;

import com.alooa.leja.config.RestAuthenticationEntryPoint;
import com.alooa.leja.config.SecurityConfig;
import com.alooa.leja.dto.TradeResponse;
import com.alooa.leja.exception.InvalidTradeException;
import com.alooa.leja.model.Direction;
import com.alooa.leja.model.Session;
import com.alooa.leja.service.TradeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TradeController.class)
@Import({SecurityConfig.class, RestAuthenticationEntryPoint.class, GlobalExceptionHandler.class})
@TestPropertySource(properties = {
        "leja.auth.username=leja",
        "leja.auth.password=test-password"
})
class TradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TradeService tradeService;

    @Test
    void getAllTrades_shouldAllowARequestWithoutAPassword() throws Exception {
        when(tradeService.getAllTrades()).thenReturn(List.of(sample()));

        mockMvc.perform(get("/api/v1/trades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].symbol").value("EURUSD"));
    }

    @Test
    void createTrade_shouldRejectARequestWithoutAPassword() throws Exception {
        mockMvc.perform(post("/api/v1/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validTradeJson()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errors[0]").value("Authentication required"));

        verify(tradeService, never()).createTrade(any());
    }

    @Test
    void getAllTrades_shouldReturnTheList() throws Exception {
        when(tradeService.getAllTrades()).thenReturn(List.of(sample()));

        mockMvc.perform(get("/api/v1/trades").with(login()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].symbol").value("EURUSD"))
                .andExpect(jsonPath("$[0].outcome").value("WIN"));
    }

    @Test
    void getTrade_shouldReturnOneTrade() throws Exception {
        when(tradeService.getTradeById(1L)).thenReturn(sample());

        mockMvc.perform(get("/api/v1/trades/1").with(login()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createTrade_shouldReturnCreated() throws Exception {
        when(tradeService.createTrade(any())).thenReturn(sample());

        mockMvc.perform(post("/api/v1/trades")
                        .with(login())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validTradeJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.symbol").value("EURUSD"));
    }

    @Test
    void createTrade_shouldReturn400WhenARequiredFieldIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/trades")
                        .with(login())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray());

        verify(tradeService, never()).createTrade(any());
    }

    @Test
    void createTrade_shouldReturn400WhenTheStopIsOnTheWrongSide() throws Exception {
        when(tradeService.createTrade(any())).thenThrow(new InvalidTradeException(
                List.of("Stop loss must be below the entry for a buy")));

        mockMvc.perform(post("/api/v1/trades")
                        .with(login())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validTradeJson()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Stop loss must be below the entry for a buy"));
    }

    @Test
    void updateTrade_shouldUsePatch() throws Exception {
        when(tradeService.updateTrade(any(), any())).thenReturn(sample());

        mockMvc.perform(patch("/api/v1/trades/1")
                        .with(login())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"\"}"))
                .andExpect(status().isOk());

        verify(tradeService).updateTrade(any(), any());
    }

    @Test
    void updateTrade_shouldRejectPut() throws Exception {
        mockMvc.perform(put("/api/v1/trades/1")
                        .with(login())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"liq sweep\"}"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.errors[0]").value("PUT is not supported"));

        verify(tradeService, never()).updateTrade(any(), any());
    }

    @Test
    void deleteTrade_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/trades/1").with(login()))
                .andExpect(status().isNoContent());

        verify(tradeService).deleteTrade(1L);
    }

    private org.springframework.test.web.servlet.request.RequestPostProcessor login() {
        return httpBasic("leja", "test-password");
    }

    private TradeResponse sample() {
        return new TradeResponse(
                1L,
                "EURUSD",
                Direction.BUY,
                Session.NY,
                new BigDecimal("1"),
                new BigDecimal("1"),
                new BigDecimal("100"),
                new BigDecimal("110"),
                new BigDecimal("90"),
                new BigDecimal("120"),
                new BigDecimal("10.00"),
                new BigDecimal("2.00"),
                "liq sweep",
                "WIN",
                null
        );
    }

    private String validTradeJson() {
        return """
                {
                  "symbol": "EURUSD",
                  "direction": "BUY",
                  "session": "NY",
                  "positionSize": "1",
                  "contractSize": "1",
                  "entryPrice": "100",
                  "exitPrice": "110",
                  "stopLoss": "90",
                  "takeProfit": "120",
                  "reason": "liq sweep"
                }
                """;
    }
}
