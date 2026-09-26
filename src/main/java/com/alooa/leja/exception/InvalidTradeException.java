package com.alooa.leja.exception;

import java.util.List;

public class InvalidTradeException extends RuntimeException {
    private final List<String> errors;

    public InvalidTradeException(List<String> errors) {
        super(String.join("; ", errors));
        this.errors = List.copyOf(errors);
    }

    public List<String> getErrors() {
        return errors;
    }
}
