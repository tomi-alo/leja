package com.alooa.leja.service;

import com.alooa.leja.dto.trades.CreateTradeRequest;
import com.alooa.leja.dto.trades.TradeResponse;
import com.alooa.leja.dto.trades.UpdateTradeRequest;
import com.alooa.leja.exception.TradeNotFoundException;
import com.alooa.leja.mapper.TradeMapper;
import com.alooa.leja.model.trades.Direction;
import com.alooa.leja.model.trades.Session;
import com.alooa.leja.model.trades.Trade;
import com.alooa.leja.repository.TradeRepository;
import org.springframework.stereotype.Service;

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
            trade.setPositionSize(Double.parseDouble(request.positionSize().trim()));
        }

        if (request.pnl() != null && !request.pnl().isBlank()) {
            trade.setPnl(Double.parseDouble(request.pnl().trim()));
        }

        if (request.reason() != null && !request.reason().isBlank()) {
            trade.setReason(request.reason());
        }

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
