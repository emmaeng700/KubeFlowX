package com.microservices.orchestration.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles Kubernetes API exceptions.
     *
     * @param e The Kubernetes API exception
     * @return ResponseEntity containing the error details
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException e) {
        log.error("Kubernetes API error: {} - Status code: {}", e.getMessage(), e.getCode());
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Kubernetes API error: " + e.getMessage());
        response.put("timestamp", LocalDateTime.now());
        response.put("status", e.getCode());
        return ResponseEntity.status(e.getCode()).body(response);
    }

    /**
     * Handles JSON processing exceptions.
     *
     * @param e The JSON processing exception
     * @return ResponseEntity containing the error details
     */
    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<Map<String, Object>> handleJsonProcessingException(JsonProcessingException e) {
        log.error("JSON processing error: {}", e.getMessage(), e);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "JSON processing error: " + e.getMessage());
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles general exceptions.
     *
     * @param e The exception
     * @return ResponseEntity containing the error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpectedException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "An unexpected error occurred: " + e.getMessage());
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
} 