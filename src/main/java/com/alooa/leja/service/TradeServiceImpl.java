package com.alooa.leja.service;

import com.alooa.leja.dto.CreateTradeRequest;
import com.alooa.leja.dto.TradeResponse;
import com.alooa.leja.dto.UpdateTradeRequest;
import com.alooa.leja.mapper.TradeMapper;
import com.alooa.leja.model.Trade;
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
                .orElseThrow(() -> new RuntimeException("Trade not found with id " + id));

        trade.setSymbol(request.symbol() != null && !request.symbol().isBlank() ? request.symbol() : trade.getSymbol());
        trade.setDirection(request.direction() != null ? request.direction() : trade.getDirection());
        trade.setSession(request.session() != null ? request.session() : trade.getSession());
        trade.setPositionSize(request.positionSize() != null ? request.positionSize() : trade.getPositionSize());
        trade.setPnl(request.pnl() != null ? request.pnl() : trade.getPnl());
        trade.setReason(request.reason() != null && !request.reason().isBlank() ? request.reason() : trade.getReason());

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
                .orElseThrow(() -> new RuntimeException("Trade not found with id " + id));
        return tradeMapper.toResponse(trade);
    }

    @Override
    public void deleteTrade(Long id) {
        if(tradeRepository.existsById(id)) {
            tradeRepository.deleteById(id);
        }
        else{
            throw new RuntimeException("Trade not found with id " + id);
        }
    }
}
