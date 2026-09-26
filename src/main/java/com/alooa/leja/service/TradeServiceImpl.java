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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TradeServiceImpl implements TradeService {

    private final TradeRepository tradeRepository;
    private final TradeMapper tradeMapper;

    public TradeServiceImpl(TradeRepository tradeRepository, TradeMapper tradeMapper) {
        this.tradeRepository = tradeRepository;
        this.tradeMapper = tradeMapper;
    }

    @Override
    public TradeResponse createTrade(CreateTradeRequest request) {
        Trade trade = tradeMapper.toEntity(request);
        trade.checkPriceLevels();
        Trade savedTrade = tradeRepository.save(trade);
        return tradeMapper.toResponse(savedTrade);
    }

    @Override
    public TradeResponse updateTrade(Long id, UpdateTradeRequest request) {
        Trade trade = tradeRepository.findById(id)
                .orElseThrow(() -> new TradeNotFoundException(id));

        if (request.symbol() != null && !request.symbol().isBlank()) {
            trade.setSymbol(request.symbol());
        }

        if (request.direction() != null && !request.direction().isBlank()) {
            trade.setDirection(Direction.valueOf(request.direction().trim().toUpperCase()));
        }

        if (request.session() != null && !request.session().isBlank()) {
            trade.setSession(Session.valueOf(request.session().trim().toUpperCase()));
        }

        if (request.positionSize() != null && !request.positionSize().isBlank()) {
            trade.setPositionSize(new BigDecimal(request.positionSize().trim()));
        }

        if (request.contractSize() != null && !request.contractSize().isBlank()) {
            trade.setContractSize(new BigDecimal(request.contractSize().trim()));
        }

        if (request.reason() != null) {
            trade.setReason(request.reason().isBlank() ? null : request.reason().trim());
        }

        if (request.entryPrice() != null && !request.entryPrice().isBlank()) {
            trade.setEntryPrice(new BigDecimal(request.entryPrice().trim()));
        }

        if (request.exitPrice() != null && !request.exitPrice().isBlank()) {
            trade.setExitPrice(new BigDecimal(request.exitPrice().trim()));
        }

        if (request.stopLoss() != null && !request.stopLoss().isBlank()) {
            trade.setStopLoss(new BigDecimal(request.stopLoss().trim()));
        }

        if (request.takeProfit() != null && !request.takeProfit().isBlank()) {
            trade.setTakeProfit(new BigDecimal(request.takeProfit().trim()));
        }

        if (request.executedAt() != null) {
            trade.setExecutedAt(request.executedAt());
        }

        trade.checkPriceLevels();
        Trade updatedTrade = tradeRepository.save(trade);
        return tradeMapper.toResponse(updatedTrade);
    }

    @Override
    public List<TradeResponse> getAllTrades() {
        return tradeRepository.findAll()
                .stream()
                .map(trade -> tradeMapper.toResponse(trade))
                .toList();
    }

    @Override
    public TradeResponse getTradeById(Long id) {
        Trade trade = tradeRepository.findById(id)
                .orElseThrow(() -> new TradeNotFoundException(id));
        return tradeMapper.toResponse(trade);
    }

    @Override
    public void deleteTrade(Long id) {
        Trade trade = tradeRepository.findById(id)
                .orElseThrow(() -> new TradeNotFoundException(id));
        tradeRepository.delete(trade);
    }
}
