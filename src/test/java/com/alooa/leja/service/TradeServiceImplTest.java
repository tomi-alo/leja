package com.alooa.leja.service;

import com.alooa.leja.dto.CreateTradeRequest;
import com.alooa.leja.dto.TradeResponse;
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
                "AAPL", Direction.BUY, Session.NY, 1.0, 150.0, "Good setup"
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
}
