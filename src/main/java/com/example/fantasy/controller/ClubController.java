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
@RequestMapping("/api/v1/clubs")
@Tag(name = "Club")
public class ClubController {
    private final CatalogService service;
    public ClubController(CatalogService service) { this.service = service; }

    @PostMapping @Operation(summary = "Создать club")
    public ResponseEntity<ClubView> create(@Valid @RequestBody ClubInput input) {
        var result = service.createClub(input);
        return HttpPages.created("/api/v1/clubs/{id}", result.id(), result);
    }
    @GetMapping("/{id}") @Operation(summary = "Получить club")
    public ClubView get(@PathVariable Long id) { return service.getClub(id); }
    @GetMapping @Operation(summary = "Список с общим количеством в X-Total-Count")
    public ResponseEntity<List<ClubView>> list(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "20") int size) {
        return HttpPages.withTotal(service.clubs(HttpPages.page(page, size)));
    }
    @PutMapping("/{id}") @Operation(summary = "Обновить club")
    public ClubView update(@PathVariable Long id, @Valid @RequestBody ClubInput input) {
        return service.updateClub(id, input);
    }
    @DeleteMapping("/{id}") @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить club")
    public void delete(@PathVariable Long id) { service.deleteClub(id); }
}
