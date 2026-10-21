package com.example.fantasy.controller;

import com.example.fantasy.dto.ApiDto.*;
import com.example.fantasy.service.GameweekService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/gameweeks")
@Tag(name = "Gameweeks")
public class GameweekController {
    private final GameweekService service;
    public GameweekController(GameweekService service) { this.service = service; }

    @PostMapping @Operation(summary = "Создать тур")
    public ResponseEntity<GameweekView> create(@Valid @RequestBody GameweekInput input) {
        var result = service.create(input);
        return HttpPages.created("/api/v1/gameweeks/{id}", result.id(), result);
    }
    @GetMapping("/{id}") @Operation(summary = "Получить тур")
    public GameweekView get(@PathVariable Long id) { return service.get(id); }
    @GetMapping @Operation(summary = "Список туров с X-Total-Count")
    public ResponseEntity<List<GameweekView>> list(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "20") int size) {
        return HttpPages.withTotal(service.list(HttpPages.page(page, size)));
    }
    @PutMapping("/{id}") @Operation(summary = "Обновить предстоящий тур")
    public GameweekView update(@PathVariable Long id, @Valid @RequestBody GameweekInput input) {
        return service.update(id, input);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @Operation(summary = "Удалить предстоящий тур")
    public void delete(@PathVariable Long id) { service.delete(id); }
    @PostMapping("/{id}/start") @Operation(summary = "Начать тур")
    public GameweekView start(@PathVariable Long id) { return service.start(id); }
    @PutMapping("/{id}/performances") @Operation(summary = "Внести или исправить статистику игрока")
    public PerformanceView performance(@PathVariable Long id, @Valid @RequestBody PerformanceInput input) {
        return service.record(id, input);
    }
    @GetMapping("/{id}/performances") @Operation(summary = "Статистика тура с X-Total-Count")
    public ResponseEntity<List<PerformanceView>> performances(@PathVariable Long id,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "20") int size) {
        return HttpPages.withTotal(service.performances(id, HttpPages.page(page, size)));
    }
    @PostMapping("/{id}/finish") @Operation(summary = "Закрыть тур и начислить очки")
    public GameweekView finish(@PathVariable Long id) { return service.finish(id); }
}
