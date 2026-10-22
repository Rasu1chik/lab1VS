package com.example.managerservice;

import java.time.Instant;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.*;

@RestControllerAdvice
public class ManagerErrors {
    public record FieldError(String field, String message) {}
    public record ErrorBody(Instant timestamp, int status, String error, String message,
                            String path, List<FieldError> errors) {}

    private ResponseEntity<ErrorBody> response(HttpStatusCode status, String message,
                                               ServerWebExchange exchange, List<FieldError> errors) {
        return ResponseEntity.status(status).body(new ErrorBody(Instant.now(), status.value(),
                HttpStatus.valueOf(status.value()).getReasonPhrase(), message,
                exchange.getRequest().getPath().value(), errors));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ErrorBody> duplicate(DataIntegrityViolationException e, ServerWebExchange x) {
        return response(HttpStatus.CONFLICT, "Имя или email уже используются", x, List.of());
    }

    @ExceptionHandler(WebExchangeBindException.class)
    ResponseEntity<ErrorBody> validation(WebExchangeBindException e, ServerWebExchange x) {
        return response(HttpStatus.BAD_REQUEST, "Ошибка валидации входных данных", x,
                e.getFieldErrors().stream().map(f -> new FieldError(f.getField(), f.getDefaultMessage())).toList());
    }

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ErrorBody> status(ResponseStatusException e, ServerWebExchange x) {
        return response(e.getStatusCode(), e.getReason(), x, List.of());
    }
}
