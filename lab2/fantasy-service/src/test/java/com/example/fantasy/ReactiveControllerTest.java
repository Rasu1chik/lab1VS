package com.example.fantasy;
import com.example.fantasy.controller.ClubController;
import com.example.fantasy.dto.ApiDto.ClubView;
import com.example.fantasy.service.ClubService;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import java.util.concurrent.atomic.AtomicReference;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
class ReactiveControllerTest {
 @Test void jpaServiceCallIsDeferredAndRunsOutsideEventLoop() {
  ClubService service=mock(ClubService.class);AtomicReference<String> thread=new AtomicReference<>();
  when(service.get(1L)).thenAnswer(i->{thread.set(Thread.currentThread().getName());return new ClubView(1L,"Club","CLU","England");});
  var result=new ClubController(service).get(1L);verifyNoInteractions(service);
  StepVerifier.create(result).assertNext(r->assertThat(r.getBody().id()).isEqualTo(1L)).verifyComplete();
  assertThat(thread.get()).startsWith("boundedElastic-");
 }
}
