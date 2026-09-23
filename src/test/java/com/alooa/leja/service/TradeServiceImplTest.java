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

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        Long tradeId = 1L;

        CreateTradeRequest request = new CreateTradeRequest(
                "AAPL", "BUY", "NY", "1.0", "1", "100", "110", "90", "120", "Good setup", null
        );

        Trade fakeEntity = new Trade(
                "AAPL",
                Direction.BUY,
                Session.NY,
                new BigDecimal("1.0"),
                new BigDecimal("1"),
                new BigDecimal("100"),
                new BigDecimal("110"),
                new BigDecimal("90"),
                new BigDecimal("120"),
                "Good setup",
                null
        );
        fakeEntity.setId(tradeId);

        TradeResponse fakeResponse = new TradeResponse(
                tradeId,
                "AAPL",
                Direction.BUY,
                Session.NY,
                new BigDecimal("1.0"),
                new BigDecimal("1"),
                new BigDecimal("100"),
                new BigDecimal("110"),
                new BigDecimal("90"),
                new BigDecimal("120"),
                new BigDecimal("10.00"),
                new BigDecimal("2.00"),
                "Good setup",
                "WIN",
                null
        );

        when(tradeMapper.toEntity(request)).thenReturn(fakeEntity);
        when(tradeRepository.save(fakeEntity)).thenReturn(fakeEntity);
        when(tradeMapper.toResponse(fakeEntity)).thenReturn(fakeResponse);

        TradeResponse result = tradeService.createTrade(request);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("AAPL", result.symbol());

        verify(tradeRepository, times(1)).save(fakeEntity);
    }
}
