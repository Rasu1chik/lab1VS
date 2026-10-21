package com.example.fantasy.controller;

import com.example.fantasy.dto.ApiDto.*;
import com.example.fantasy.entity.Position;
import com.example.fantasy.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base-path}/players")
@Tag(name = "Players")
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService service;

    @PostMapping @Operation(summary = "Создать игрока")
    public ResponseEntity<PlayerView> create(@Valid @RequestBody PlayerInput input) {
        var result = service.create(input);
        return HttpPages.created("/api/v1/players/{id}", result.id(), result);
    }
    @GetMapping("/{id}") @Operation(summary = "Получить игрока")
    public ResponseEntity<PlayerView> get(@PathVariable Long id) { return ResponseEntity.ok(service.get(id)); }
    @GetMapping @Operation(summary = "Рынок игроков: прокрутка без COUNT запроса")
    public ResponseEntity<ScrollView<PlayerView>> list(@RequestParam(required = false) Position position,
                                       @RequestParam(required = false) Long clubId,
                                       @RequestParam(required = false) BigDecimal maxPrice,
                                       @RequestParam(required = false) String name,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.list(position, clubId, maxPrice, name, HttpPages.page(page, size)));
    }
    @PutMapping("/{id}") @Operation(summary = "Обновить игрока")
    public ResponseEntity<PlayerView> update(@PathVariable Long id, @Valid @RequestBody PlayerInput input) {
        return ResponseEntity.ok(service.update(id, input));
    }
    @DeleteMapping("/{id}") @Operation(summary = "Удалить игрока")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
