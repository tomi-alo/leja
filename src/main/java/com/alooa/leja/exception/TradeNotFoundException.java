package com.alooa.leja.exception;

public class TradeNotFoundException extends RuntimeException {
    private final Long id;

    public TradeNotFoundException(Long id) {
        super("Trade not found with ID: " + id);
        this.id = id;
    }
}
