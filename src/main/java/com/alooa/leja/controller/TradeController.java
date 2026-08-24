package com.alooa.leja.controller;

import com.alooa.leja.dto.trades.CreateTradeRequest;
import com.alooa.leja.dto.trades.TradeResponse;
import com.alooa.leja.dto.trades.UpdateTradeRequest;
import com.alooa.leja.service.TradeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trades") // sets api controller url
public class TradeController {
    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping
    public ResponseEntity<TradeResponse> createTrade(@Valid @RequestBody CreateTradeRequest request) {
        //returning a response entity of type trade response
        TradeResponse response = tradeService.createTrade(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TradeResponse>> getAllTrades() {
        List<TradeResponse> response = tradeService.getAllTrades();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TradeResponse> getTrade(@PathVariable Long id) {
        TradeResponse response = tradeService.getTradeById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TradeResponse> updateTrade(@PathVariable Long id, @Valid @RequestBody UpdateTradeRequest request) {
        TradeResponse response = tradeService.updateTrade(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrade(@PathVariable Long id) {
        tradeService.deleteTrade(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
