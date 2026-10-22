package com.example.fantasy.controller;

import com.example.fantasy.dto.ApiDto.*;
import com.example.fantasy.service.TeamService;
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
@RequestMapping("${api.base-path}/fantasy-teams")
@Tag(name = "Fantasy teams")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService service;

    @PostMapping @Operation(summary = "Создать команду сразу с составом")
    public Mono<ResponseEntity<TeamView>> create(@Valid @RequestBody TeamInput input) {
        return Mono.<ResponseEntity<TeamView>>fromCallable(() -> {
            var result = service.create(input);
            return HttpPages.created("/api/v1/fantasy-teams/{id}", result.id(), result);
        }).subscribeOn(Schedulers.boundedElastic());
    }
    @GetMapping("/{id}") @Operation(summary = "Получить команду и состав")
    public Mono<ResponseEntity<TeamView>> get(@PathVariable Long id) {
        return Mono.<ResponseEntity<TeamView>>fromCallable(() -> {
            return ResponseEntity.ok(service.get(id));
        }).subscribeOn(Schedulers.boundedElastic());
    }
    @GetMapping @Operation(summary = "Список команд с X-Total-Count")
    public Mono<ResponseEntity<List<TeamView>>> list(@RequestParam(required = false) Long managerId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "20") int size) {
        return Mono.<ResponseEntity<List<TeamView>>>fromCallable(() -> {
            return HttpPages.withTotal(service.list(managerId, HttpPages.page(page, size)));
        }).subscribeOn(Schedulers.boundedElastic());
    }
    @PatchMapping("/{id}") @Operation(summary = "Переименовать команду")
    public Mono<ResponseEntity<TeamView>> rename(@PathVariable Long id, @Valid @RequestBody TeamNameInput input) {
        return Mono.<ResponseEntity<TeamView>>fromCallable(() -> {
            return ResponseEntity.ok(service.rename(id, input));
        }).subscribeOn(Schedulers.boundedElastic());
    }
    @PutMapping("/{id}/lineup") @Operation(summary = "Выбрать стартовый состав и капитана")
    public Mono<ResponseEntity<TeamView>> lineup(@PathVariable Long id, @Valid @RequestBody LineupInput input) {
        return Mono.<ResponseEntity<TeamView>>fromCallable(() -> {
            return ResponseEntity.ok(service.lineup(id, input));
        }).subscribeOn(Schedulers.boundedElastic());
    }
    @DeleteMapping("/{id}") @Operation(summary = "Удалить команду")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return Mono.<ResponseEntity<Void>>fromCallable(() -> {
            service.delete(id);
            return ResponseEntity.noContent().build();
        }).subscribeOn(Schedulers.boundedElastic());
    }
    @PostMapping("/{id}/transfers") @Operation(summary = "Совершить трансфер")
    public Mono<ResponseEntity<TransferView>> transfer(@PathVariable Long id, @Valid @RequestBody TransferInput input) {
        return Mono.<ResponseEntity<TransferView>>fromCallable(() -> {
            var result = service.transfer(id, input);
            return HttpPages.created("/api/v1/fantasy-teams/" + id + "/transfers/{id}", result.id(), result);
        }).subscribeOn(Schedulers.boundedElastic());
    }
    @GetMapping("/{id}/transfers") @Operation(summary = "История трансферов без общего количества")
    public Mono<ResponseEntity<ScrollView<TransferView>>> transfers(@PathVariable Long id, @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "20") int size) {
        return Mono.<ResponseEntity<ScrollView<TransferView>>>fromCallable(() -> {
            return ResponseEntity.ok(service.transfers(id, HttpPages.page(page, size)));
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
