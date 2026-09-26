package com.alooa.leja.controller;

import com.alooa.leja.dto.ErrorResponse;
import com.alooa.leja.exception.InvalidTradeException;
import com.alooa.leja.exception.TradeNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // FOR TRADE NOT FOUND ERRORS
    @ExceptionHandler(TradeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTradeNotFoundException(TradeNotFoundException ex){
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                List.of(ex.getMessage()),
                Instant.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // FOR DTO VALIDATION ERRORS
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex){
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                errors, // Sends the full list of errors at once
                Instant.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // FOR MALFORMED JSON OR TYPE MISMATCHES
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String detailMessage = "Malformed JSON payload or syntax error";

        if (ex.getCause() instanceof InvalidFormatException ife) {
            // getPathReference() automatically formats the field path (e.g., "positionSize")
            String fieldName = ife.getPathReference() != null && !ife.getPathReference().isBlank()
                    ? ife.getPathReference()
                    : "unknown field";

            // Strips package names if getPathReference returns something like "CreateTradeRequest[\"positionSize\"]"
            if (fieldName.contains("\"")) {
                fieldName = fieldName.substring(fieldName.indexOf("\"") + 1, fieldName.lastIndexOf("\""));
            }

            String targetType = ife.getTargetType() != null ? ife.getTargetType().getSimpleName() : "valid type";

            detailMessage = String.format("%s: Invalid value '%s'. Expected type: %s",
                    fieldName, ife.getValue(), targetType);
        }

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                List.of(detailMessage),
                Instant.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    //FOR INVALID TRADE LOGIC ERRORS
    @ExceptionHandler(InvalidTradeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTrade(InvalidTradeException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getErrors(),
                Instant.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // FOR UNEXPECTED SERVER ERRORS
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex){
        log.error("Unhandled error", ex);
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                List.of("Unexpected error"),
                Instant.now()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
