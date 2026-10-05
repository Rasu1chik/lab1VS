package com.example.fantasy.dto;

import com.example.fantasy.entity.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class ApiDto {
    private ApiDto() {}

    public record ManagerInput(@NotBlank @Size(max = 50) String username, @NotBlank @Email String email) {}
    public record ManagerView(Long id, String username, String email, Instant createdAt) {}
    public record ClubInput(@NotBlank @Size(max = 100) String name,
                            @NotBlank @Size(min = 2, max = 3) String shortName,
                            @NotBlank @Size(max = 50) String country) {}
    public record ClubView(Long id, String name, String shortName, String country) {}
    public record PlayerInput(@NotNull Long clubId, @NotBlank @Size(max = 50) String firstName,
                              @NotBlank @Size(max = 50) String lastName,
                              @NotNull Position position, @NotNull PlayerStatus status,
                              @NotNull @DecimalMin("4.0") @DecimalMax("15.0") BigDecimal price) {}
    public record PlayerView(Long id, Long clubId, String firstName, String lastName,
                             Position position, PlayerStatus status, BigDecimal price, int totalPoints) {}
    public record TeamInput(@NotNull Long managerId, @NotBlank @Size(max = 50) String name,
                            @NotNull @Size(min = 15, max = 15) List<@NotNull Long> playerIds,
                            @NotNull @Size(min = 11, max = 11) List<@NotNull Long> starterIds,
                            @NotNull Long captainId) {}
    public record TeamNameInput(@NotBlank @Size(max = 50) String name) {}
    public record LineupInput(@NotNull @Size(min = 11, max = 11) List<@NotNull Long> starterIds,
                              @NotNull Long captainId) {}
    public record SquadView(Long playerId, String name, Position position, BigDecimal purchasePrice,
                            boolean starter, boolean captain) {}
    public record TeamView(Long id, Long managerId, String name, BigDecimal budget,
                           int totalPoints, List<SquadView> squad) {}
    public record TransferInput(@NotNull Long playerOutId, @NotNull Long playerInId) {}
    public record TransferView(Long id, Long teamId, Long playerOutId, Long playerInId,
                               Long gameweekId, BigDecimal priceOut, BigDecimal priceIn, Instant createdAt) {}
    public record GameweekInput(@Min(1) int number, @NotNull Instant deadline) {}
    public record GameweekView(Long id, int number, Instant deadline, GameweekStatus status) {}
    public record PerformanceInput(@NotNull Long playerId, @Min(0) @Max(120) int minutesPlayed,
                                   @Min(0) int goals, @Min(0) int assists, boolean cleanSheet,
                                   @Min(0) int yellowCards, @Min(0) int redCards) {}
    public record PerformanceView(Long id, Long playerId, Long gameweekId, int minutesPlayed,
                                  int goals, int assists, boolean cleanSheet,
                                  int yellowCards, int redCards, int points) {}
    public record LeagueInput(@NotNull Long ownerId, @NotBlank @Size(max = 100) String name,
                              @NotNull LeagueType type, @Size(max = 8) String joinCode) {}
    public record LeagueView(Long id, Long ownerId, String name, LeagueType type, String joinCode) {}
    public record JoinLeagueInput(@NotNull Long teamId, String joinCode) {}
    public record ScrollView<T>(List<T> content, int page, int size, boolean hasNext) {}
}
