package com.example.fantasy.controller;

import com.example.fantasy.dto.ApiDto.*;
import com.example.fantasy.service.ClubService;
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
@RequestMapping("${api.base-path}/clubs")
@Tag(name = "Club")
@RequiredArgsConstructor
public class ClubController {
    private final ClubService service;

    @PostMapping @Operation(summary = "Создать club")
    public Mono<ResponseEntity<ClubView>> create(@Valid @RequestBody ClubInput input) {
        return Mono.<ResponseEntity<ClubView>>fromCallable(() -> {
            var result = service.create(input);
            return HttpPages.created("/api/v1/clubs/{id}", result.id(), result);
        }).subscribeOn(Schedulers.boundedElastic());
    }
    @GetMapping("/{id}") @Operation(summary = "Получить club")
    public Mono<ResponseEntity<ClubView>> get(@PathVariable Long id) {
        return Mono.<ResponseEntity<ClubView>>fromCallable(() -> {
            return ResponseEntity.ok(service.get(id));
        }).subscribeOn(Schedulers.boundedElastic());
    }
    @GetMapping @Operation(summary = "Список с общим количеством в X-Total-Count")
    public Mono<ResponseEntity<List<ClubView>>> list(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "20") int size) {
        return Mono.<ResponseEntity<List<ClubView>>>fromCallable(() -> {
            return HttpPages.withTotal(service.list(HttpPages.page(page, size)));
        }).subscribeOn(Schedulers.boundedElastic());
    }
    @PutMapping("/{id}") @Operation(summary = "Обновить club")
    public Mono<ResponseEntity<ClubView>> update(@PathVariable Long id, @Valid @RequestBody ClubInput input) {
        return Mono.<ResponseEntity<ClubView>>fromCallable(() -> {
            return ResponseEntity.ok(service.update(id, input));
        }).subscribeOn(Schedulers.boundedElastic());
    }
    @DeleteMapping("/{id}") @Operation(summary = "Удалить club")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return Mono.<ResponseEntity<Void>>fromCallable(() -> {
            service.delete(id);
            return ResponseEntity.noContent().build();
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
