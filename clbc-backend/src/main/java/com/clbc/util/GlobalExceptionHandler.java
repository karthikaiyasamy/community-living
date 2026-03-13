package com.clbc.util;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", "Authentication failed: " + ex.getMessage());
        return ResponseEntity.status(401).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception ex) {
        System.err.println("Unhandled Exception: " + ex.getMessage());
        ex.printStackTrace();
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", "An internal error occurred: " + ex.getMessage());
        return ResponseEntity.status(500).body(body);
    }
}
