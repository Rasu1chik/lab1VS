package com.example.fantasy.controller;

import com.example.fantasy.dto.ApiDto.*;
import com.example.fantasy.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fantasy-teams")
@Tag(name = "Fantasy teams")
public class TeamController {
    private final TeamService service;
    public TeamController(TeamService service) { this.service = service; }

    @PostMapping @Operation(summary = "Создать команду сразу с составом")
    public ResponseEntity<TeamView> create(@Valid @RequestBody TeamInput input) {
        var result = service.create(input);
        return HttpPages.created("/api/v1/fantasy-teams/{id}", result.id(), result);
    }
    @GetMapping("/{id}") @Operation(summary = "Получить команду и состав")
    public TeamView get(@PathVariable Long id) { return service.get(id); }
    @GetMapping @Operation(summary = "Список команд с X-Total-Count")
    public ResponseEntity<List<TeamView>> list(@RequestParam(required = false) Long managerId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "20") int size) {
        return HttpPages.withTotal(service.list(managerId, HttpPages.page(page, size)));
    }
    @PatchMapping("/{id}") @Operation(summary = "Переименовать команду")
    public TeamView rename(@PathVariable Long id, @Valid @RequestBody TeamNameInput input) {
        return service.rename(id, input);
    }
    @PutMapping("/{id}/lineup") @Operation(summary = "Выбрать стартовый состав и капитана")
    public TeamView lineup(@PathVariable Long id, @Valid @RequestBody LineupInput input) {
        return service.lineup(id, input);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @Operation(summary = "Удалить команду")
    public void delete(@PathVariable Long id) { service.delete(id); }
    @PostMapping("/{id}/transfers") @Operation(summary = "Совершить трансфер")
    public ResponseEntity<TransferView> transfer(@PathVariable Long id, @Valid @RequestBody TransferInput input) {
        var result = service.transfer(id, input);
        return HttpPages.created("/api/v1/fantasy-teams/" + id + "/transfers/{id}", result.id(), result);
    }
    @GetMapping("/{id}/transfers") @Operation(summary = "История трансферов без общего количества")
    public ScrollView<TransferView> transfers(@PathVariable Long id, @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "20") int size) {
        return service.transfers(id, HttpPages.page(page, size));
    }
}
