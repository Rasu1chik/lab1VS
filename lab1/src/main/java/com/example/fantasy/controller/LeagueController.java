package com.example.fantasy.controller;

import com.example.fantasy.dto.ApiDto.*;
import com.example.fantasy.service.LeagueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    public ResponseEntity<LeagueView> create(@Valid @RequestBody LeagueInput input) {
        var result = service.create(input);
        return HttpPages.created("/api/v1/leagues/{id}", result.id(), result);
    }
    @GetMapping("/{id}") @Operation(summary = "Получить лигу")
    public ResponseEntity<LeagueView> get(@PathVariable Long id) { return ResponseEntity.ok(service.get(id)); }
    @GetMapping @Operation(summary = "Список лиг с X-Total-Count")
    public ResponseEntity<List<LeagueView>> list(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size) {
        return HttpPages.withTotal(service.list(HttpPages.page(page, size)));
    }
    @PutMapping("/{id}") @Operation(summary = "Обновить лигу")
    public ResponseEntity<LeagueView> update(@PathVariable Long id, @Valid @RequestBody LeagueInput input) {
        return ResponseEntity.ok(service.update(id, input));
    }
    @DeleteMapping("/{id}") @Operation(summary = "Удалить лигу")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{id}/teams") @Operation(summary = "Вступить в лигу")
    public ResponseEntity<Void> join(@PathVariable Long id, @Valid @RequestBody JoinLeagueInput input) {
        service.join(id, input);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{id}/teams/{teamId}") @Operation(summary = "Покинуть лигу")
    public ResponseEntity<Void> leave(@PathVariable Long id, @PathVariable Long teamId) {
        service.leave(id, teamId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}/standings") @Operation(summary = "Таблица лиги с X-Total-Count")
    public ResponseEntity<List<TeamView>> standings(@PathVariable Long id,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "20") int size) {
        return HttpPages.withTotal(service.standings(id, HttpPages.page(page, size)));
    }
}
