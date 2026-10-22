package com.example.fantasy.controller;

import com.example.fantasy.dto.ApiDto.*;
import com.example.fantasy.service.LeagueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base-path}/leagues")
@Tag(name = "Leagues")
@RequiredArgsConstructor
public class LeagueController {
    private final LeagueService service;

    @PostMapping @Operation(summary = "Создать лигу")
    public Mono<ResponseEntity<LeagueView>> create(@Valid @RequestBody LeagueInput input) {
        return Mono.<ResponseEntity<LeagueView>>fromCallable(() -> {
            var result = service.create(input);
            return HttpPages.created("/api/v1/leagues/{id}", result.id(), result);
        }).subscribeOn(Schedulers.boundedElastic());
    }
    @GetMapping("/{id}") @Operation(summary = "Получить лигу")
    public Mono<ResponseEntity<LeagueView>> get(@PathVariable Long id) {
        return Mono.<ResponseEntity<LeagueView>>fromCallable(() -> {
            return ResponseEntity.ok(service.get(id));
        }).subscribeOn(Schedulers.boundedElastic());
    }
    @GetMapping @Operation(summary = "Список лиг с X-Total-Count")
    public Mono<ResponseEntity<List<LeagueView>>> list(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size) {
        return Mono.<ResponseEntity<List<LeagueView>>>fromCallable(() -> {
            return HttpPages.withTotal(service.list(HttpPages.page(page, size)));
        }).subscribeOn(Schedulers.boundedElastic());
    }
    @PutMapping("/{id}") @Operation(summary = "Обновить лигу")
    public Mono<ResponseEntity<LeagueView>> update(@PathVariable Long id, @Valid @RequestBody LeagueInput input) {
        return Mono.<ResponseEntity<LeagueView>>fromCallable(() -> {
            return ResponseEntity.ok(service.update(id, input));
        }).subscribeOn(Schedulers.boundedElastic());
    }
    @DeleteMapping("/{id}") @Operation(summary = "Удалить лигу")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return Mono.<ResponseEntity<Void>>fromCallable(() -> {
            service.delete(id);
            return ResponseEntity.noContent().build();
        }).subscribeOn(Schedulers.boundedElastic());
    }
    @PostMapping("/{id}/teams") @Operation(summary = "Вступить в лигу")
    public Mono<ResponseEntity<Void>> join(@PathVariable Long id, @Valid @RequestBody JoinLeagueInput input) {
        return Mono.<ResponseEntity<Void>>fromCallable(() -> {
            service.join(id, input);
            return ResponseEntity.noContent().build();
        }).subscribeOn(Schedulers.boundedElastic());
    }
    @DeleteMapping("/{id}/teams/{teamId}") @Operation(summary = "Покинуть лигу")
    public Mono<ResponseEntity<Void>> leave(@PathVariable Long id, @PathVariable Long teamId) {
        return Mono.<ResponseEntity<Void>>fromCallable(() -> {
            service.leave(id, teamId);
            return ResponseEntity.noContent().build();
        }).subscribeOn(Schedulers.boundedElastic());
    }
    @GetMapping("/{id}/standings") @Operation(summary = "Таблица лиги с X-Total-Count")
    public Mono<ResponseEntity<List<TeamView>>> standings(@PathVariable Long id,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "20") int size) {
        return Mono.<ResponseEntity<List<TeamView>>>fromCallable(() -> {
            return HttpPages.withTotal(service.standings(id, HttpPages.page(page, size)));
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
