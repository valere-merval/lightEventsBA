package com.lightevents.shared;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiErrorHandler {
    @ExceptionHandler(ApiException.class)
    ResponseEntity<Map<String, Object>> api(ApiException ex) {
        return ResponseEntity.status(ex.status()).body(Map.of(
            "timestamp", Instant.now().toString(),
            "status", ex.status().value(),
            "error", ex.status().getReasonPhrase(),
            "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(Map.of(
            "timestamp", Instant.now().toString(),
            "status", 400,
            "error", "Validation failed",
            "message", message
        ));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String, Object>> fallback(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
            "timestamp", Instant.now().toString(),
            "status", 500,
            "error", "Internal server error",
            "message", ex.getMessage()
        ));
    }
}
