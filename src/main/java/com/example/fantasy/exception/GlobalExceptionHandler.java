package com.example.fantasy.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    public record FieldError(String field, String message) {}
    public record ErrorBody(Instant timestamp, int status, String error, String message,
                            String path, List<FieldError> errors) {}

    private ResponseEntity<ErrorBody> response(HttpStatus status, String message,
                                               HttpServletRequest request, List<FieldError> errors) {
        return ResponseEntity.status(status).body(new ErrorBody(Instant.now(), status.value(),
                status.getReasonPhrase(), message, request.getRequestURI(), errors));
    }

    @ExceptionHandler(ApiException.class)
    ResponseEntity<ErrorBody> api(ApiException exception, HttpServletRequest request) {
        return response(exception.status(), exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorBody> validation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        var fields = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldError(error.getField(), error.getDefaultMessage())).toList();
        return response(HttpStatus.BAD_REQUEST, "Ошибка валидации входных данных", request, fields);
    }

    @ExceptionHandler({ConstraintViolationException.class, HttpMessageNotReadableException.class,
            IllegalArgumentException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<ErrorBody> badRequest(Exception exception, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "Некорректный запрос: " + exception.getMessage(), request, List.of());
    }

    @ExceptionHandler({DataIntegrityViolationException.class, ObjectOptimisticLockingFailureException.class})
    ResponseEntity<ErrorBody> conflict(Exception exception, HttpServletRequest request) {
        return response(HttpStatus.CONFLICT, "Данные уже используются или были изменены другим запросом", request, List.of());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorBody> unexpected(Exception exception, HttpServletRequest request) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера", request, List.of());
    }
}
