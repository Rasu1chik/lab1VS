package com.example.fantasy.controller;

import com.example.fantasy.dto.ApiDto.*;
import com.example.fantasy.service.LeagueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/leagues")
@Tag(name = "Leagues")
public class LeagueController {
    private final LeagueService service;
    public LeagueController(LeagueService service) { this.service = service; }

    @PostMapping @Operation(summary = "Создать лигу")
    public ResponseEntity<LeagueView> create(@Valid @RequestBody LeagueInput input) {
        var result = service.create(input);
        return HttpPages.created("/api/v1/leagues/{id}", result.id(), result);
    }
    @GetMapping("/{id}") @Operation(summary = "Получить лигу")
    public LeagueView get(@PathVariable Long id) { return service.get(id); }
    @GetMapping @Operation(summary = "Список лиг с X-Total-Count")
    public ResponseEntity<List<LeagueView>> list(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size) {
        return HttpPages.withTotal(service.list(HttpPages.page(page, size)));
    }
    @PutMapping("/{id}") @Operation(summary = "Обновить лигу")
    public LeagueView update(@PathVariable Long id, @Valid @RequestBody LeagueInput input) {
        return service.update(id, input);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @Operation(summary = "Удалить лигу")
    public void delete(@PathVariable Long id) { service.delete(id); }
    @PostMapping("/{id}/teams") @ResponseStatus(HttpStatus.NO_CONTENT) @Operation(summary = "Вступить в лигу")
    public void join(@PathVariable Long id, @Valid @RequestBody JoinLeagueInput input) { service.join(id, input); }
    @DeleteMapping("/{id}/teams/{teamId}") @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Покинуть лигу")
    public void leave(@PathVariable Long id, @PathVariable Long teamId) { service.leave(id, teamId); }
    @GetMapping("/{id}/standings") @Operation(summary = "Таблица лиги с X-Total-Count")
    public ResponseEntity<List<TeamView>> standings(@PathVariable Long id,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "20") int size) {
        return HttpPages.withTotal(service.standings(id, HttpPages.page(page, size)));
    }
}
