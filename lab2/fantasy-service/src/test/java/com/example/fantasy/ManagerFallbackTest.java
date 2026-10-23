package com.example.fantasy;
import com.example.fantasy.client.ManagerClientFallback;
import feign.*;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import io.github.resilience4j.circuitbreaker.*;
import java.time.Duration;
import java.util.Map;
import static org.assertj.core.api.Assertions.*;
class ManagerFallbackTest {
 @Test void notFoundIsNotDisguisedAsAvailabilityFailure() {
  var request=Request.create(Request.HttpMethod.GET,"http://manager/999",Map.of(),null,java.nio.charset.StandardCharsets.UTF_8,null);
  var failure=FeignException.errorStatus("get",Response.builder().request(request).status(404).reason("Not Found").build());
  assertThatThrownBy(()->new ManagerClientFallback().create(failure).get(999L)).isInstanceOfSatisfying(ResponseStatusException.class,e->assertThat(e.getStatusCode().value()).isEqualTo(404));
 }
 @Test void openCircuitFailsClosedWith503() {
  CircuitBreaker cb=CircuitBreaker.of("manager",CircuitBreakerConfig.custom().minimumNumberOfCalls(2).slidingWindowSize(2).waitDurationInOpenState(Duration.ofSeconds(10)).build());
  for(int i=0;i<2;i++) try {cb.executeSupplier(()->{throw new IllegalStateException("offline");});}catch(IllegalStateException ignored){}
  assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);
  assertThatThrownBy(()->cb.executeSupplier(()->"unreachable")).isInstanceOf(CallNotPermittedException.class);
  assertThatThrownBy(()->new ManagerClientFallback().create(CallNotPermittedException.createCallNotPermittedException(cb)).get(1L)).isInstanceOfSatisfying(ResponseStatusException.class,e->assertThat(e.getStatusCode().value()).isEqualTo(503));
 }
}
