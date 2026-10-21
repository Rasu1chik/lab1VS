package com.example.fantasy.controller;

import com.example.fantasy.dto.ApiDto.*;
import com.example.fantasy.entity.Position;
import com.example.fantasy.service.CatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/players")
@Tag(name = "Players")
public class PlayerController {
    private final CatalogService service;
    public PlayerController(CatalogService service) { this.service = service; }

    @PostMapping @Operation(summary = "Создать игрока")
    public ResponseEntity<PlayerView> create(@Valid @RequestBody PlayerInput input) {
        var result = service.createPlayer(input);
        return HttpPages.created("/api/v1/players/{id}", result.id(), result);
    }
    @GetMapping("/{id}") @Operation(summary = "Получить игрока")
    public PlayerView get(@PathVariable Long id) { return service.getPlayer(id); }
    @GetMapping @Operation(summary = "Рынок игроков: прокрутка без COUNT запроса")
    public ScrollView<PlayerView> list(@RequestParam(required = false) Position position,
                                       @RequestParam(required = false) Long clubId,
                                       @RequestParam(required = false) BigDecimal maxPrice,
                                       @RequestParam(required = false) String name,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size) {
        return service.players(position, clubId, maxPrice, name, HttpPages.page(page, size));
    }
    @PutMapping("/{id}") @Operation(summary = "Обновить игрока")
    public PlayerView update(@PathVariable Long id, @Valid @RequestBody PlayerInput input) {
        return service.updatePlayer(id, input);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @Operation(summary = "Удалить игрока")
    public void delete(@PathVariable Long id) { service.deletePlayer(id); }
}
