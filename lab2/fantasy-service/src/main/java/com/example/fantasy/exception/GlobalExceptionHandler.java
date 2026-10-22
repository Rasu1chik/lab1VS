package com.example.fantasy.exception;
import java.time.Instant;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
@RestControllerAdvice
public class GlobalExceptionHandler {
 public record FieldError(String field, String message) {}
 public record ErrorBody(Instant timestamp,int status,String error,String message,String path,List<FieldError> errors) {}
 private ResponseEntity<ErrorBody> response(HttpStatusCode status,String message,ServerWebExchange exchange,List<FieldError> errors) {
  return ResponseEntity.status(status).body(new ErrorBody(Instant.now(),status.value(),HttpStatus.valueOf(status.value()).getReasonPhrase(),message,exchange.getRequest().getPath().value(),errors));
 }
 @ExceptionHandler(ApiException.class)
 ResponseEntity<ErrorBody> api(ApiException e,ServerWebExchange x) {return response(e.status(),e.getMessage(),x,List.of());}
 @ExceptionHandler(WebExchangeBindException.class)
 ResponseEntity<ErrorBody> validation(WebExchangeBindException e,ServerWebExchange x) {
  return response(HttpStatus.BAD_REQUEST,"Ошибка валидации",x,e.getFieldErrors().stream().map(f->new FieldError(f.getField(),f.getDefaultMessage())).toList());
 }
 @ExceptionHandler(ResponseStatusException.class)
 ResponseEntity<ErrorBody> status(ResponseStatusException e,ServerWebExchange x) {return response(e.getStatusCode(),e.getReason(),x,List.of());}
 @ExceptionHandler({org.springframework.web.server.ServerWebInputException.class,IllegalArgumentException.class,jakarta.validation.ConstraintViolationException.class})
 ResponseEntity<ErrorBody> bad(Exception e,ServerWebExchange x) {return response(HttpStatus.BAD_REQUEST,"Некорректный запрос",x,List.of());}
 @ExceptionHandler({DataIntegrityViolationException.class,ObjectOptimisticLockingFailureException.class})
 ResponseEntity<ErrorBody> conflict(Exception e,ServerWebExchange x) {return response(HttpStatus.CONFLICT,"Данные уже используются или изменены",x,List.of());}
}
