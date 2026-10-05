package com.example.fantasy.service;

import com.example.fantasy.dto.ApiDto.*;
import com.example.fantasy.entity.*;
import com.example.fantasy.exception.ApiException;
import com.example.fantasy.repository.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TeamService {
    private final FantasyTeamRepository teams;
    private final PlayerRepository players;
    private final ManagerRepository managers;
    private final GameweekRepository gameweeks;
    private final TransferRepository transfers;
    private final SquadValidator validator;

    public TeamService(FantasyTeamRepository teams, PlayerRepository players, ManagerRepository managers,
                       GameweekRepository gameweeks, TransferRepository transfers, SquadValidator validator) {
        this.teams = teams;
        this.players = players;
        this.managers = managers;
        this.gameweeks = gameweeks;
        this.transfers = transfers;
        this.validator = validator;
    }

    private FantasyTeam team(Long id) {
        return teams.findById(id).orElseThrow(() -> ApiException.missing("Команда"));
    }

    public TeamView create(TeamInput input) {
        Manager manager = managers.findById(input.managerId()).orElseThrow(() -> ApiException.missing("Менеджер"));
        List<Player> roster = players.findAllById(input.playerIds());
        if (roster.size() != input.playerIds().size()) throw ApiException.missing("Один из игроков");
        BigDecimal budget = validator.validate(roster, Set.copyOf(input.starterIds()), input.captainId());
        FantasyTeam team = new FantasyTeam();
        team.setManager(manager);
        team.setName(input.name());
        team.setBudget(budget);
        team.setCreatedAt(Instant.now());
        for (Player player : roster) {
            SquadPlayer item = new SquadPlayer();
            item.setTeam(team);
            item.setPlayer(player);
            item.setPurchasePrice(player.getPrice());
            item.setStarter(input.starterIds().contains(player.getId()));
            item.setCaptain(input.captainId().equals(player.getId()));
            item.setAddedAt(Instant.now());
            team.getSquad().add(item);
        }
        return Views.team(teams.save(team));
    }

    @Transactional(readOnly = true)
    public TeamView get(Long id) { return Views.team(team(id)); }

    @Transactional(readOnly = true)
    public Page<TeamView> list(Long managerId, Pageable pageable) {
        Page<FantasyTeam> result = managerId == null ? teams.findAll(pageable) : teams.findByManagerId(managerId, pageable);
        return result.map(Views::team);
    }

    public TeamView rename(Long id, TeamNameInput input) {
        FantasyTeam team = team(id);
        team.setName(input.name());
        return Views.team(team);
    }

    public TeamView lineup(Long id, LineupInput input) {
        FantasyTeam team = team(id);
        List<Player> roster = team.getSquad().stream().map(SquadPlayer::getPlayer).toList();
        Set<Long> starters = Set.copyOf(input.starterIds());
        validator.validateLineup(roster, starters, input.captainId());
        for (SquadPlayer item : team.getSquad()) {
            item.setStarter(starters.contains(item.getPlayer().getId()));
            item.setCaptain(input.captainId().equals(item.getPlayer().getId()));
        }
        return Views.team(team);
    }

    public void delete(Long id) { teams.delete(team(id)); teams.flush(); }

    public TransferView transfer(Long id, TransferInput input) {
        FantasyTeam team = teams.findForTransfer(id).orElseThrow(() -> ApiException.missing("Команда"));
        Gameweek week = gameweeks.findFirstByStatusNotOrderByNumberAsc(GameweekStatus.FINISHED)
                .orElseThrow(() -> ApiException.rule("Нет предстоящего тура для трансфера"));
        if (week.getStatus() != GameweekStatus.UPCOMING || !Instant.now().isBefore(week.getDeadline())) {
            throw ApiException.rule("Трансферы доступны только до начала ближайшего тура");
        }
        SquadPlayer outgoing = team.getSquad().stream()
                .filter(item -> item.getPlayer().getId().equals(input.playerOutId())).findFirst()
                .orElseThrow(() -> ApiException.rule("Продаваемого игрока нет в составе"));
        Player incoming = players.findById(input.playerInId()).orElseThrow(() -> ApiException.missing("Покупаемый игрок"));
        if (incoming.getStatus() != PlayerStatus.AVAILABLE ||
                incoming.getPosition() != outgoing.getPlayer().getPosition()) {
            throw ApiException.rule("Новый игрок должен быть доступен и играть на той же позиции");
        }
        if (team.getSquad().stream().anyMatch(item -> item.getPlayer().getId().equals(incoming.getId()))) {
            throw ApiException.rule("Игрок уже находится в составе");
        }
        BigDecimal newBudget = team.getBudget().add(outgoing.getPurchasePrice()).subtract(incoming.getPrice());
        if (newBudget.signum() < 0) throw ApiException.rule("Недостаточно бюджета для трансфера");
        List<Player> newRoster = team.getSquad().stream().filter(item -> item != outgoing)
                .map(SquadPlayer::getPlayer).collect(Collectors.toCollection(ArrayList::new));
        newRoster.add(incoming);
        validator.validateClubLimit(newRoster);
        boolean starter = outgoing.isStarter();
        boolean captain = outgoing.isCaptain();
        team.getSquad().remove(outgoing);
        SquadPlayer replacement = new SquadPlayer();
        replacement.setTeam(team);
        replacement.setPlayer(incoming);
        replacement.setPurchasePrice(incoming.getPrice());
        replacement.setStarter(starter);
        replacement.setCaptain(captain);
        replacement.setAddedAt(Instant.now());
        team.getSquad().add(replacement);
        team.setBudget(newBudget);
        Transfer transfer = new Transfer();
        transfer.setTeam(team);
        transfer.setGameweek(week);
        transfer.setPlayerOut(outgoing.getPlayer());
        transfer.setPlayerIn(incoming);
        transfer.setPriceOut(outgoing.getPurchasePrice());
        transfer.setPriceIn(incoming.getPrice());
        transfer.setCreatedAt(Instant.now());
        teams.flush();
        return Views.transfer(transfers.save(transfer));
    }

    @Transactional(readOnly = true)
    public ScrollView<TransferView> transfers(Long teamId, Pageable pageable) {
        team(teamId);
        Slice<TransferView> slice = transfers.findByTeamId(teamId, pageable).map(Views::transfer);
        return new ScrollView<>(slice.getContent(), slice.getNumber(), slice.getSize(), slice.hasNext());
    }
}
