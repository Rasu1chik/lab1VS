package com.example.fantasy.controller;

import com.example.fantasy.dto.ApiDto.*;
import com.example.fantasy.service.ManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base-path}/managers")
@Tag(name = "Manager")
@RequiredArgsConstructor
public class ManagerController {
    private final ManagerService service;

    @PostMapping @Operation(summary = "Создать manager")
    public ResponseEntity<ManagerView> create(@Valid @RequestBody ManagerInput input) {
        var result = service.create(input);
        return HttpPages.created("/api/v1/managers/{id}", result.id(), result);
    }
    @GetMapping("/{id}") @Operation(summary = "Получить manager")
    public ResponseEntity<ManagerView> get(@PathVariable Long id) { return ResponseEntity.ok(service.get(id)); }
    @GetMapping @Operation(summary = "Список с общим количеством в X-Total-Count")
    public ResponseEntity<List<ManagerView>> list(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "20") int size) {
        return HttpPages.withTotal(service.list(HttpPages.page(page, size)));
    }
    @PutMapping("/{id}") @Operation(summary = "Обновить manager")
    public ResponseEntity<ManagerView> update(@PathVariable Long id, @Valid @RequestBody ManagerInput input) {
        return ResponseEntity.ok(service.update(id, input));
    }
    @DeleteMapping("/{id}") @Operation(summary = "Удалить manager")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
