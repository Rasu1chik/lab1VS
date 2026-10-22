package com.example.fantasy.controller;

import com.example.fantasy.dto.ApiDto.*;
import com.example.fantasy.service.GameweekService;
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
@RequestMapping("${api.base-path}/gameweeks")
@Tag(name = "Gameweeks")
@RequiredArgsConstructor
public class GameweekController {
    private final GameweekService service;

    @PostMapping @Operation(summary = "Создать тур")
    public Mono<ResponseEntity<GameweekView>> create(@Valid @RequestBody GameweekInput input) {
        return Mono.<ResponseEntity<GameweekView>>fromCallable(() -> {
            var result = service.create(input);
            return HttpPages.created("/api/v1/gameweeks/{id}", result.id(), result);
        }).subscribeOn(Schedulers.boundedElastic());
    }
    @GetMapping("/{id}") @Operation(summary = "Получить тур")
    public Mono<ResponseEntity<GameweekView>> get(@PathVariable Long id) {
        return Mono.<ResponseEntity<GameweekView>>fromCallable(() -> {
            return ResponseEntity.ok(service.get(id));
        }).subscribeOn(Schedulers.boundedElastic());
    }
    @GetMapping @Operation(summary = "Список туров с X-Total-Count")
    public Mono<ResponseEntity<List<GameweekView>>> list(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "20") int size) {
        return Mono.<ResponseEntity<List<GameweekView>>>fromCallable(() -> {
            return HttpPages.withTotal(service.list(HttpPages.page(page, size)));
        }).subscribeOn(Schedulers.boundedElastic());
    }
    @PutMapping("/{id}") @Operation(summary = "Обновить предстоящий тур")
    public Mono<ResponseEntity<GameweekView>> update(@PathVariable Long id, @Valid @RequestBody GameweekInput input) {
        return Mono.<ResponseEntity<GameweekView>>fromCallable(() -> {
            return ResponseEntity.ok(service.update(id, input));
        }).subscribeOn(Schedulers.boundedElastic());
    }
    @DeleteMapping("/{id}") @Operation(summary = "Удалить предстоящий тур")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return Mono.<ResponseEntity<Void>>fromCallable(() -> {
            service.delete(id);
            return ResponseEntity.noContent().build();
        }).subscribeOn(Schedulers.boundedElastic());
    }
    @PostMapping("/{id}/start") @Operation(summary = "Начать тур")
    public Mono<ResponseEntity<GameweekView>> start(@PathVariable Long id) {
        return Mono.<ResponseEntity<GameweekView>>fromCallable(() -> {
            return ResponseEntity.ok(service.start(id));
        }).subscribeOn(Schedulers.boundedElastic());
    }
    @PutMapping("/{id}/performances") @Operation(summary = "Внести или исправить статистику игрока")
    public Mono<ResponseEntity<PerformanceView>> performance(@PathVariable Long id, @Valid @RequestBody PerformanceInput input) {
        return Mono.<ResponseEntity<PerformanceView>>fromCallable(() -> {
            return ResponseEntity.ok(service.record(id, input));
        }).subscribeOn(Schedulers.boundedElastic());
    }
    @GetMapping("/{id}/performances") @Operation(summary = "Статистика тура с X-Total-Count")
    public Mono<ResponseEntity<List<PerformanceView>>> performances(@PathVariable Long id,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "20") int size) {
        return Mono.<ResponseEntity<List<PerformanceView>>>fromCallable(() -> {
            return HttpPages.withTotal(service.performances(id, HttpPages.page(page, size)));
        }).subscribeOn(Schedulers.boundedElastic());
    }
    @PostMapping("/{id}/finish") @Operation(summary = "Закрыть тур и начислить очки")
    public Mono<ResponseEntity<GameweekView>> finish(@PathVariable Long id) {
        return Mono.<ResponseEntity<GameweekView>>fromCallable(() -> {
            return ResponseEntity.ok(service.finish(id));
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
