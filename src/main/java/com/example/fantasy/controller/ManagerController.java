package com.example.fantasy.controller;

import com.example.fantasy.dto.ApiDto.*;
import com.example.fantasy.service.CatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/managers")
@Tag(name = "Manager")
public class ManagerController {
    private final CatalogService service;
    public ManagerController(CatalogService service) { this.service = service; }

    @PostMapping @Operation(summary = "Создать manager")
    public ResponseEntity<ManagerView> create(@Valid @RequestBody ManagerInput input) {
        var result = service.createManager(input);
        return HttpPages.created("/api/v1/managers/{id}", result.id(), result);
    }
    @GetMapping("/{id}") @Operation(summary = "Получить manager")
    public ManagerView get(@PathVariable Long id) { return service.getManager(id); }
    @GetMapping @Operation(summary = "Список с общим количеством в X-Total-Count")
    public ResponseEntity<List<ManagerView>> list(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "20") int size) {
        return HttpPages.withTotal(service.managers(HttpPages.page(page, size)));
    }
    @PutMapping("/{id}") @Operation(summary = "Обновить manager")
    public ManagerView update(@PathVariable Long id, @Valid @RequestBody ManagerInput input) {
        return service.updateManager(id, input);
    }
    @DeleteMapping("/{id}") @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить manager")
    public void delete(@PathVariable Long id) { service.deleteManager(id); }
}
