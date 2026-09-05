package com.alooa.leja.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        int statusCode,
        List<String> errors,
        LocalDateTime timestamp
) {
}
