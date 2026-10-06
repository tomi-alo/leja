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
import java.util.function.Consumer;

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

        setIfPresent(request.symbol(), trade::setSymbol);
        setIfPresent(request.direction(), value -> trade.setDirection(Direction.valueOf(value.toUpperCase())));
        setIfPresent(request.session(), value -> trade.setSession(Session.valueOf(value.toUpperCase())));
        setIfPresent(request.positionSize(), value -> trade.setPositionSize(new BigDecimal(value)));
        setIfPresent(request.contractSize(), value -> trade.setContractSize(new BigDecimal(value)));
        setIfPresent(request.entryPrice(), value -> trade.setEntryPrice(new BigDecimal(value)));
        setIfPresent(request.exitPrice(), value -> trade.setExitPrice(new BigDecimal(value)));
        setIfPresent(request.stopLoss(), value -> trade.setStopLoss(new BigDecimal(value)));
        setIfPresent(request.takeProfit(), value -> trade.setTakeProfit(new BigDecimal(value)));

        if (request.reason() != null) {
            trade.setReason(request.reason().isBlank() ? null : request.reason().trim());
        }

        if (request.executedAt() != null) {
            trade.setExecutedAt(request.executedAt());
        }

        trade.checkPriceLevels();
        Trade updatedTrade = tradeRepository.save(trade);
        return tradeMapper.toResponse(updatedTrade);
    }

    private void setIfPresent(String value, Consumer<String> setter) {
        if (value != null && !value.isBlank()) {
            setter.accept(value.trim());
        }
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
