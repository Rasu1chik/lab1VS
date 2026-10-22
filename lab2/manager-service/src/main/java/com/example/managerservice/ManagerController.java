package com.example.managerservice;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import com.example.managerservice.client.FantasyClient;
@RestController
@RequestMapping("/api/v1/managers")
public class ManagerController {
 public record Input(@NotBlank @Size(max=50) String username,@NotBlank @Email @Size(max=255) String email) {}
 private final ManagerRepository repository;
 private final FantasyClient fantasy;
 public ManagerController(ManagerRepository repository,FantasyClient fantasy) {this.repository=repository;this.fantasy=fantasy;}
 private Mono<Manager> require(Long id) {return repository.findById(id).switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,"Менеджер не найден")));}
 @PostMapping
 public Mono<ResponseEntity<Manager>> create(@Valid @RequestBody Input input) {
  return repository.save(new Manager(null,input.username(),input.email(),Instant.now()))
    .map(m->ResponseEntity.created(URI.create("/api/v1/managers/"+m.id())).body(m));
 }
 @GetMapping("/{id}") public Mono<Manager> get(@PathVariable Long id) {return require(id);}
 @GetMapping public Mono<ResponseEntity<List<Manager>>> list(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size) {
  if(page<0||size<1||size>50) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"page >= 0, size от 1 до 50");
  return Mono.zip(repository.page(size,(long)page*size).collectList(),repository.count())
    .map(t->ResponseEntity.ok().header("X-Total-Count",t.getT2().toString()).body(t.getT1()));
 }
 @PutMapping("/{id}") public Mono<Manager> update(@PathVariable Long id,@Valid @RequestBody Input input) {
  return require(id).flatMap(m->repository.save(new Manager(m.id(),input.username(),input.email(),m.createdAt())));
 }
 @DeleteMapping("/{id}") public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
  return require(id).flatMap(m->Mono.fromCallable(()->fantasy.references(id)).subscribeOn(Schedulers.boundedElastic()))
    .flatMap(used->used ? Mono.<Void>error(new ResponseStatusException(HttpStatus.CONFLICT,"У менеджера есть команды или лиги")) : repository.deleteById(id))
    .thenReturn(ResponseEntity.noContent().build());
 }
}
