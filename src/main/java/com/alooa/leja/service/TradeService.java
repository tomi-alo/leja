package com.alooa.leja.service;

import com.alooa.leja.repository.TradeRepository;

public class TradeService {
    private TradeRepository tradeRepository;

    public TradeService(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }
}
