package com.alooa.leja.service;

import com.alooa.leja.dto.CreateTradeRequest;
import com.alooa.leja.dto.TradeResponse;
import com.alooa.leja.dto.UpdateTradeRequest;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeServiceImplTest {

    @Mock
    private TradeRepository tradeRepository;
    @Mock
    private TradeMapper tradeMapper;

    private TradeServiceImpl tradeService;

    @BeforeEach
    void setUp() {
        tradeService = new TradeServiceImpl(tradeRepository, tradeMapper);
    }

    @Test
    void createTrade_ShouldReturnTradeResponse() {
        // --- GIVEN ---
        Long tradeId = 1L;

        // 1. Creating a fake request
        CreateTradeRequest request = new CreateTradeRequest(
                "AAPL", "BUY", "NY", "1.0", "150.0", "Good setup"
        );

        // 2. Creating fake entity and response objects
        Trade fakeEntity = new Trade("AAPL", Direction.BUY, Session.NY, 1.0, 150.0, "Good setup");
        fakeEntity.setId(tradeId);

        TradeResponse fakeResponse = new TradeResponse(
                tradeId, "AAPL", Direction.BUY, Session.NY, 1.0, 150.0, "Good setup", "WIN"
        );

        //3. Stubbing
        when(tradeMapper.toEntity(request)).thenReturn(fakeEntity);
        when(tradeRepository.save(fakeEntity)).thenReturn(fakeEntity);
        when(tradeMapper.toResponse(fakeEntity)).thenReturn(fakeResponse);


        TradeResponse result = tradeService.createTrade(request);


        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("AAPL", result.symbol());

        // Verifies that the repository's save method was called exactly 1 time
        verify(tradeRepository, times(1)).save(fakeEntity);
    }

    @Test
    void updateTrade_ShouldReturnUpdatedTradeResponse() {
        // --- GIVEN ---
        Long tradeId = 1L;

        // 1. Creating a fake request
        UpdateTradeRequest request = new UpdateTradeRequest(
                "AAPL", "SELL", "NY", "2.0", "200.0", "Updated reason"
        );

        // 2. Creating fake entity and response objects
        Trade existingTrade = new Trade("AAPL", Direction.BUY, Session.NY, 1.0, 150.0, "Good setup");
        existingTrade.setId(tradeId);

        Trade updatedTrade = new Trade("AAPL", Direction.SELL, Session.NY, 2.0, 200.0, "Updated reason");
        updatedTrade.setId(tradeId);

        TradeResponse updatedResponse = new TradeResponse(
                tradeId, "AAPL", Direction.SELL, Session.NY, 2.0, 200.0, "Updated reason", "LOSS"
        );

        //3. Stubbing
        when(tradeRepository.findById(tradeId)).thenReturn(java.util.Optional.of(existingTrade));
        when(tradeRepository.save(existingTrade)).thenReturn(updatedTrade);
        when(tradeMapper.toResponse(updatedTrade)).thenReturn(updatedResponse);

        // --- WHEN ---
        TradeResponse result = tradeService.updateTrade(tradeId, request);

        // --- THEN ---
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("AAPL", result.symbol());
        assertEquals(Direction.SELL, result.direction());
        assertEquals(Session.NY, result.session());
        assertEquals(2.0, result.positionSize());
        assertEquals(200.0, result.pnl());
        assertEquals("Updated reason", result.reason());

        // Verifies that the repository's save method was called exactly 1 time
        verify(tradeRepository, times(1)).save(existingTrade);
    }

    @Test
    void updateTrade_ShouldThrowException_WhenTradeNotFound() {
        // --- GIVEN ---
        Long tradeId = 1L;

        UpdateTradeRequest request = new UpdateTradeRequest(
                "AAPL", "SELL", "NY", "2.0", "200.0", "Updated reason"
        );

        // Stubbing to return empty Optional
        when(tradeRepository.findById(tradeId)).thenReturn(java.util.Optional.empty());

        // --- WHEN & THEN ---
        assertThrows(TradeNotFoundException.class, () -> {
            tradeService.updateTrade(tradeId, request);
        });

        // Verifies that the repository's save method was never called
        verify(tradeRepository, never()).save(any(Trade.class));
    }

    @Test
    void getTradeById_ShouldReturnTradeResponse() {
        // --- GIVEN ---
        Long tradeId = 1L;

        Trade existingTrade = new Trade("AAPL", Direction.BUY, Session.NY, 1.0, 150.0, "Good setup");
        existingTrade.setId(tradeId);

        TradeResponse expectedResponse = new TradeResponse(
                tradeId, "AAPL", Direction.BUY, Session.NY, 1.0, 150.0, "Good setup", "WIN"
        );

        // Stubbing
        when(tradeRepository.findById(tradeId)).thenReturn(java.util.Optional.of(existingTrade));
        when(tradeMapper.toResponse(existingTrade)).thenReturn(expectedResponse);

        // --- WHEN ---
        TradeResponse result = tradeService.getTradeById(tradeId);

        // --- THEN ---
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("AAPL", result.symbol());
        assertEquals(Direction.BUY, result.direction());
        assertEquals(Session.NY, result.session());
        assertEquals(1.0, result.positionSize());
        assertEquals(150.0, result.pnl());
        assertEquals("Good setup", result.reason());
        assertEquals("WIN", result.outcome());
    }

    @Test
    void getTradeById_ShouldThrowException_WhenTradeNotFound() {
        // --- GIVEN ---
        Long tradeId = 1L;

        // Stubbing to return empty Optional
        when(tradeRepository.findById(tradeId)).thenReturn(java.util.Optional.empty());

        // --- WHEN & THEN ---
        assertThrows(TradeNotFoundException.class, () -> {
            tradeService.getTradeById(tradeId);
        });
    }

    @Test
    void getAllTrades_ShouldReturnListOfTradeResponses() {
        // --- GIVEN ---
        Trade trade1 = new Trade("AAPL", Direction.BUY, Session.NY, 1.0, 150.0, "Good setup");
        trade1.setId(1L);
        Trade trade2 = new Trade("GOOGL", Direction.SELL, Session.LONDON, 2.0, 200.0, "Another setup");
        trade2.setId(2L);

        TradeResponse response1 = new TradeResponse(
                1L, "AAPL", Direction.BUY, Session.NY, 1.0, 150.0, "Good setup", "WIN"
        );
        TradeResponse response2 = new TradeResponse(
                2L, "GOOGL", Direction.SELL, Session.LONDON, 2.0, 200.0, "Another setup", "LOSS"
        );

        // Stubbing
        when(tradeRepository.findAll()).thenReturn(java.util.List.of(trade1, trade2));
        when(tradeMapper.toResponse(trade1)).thenReturn(response1);
        when(tradeMapper.toResponse(trade2)).thenReturn(response2);

        // --- WHEN ---
        java.util.List<TradeResponse> result = tradeService.getAllTrades();

        // --- THEN ---
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));
    }

    @Test
    void deleteTrade_ShouldDeleteTrade() {
        // --- GIVEN ---
        Long tradeId = 1L;

        Trade existingTrade = new Trade("AAPL", Direction.BUY, Session.NY, 1.0, 150.0, "Good setup");
        existingTrade.setId(tradeId);

        // Stubbing
        when(tradeRepository.findById(tradeId)).thenReturn(java.util.Optional.of(existingTrade));

        // --- WHEN ---
        tradeService.deleteTrade(tradeId);

        // --- THEN ---
        verify(tradeRepository, times(1)).delete(existingTrade);
    }

    @Test
    void deleteTrade_ShouldThrowException_WhenTradeNotFound() {
        // --- GIVEN ---
        Long tradeId = 1L;

        // Stubbing to return empty Optional
        when(tradeRepository.findById(tradeId)).thenReturn(java.util.Optional.empty());

        // --- WHEN & THEN ---
        assertThrows(TradeNotFoundException.class, () -> {
            tradeService.deleteTrade(tradeId);
        });

        // Verifies that the repository's delete method was never called
        verify(tradeRepository, never()).delete(any(Trade.class));
    }
}

