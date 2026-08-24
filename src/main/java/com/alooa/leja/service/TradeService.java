package com.alooa.leja.service;

import com.alooa.leja.dto.trades.CreateTradeRequest;
import com.alooa.leja.dto.trades.TradeResponse;
import com.alooa.leja.dto.trades.UpdateTradeRequest;

import java.util.List;

public interface TradeService {
    TradeResponse createTrade(CreateTradeRequest request);

    TradeResponse updateTrade(Long id, UpdateTradeRequest request);

    List<TradeResponse> getAllTrades();

    TradeResponse getTradeById(Long id);

    void deleteTrade(Long id);
}
