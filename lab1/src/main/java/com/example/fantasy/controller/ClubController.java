package com.example.fantasy.controller;

import com.example.fantasy.dto.ApiDto.*;
import com.example.fantasy.service.ClubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    public ResponseEntity<ClubView> create(@Valid @RequestBody ClubInput input) {
        var result = service.create(input);
        return HttpPages.created("/api/v1/clubs/{id}", result.id(), result);
    }
    @GetMapping("/{id}") @Operation(summary = "Получить club")
    public ResponseEntity<ClubView> get(@PathVariable Long id) { return ResponseEntity.ok(service.get(id)); }
    @GetMapping @Operation(summary = "Список с общим количеством в X-Total-Count")
    public ResponseEntity<List<ClubView>> list(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "20") int size) {
        return HttpPages.withTotal(service.list(HttpPages.page(page, size)));
    }
    @PutMapping("/{id}") @Operation(summary = "Обновить club")
    public ResponseEntity<ClubView> update(@PathVariable Long id, @Valid @RequestBody ClubInput input) {
        return ResponseEntity.ok(service.update(id, input));
    }
    @DeleteMapping("/{id}") @Operation(summary = "Удалить club")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
