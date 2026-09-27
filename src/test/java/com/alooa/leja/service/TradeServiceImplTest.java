package com.alooa.leja.service;

import com.alooa.leja.dto.CreateTradeRequest;
import com.alooa.leja.dto.TradeResponse;
import com.alooa.leja.dto.UpdateTradeRequest;
import com.alooa.leja.exception.InvalidTradeException;
import com.alooa.leja.exception.TradeNotFoundException;
import com.alooa.leja.mapper.TradeMapper;
import com.alooa.leja.model.Direction;
import com.alooa.leja.model.Session;
import com.alooa.leja.model.Trade;
import com.alooa.leja.repository.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeServiceImplTest {

    @Mock
    private TradeRepository tradeRepository;

    private TradeServiceImpl tradeService;

    @BeforeEach
    void setUp() {
        tradeService = new TradeServiceImpl(tradeRepository, new TradeMapper());
    }

    @Test
    void createTrade_shouldSaveAValidTrade() {
        when(tradeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TradeResponse result = tradeService.createTrade(createRequest("90", "120"));

        assertEquals("EURUSD", result.symbol());
        assertEquals("WIN", result.outcome());
        verify(tradeRepository).save(any());
    }

    @Test
    void createTrade_shouldRejectAStopOnTheWrongSide() {
        assertThrows(InvalidTradeException.class, () -> tradeService.createTrade(createRequest("105", "120")));

        verify(tradeRepository, never()).save(any());
    }

    @Test
    void updateTrade_shouldClearABlankReason() {
        when(tradeRepository.findById(1L)).thenReturn(Optional.of(buy("90", "120")));
        when(tradeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TradeResponse result = tradeService.updateTrade(1L, update(
                null, null, null, null, null, null, null, null, null, ""));

        assertNull(result.reason());
    }

    @Test
    void updateTrade_shouldSanitizeSymbol() {
        when(tradeRepository.findById(1L)).thenReturn(Optional.of(buy("90", "120")));
        when(tradeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TradeResponse result = tradeService.updateTrade(1L, update(
                "  gbpusd ", null, null, null, null, null, null, null, null, null));

        assertEquals("GBPUSD", result.symbol());
    }

    @Test
    void updateTrade_shouldRejectAStopOnTheWrongSideAndLeaveTheTradeUnsaved() {
        when(tradeRepository.findById(1L)).thenReturn(Optional.of(buy("90", "120")));

        assertThrows(InvalidTradeException.class, () -> tradeService.updateTrade(1L, update(
                null, null, null, null, null, null, null, "105", null, null)));

        verify(tradeRepository, never()).save(any());
    }

    @Test
    void updateTrade_shouldThrowWhenTradeIsMissing() {
        when(tradeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TradeNotFoundException.class, () -> tradeService.updateTrade(1L, update(
                null, null, null, null, null, null, null, null, null, "note")));

        verify(tradeRepository, never()).save(any());
    }

    @Test
    void getTradeById_shouldReturnTheTrade() {
        when(tradeRepository.findById(1L)).thenReturn(Optional.of(buy("90", "120")));

        TradeResponse result = tradeService.getTradeById(1L);

        assertEquals("EURUSD", result.symbol());
        assertEquals("WIN", result.outcome());
    }

    @Test
    void getTradeById_shouldThrowWhenTradeIsMissing() {
        when(tradeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TradeNotFoundException.class, () -> tradeService.getTradeById(1L));
    }

    @Test
    void getAllTrades_shouldReturnEveryTrade() {
        when(tradeRepository.findAll()).thenReturn(List.of(buy("90", "120")));

        List<TradeResponse> result = tradeService.getAllTrades();

        assertEquals(1, result.size());
        assertEquals("EURUSD", result.get(0).symbol());
    }

    @Test
    void deleteTrade_shouldDeleteTheTrade() {
        Trade trade = buy("90", "120");
        when(tradeRepository.findById(1L)).thenReturn(Optional.of(trade));

        tradeService.deleteTrade(1L);

        verify(tradeRepository).delete(trade);
    }

    @Test
    void deleteTrade_shouldThrowWhenTradeIsMissing() {
        when(tradeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TradeNotFoundException.class, () -> tradeService.deleteTrade(1L));

        verify(tradeRepository, never()).delete(any());
    }

    private Trade buy(String stop, String target) {
        Trade trade = new Trade(
                "EURUSD",
                Direction.BUY,
                Session.NY,
                new BigDecimal("1"),
                new BigDecimal("1"),
                new BigDecimal("100"),
                new BigDecimal("110"),
                new BigDecimal(stop),
                new BigDecimal(target),
                "liq sweep",
                null
        );
        trade.setId(1L);
        return trade;
    }

    private CreateTradeRequest createRequest(String stop, String target) {
        return new CreateTradeRequest(
                "EURUSD", "BUY", "NY", "1", "1", "100", "110", stop, target, "liq sweep", null
        );
    }

    private UpdateTradeRequest update(
            String symbol,
            String direction,
            String session,
            String positionSize,
            String contractSize,
            String entryPrice,
            String exitPrice,
            String stopLoss,
            String takeProfit,
            String reason) {
        return new UpdateTradeRequest(
                symbol, direction, session, positionSize, contractSize,
                entryPrice, exitPrice, stopLoss, takeProfit, reason, null
        );
    }
}
