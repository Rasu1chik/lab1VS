package com.example.fantasy.service;

import com.example.fantasy.dto.ApiDto.*;
import com.example.fantasy.entity.*;
import com.example.fantasy.exception.ApiException;
import com.example.fantasy.repository.*;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class GameweekService {
    private final GameweekRepository gameweeks;
    private final PlayerRepository players;
    private final PlayerPerformanceRepository performances;
    private final FantasyTeamRepository teams;
    private final PointsCalculator calculator;

    private Gameweek week(Long id) {
        return gameweeks.findById(id).orElseThrow(() -> ApiException.missing("Тур"));
    }

    public GameweekView create(GameweekInput input) {
        Gameweek week = new Gameweek();
        week.setNumber(input.number());
        week.setDeadline(input.deadline());
        week.setStatus(GameweekStatus.UPCOMING);
        return Views.gameweek(gameweeks.save(week));
    }

    public GameweekView update(Long id, GameweekInput input) {
        Gameweek week = week(id);
        if (week.getStatus() != GameweekStatus.UPCOMING) throw ApiException.conflict("Начавшийся тур менять нельзя");
        week.setNumber(input.number());
        week.setDeadline(input.deadline());
        return Views.gameweek(week);
    }

    @Transactional(readOnly = true)
    public GameweekView get(Long id) { return Views.gameweek(week(id)); }
    @Transactional(readOnly = true)
    public Page<GameweekView> list(Pageable pageable) { return gameweeks.findAll(pageable).map(Views::gameweek); }

    public void delete(Long id) {
        Gameweek week = week(id);
        if (week.getStatus() != GameweekStatus.UPCOMING) throw ApiException.conflict("Начавшийся тур удалить нельзя");
        gameweeks.delete(week);
        gameweeks.flush();
    }

    public GameweekView start(Long id) {
        Gameweek week = gameweeks.findWithLockById(id).orElseThrow(() -> ApiException.missing("Тур"));
        if (week.getStatus() != GameweekStatus.UPCOMING) throw ApiException.conflict("Тур уже начат");
        week.setStatus(GameweekStatus.ACTIVE);
        return Views.gameweek(week);
    }

    public PerformanceView record(Long id, PerformanceInput input) {
        Gameweek week = week(id);
        if (week.getStatus() != GameweekStatus.ACTIVE) throw ApiException.conflict("Статистику можно внести только в активный тур");
        Player player = players.findById(input.playerId()).orElseThrow(() -> ApiException.missing("Игрок"));
        PlayerPerformance value = performances.findByGameweekIdAndPlayerId(id, input.playerId())
                .orElseGet(PlayerPerformance::new);
        value.setGameweek(week);
        value.setPlayer(player);
        value.setMinutesPlayed(input.minutesPlayed());
        value.setGoals(input.goals());
        value.setAssists(input.assists());
        value.setCleanSheet(input.cleanSheet());
        value.setYellowCards(input.yellowCards());
        value.setRedCards(input.redCards());
        return Views.performance(performances.save(value));
    }

    @Transactional(readOnly = true)
    public Page<PerformanceView> performances(Long id, Pageable pageable) {
        week(id);
        return performances.findByGameweekId(id, pageable).map(Views::performance);
    }

    public GameweekView finish(Long id) {
        Gameweek week = gameweeks.findWithLockById(id).orElseThrow(() -> ApiException.missing("Тур"));
        if (week.getStatus() != GameweekStatus.ACTIVE) throw ApiException.conflict("Закрыть можно только активный тур");
        var results = performances.findByGameweekId(id);
        for (PlayerPerformance result : results) {
            int points = calculator.calculate(result.getPlayer().getPosition(), result);
            result.setPoints(points);
            result.getPlayer().setTotalPoints(result.getPlayer().getTotalPoints() + points);
        }
        Map<Long, Integer> pointsByPlayer = results.stream().collect(Collectors.toMap(
                result -> result.getPlayer().getId(), PlayerPerformance::getPoints));
        int pageNumber = 0;
        Page<FantasyTeam> batch;
        do {
            batch = teams.findAll(PageRequest.of(pageNumber++, 50, Sort.by("id")));
            for (FantasyTeam team : batch) {
                int earned = team.getSquad().stream().filter(SquadPlayer::isStarter)
                        .mapToInt(item -> pointsByPlayer.getOrDefault(item.getPlayer().getId(), 0)
                                * (item.isCaptain() ? 2 : 1)).sum();
                team.setTotalPoints(team.getTotalPoints() + earned);
            }
        } while (batch.hasNext());
        week.setStatus(GameweekStatus.FINISHED);
        return Views.gameweek(week);
    }
}
