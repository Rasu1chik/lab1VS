package com.example.fantasy.service;

import com.example.fantasy.dto.ApiDto.*;
import com.example.fantasy.entity.*;
import com.example.fantasy.exception.ApiException;
import com.example.fantasy.repository.*;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LeagueService {
    private final LeagueRepository leagues;
    private final ManagerRepository managers;
    private final FantasyTeamRepository teams;

    private League league(Long id) {
        return leagues.findById(id).orElseThrow(() -> ApiException.missing("Лига"));
    }

    public LeagueView create(LeagueInput input) {
        League league = new League();
        apply(league, input);
        return Views.league(leagues.save(league));
    }

    public LeagueView update(Long id, LeagueInput input) {
        League league = league(id);
        apply(league, input);
        return Views.league(league);
    }

    private void apply(League league, LeagueInput input) {
        league.setOwner(managers.findById(input.ownerId()).orElseThrow(() -> ApiException.missing("Менеджер")));
        league.setName(input.name());
        league.setType(input.type());
        if (input.type() == LeagueType.PRIVATE && (input.joinCode() == null || input.joinCode().isBlank())) {
            throw ApiException.rule("Для приватной лиги нужен код приглашения");
        }
        league.setJoinCode(input.type() == LeagueType.PRIVATE ? input.joinCode() : null);
    }

    @Transactional(readOnly = true)
    public LeagueView get(Long id) { return Views.league(league(id)); }
    @Transactional(readOnly = true)
    public Page<LeagueView> list(Pageable pageable) { return leagues.findAll(pageable).map(Views::league); }
    public void delete(Long id) { leagues.delete(league(id)); leagues.flush(); }

    public void join(Long id, JoinLeagueInput input) {
        League league = league(id);
        FantasyTeam team = teams.findById(input.teamId()).orElseThrow(() -> ApiException.missing("Команда"));
        if (league.getType() == LeagueType.PRIVATE && !league.getJoinCode().equals(input.joinCode())) {
            throw ApiException.rule("Неверный код приглашения");
        }
        if (!league.getTeams().add(team)) throw ApiException.conflict("Команда уже состоит в лиге");
    }

    public void leave(Long id, Long teamId) {
        League league = league(id);
        FantasyTeam team = teams.findById(teamId).orElseThrow(() -> ApiException.missing("Команда"));
        if (!league.getTeams().remove(team)) throw ApiException.missing("Участие команды в лиге");
    }

    @Transactional(readOnly = true)
    public Page<TeamView> standings(Long id, Pageable pageable) {
        var sorted = league(id).getTeams().stream()
                .sorted(Comparator.comparingInt(FantasyTeam::getTotalPoints).reversed()
                        .thenComparing(FantasyTeam::getId)).toList();
        int start = Math.min((int) pageable.getOffset(), sorted.size());
        int end = Math.min(start + pageable.getPageSize(), sorted.size());
        return new PageImpl<>(sorted.subList(start, end).stream().map(Views::team).toList(), pageable, sorted.size());
    }
}
