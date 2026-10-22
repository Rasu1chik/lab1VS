package com.example.fantasy.service;

import com.example.fantasy.entity.Player;
import com.example.fantasy.entity.PlayerStatus;
import com.example.fantasy.entity.Position;
import com.example.fantasy.exception.ApiException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SquadValidator {
    private final BigDecimal initialBudget;
    private final int maxPlayersPerClub;

    public SquadValidator(@Value("${game.initial-budget}") BigDecimal initialBudget,
                          @Value("${game.max-players-per-club}") int maxPlayersPerClub) {
        this.initialBudget = initialBudget;
        this.maxPlayersPerClub = maxPlayersPerClub;
    }

    public BigDecimal validate(List<Player> players, Set<Long> starterIds, Long captainId) {
        if (players.size() != 15 || players.stream().map(Player::getId).distinct().count() != 15) {
            throw ApiException.rule("В составе должно быть 15 разных игроков");
        }
        Map<Position, Long> positions = players.stream().collect(Collectors.groupingBy(Player::getPosition, Collectors.counting()));
        if (positions.getOrDefault(Position.GK, 0L) != 2 || positions.getOrDefault(Position.DEF, 0L) != 5
                || positions.getOrDefault(Position.MID, 0L) != 5 || positions.getOrDefault(Position.FWD, 0L) != 3) {
            throw ApiException.rule("Состав должен содержать 2 GK, 5 DEF, 5 MID и 3 FWD");
        }
        if (players.stream().collect(Collectors.groupingBy(p -> p.getClub().getId(), Collectors.counting()))
                .values().stream().anyMatch(count -> count > maxPlayersPerClub)) {
            throw ApiException.rule("В составе слишком много игроков одного клуба");
        }
        if (players.stream().anyMatch(player -> player.getStatus() != PlayerStatus.AVAILABLE)) {
            throw ApiException.rule("В состав можно добавить только доступных игроков");
        }
        validateLineup(players, starterIds, captainId);
        BigDecimal cost = players.stream().map(Player::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (cost.compareTo(initialBudget) > 0) {
            throw ApiException.rule("Недостаточно бюджета для создания команды");
        }
        return initialBudget.subtract(cost);
    }

    public void validateLineup(List<Player> players, Set<Long> starterIds, Long captainId) {
        Set<Long> playerIds = players.stream().map(Player::getId).collect(Collectors.toSet());
        if (starterIds.size() != 11 || !playerIds.containsAll(starterIds) || !starterIds.contains(captainId)) {
            throw ApiException.rule("Нужно выбрать 11 игроков состава и капитана среди них");
        }
    }

    public void validateClubLimit(List<Player> players) {
        if (players.stream().collect(Collectors.groupingBy(p -> p.getClub().getId(), Collectors.counting()))
                .values().stream().anyMatch(count -> count > maxPlayersPerClub)) {
            throw ApiException.rule("После трансфера в составе будет больше " + maxPlayersPerClub + " игроков одного клуба");
        }
    }
}
