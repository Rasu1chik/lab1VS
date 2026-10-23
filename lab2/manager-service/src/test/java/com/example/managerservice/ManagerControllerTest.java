package com.example.managerservice;
import com.example.managerservice.client.FantasyClient;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
class ManagerControllerTest {
 final ManagerRepository repo=mock(ManagerRepository.class);
 final FantasyClient fantasy=mock(FantasyClient.class);
 final ManagerController controller=new ManagerController(repo,fantasy);
 final Manager manager=new Manager(1L,"owner","owner@test.org",Instant.now());
 @Test void missingManagerIs404() {
  when(repo.findById(1L)).thenReturn(Mono.empty());
  StepVerifier.create(controller.get(1L)).expectErrorSatisfies(e->assertThat(((ResponseStatusException)e).getStatusCode().value()).isEqualTo(404)).verify();
 }
 @Test void deletionChecksReferencesOnWorkerAndRejectsUsedManager() {
  when(repo.findById(1L)).thenReturn(Mono.just(manager));
  AtomicReference<String> thread=new AtomicReference<>();
  when(fantasy.references(1L)).thenAnswer(i->{thread.set(Thread.currentThread().getName());return true;});
  StepVerifier.create(controller.delete(1L)).expectErrorSatisfies(e->assertThat(((ResponseStatusException)e).getStatusCode().value()).isEqualTo(409)).verify();
  assertThat(thread.get()).startsWith("boundedElastic-");verify(repo,never()).deleteById(anyLong());
 }
 @Test void dependencyFailureDoesNotDeleteManager() {
  when(repo.findById(1L)).thenReturn(Mono.just(manager));
  when(fantasy.references(1L)).thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE));
  StepVerifier.create(controller.delete(1L)).expectErrorSatisfies(e->assertThat(((ResponseStatusException)e).getStatusCode().value()).isEqualTo(503)).verify();
  verify(repo,never()).deleteById(anyLong());
 }
 @Test void unreferencedManagerIsDeleted() {
  when(repo.findById(1L)).thenReturn(Mono.just(manager));when(fantasy.references(1L)).thenReturn(false);when(repo.deleteById(1L)).thenReturn(Mono.empty());
  StepVerifier.create(controller.delete(1L)).assertNext(r->assertThat(r.getStatusCode().value()).isEqualTo(204)).verifyComplete();
 }
}
