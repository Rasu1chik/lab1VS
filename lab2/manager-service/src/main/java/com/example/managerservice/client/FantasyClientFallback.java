package com.example.managerservice.client;
import feign.FeignException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
@Component
public class FantasyClientFallback implements FallbackFactory<FantasyClient> {
 public FantasyClient create(Throwable cause) {
  return id -> {
   Throwable error=cause;
   while(error.getCause()!=null && !(error instanceof FeignException)) error=error.getCause();
   if(error instanceof FeignException fe && fe.status()==404) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Менеджер не найден");
   throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Сервис fantasy-service недоступен, повторите позже",cause);
  };
 }
}
