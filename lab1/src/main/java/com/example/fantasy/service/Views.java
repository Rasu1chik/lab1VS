package com.example.fantasy.service;

import com.example.fantasy.dto.ApiDto.*;
import com.example.fantasy.entity.*;

public final class Views {
    private Views() {}

    public static ManagerView manager(Manager value) {
        return new ManagerView(value.getId(), value.getUsername(), value.getEmail(), value.getCreatedAt());
    }

    public static ClubView club(RealClub value) {
        return new ClubView(value.getId(), value.getName(), value.getShortName(), value.getCountry());
    }

    public static PlayerView player(Player value) {
        return new PlayerView(value.getId(), value.getClub().getId(), value.getFirstName(), value.getLastName(),
                value.getPosition(), value.getStatus(), value.getPrice(), value.getTotalPoints());
    }

    public static TeamView team(FantasyTeam value) {
        var squad = value.getSquad().stream().map(item -> new SquadView(item.getPlayer().getId(),
                item.getPlayer().getFirstName() + " " + item.getPlayer().getLastName(),
                item.getPlayer().getPosition(), item.getPurchasePrice(), item.isStarter(), item.isCaptain())).toList();
        return new TeamView(value.getId(), value.getManager().getId(), value.getName(),
                value.getBudget(), value.getTotalPoints(), squad);
    }

    public static GameweekView gameweek(Gameweek value) {
        return new GameweekView(value.getId(), value.getNumber(), value.getDeadline(), value.getStatus());
    }

    public static PerformanceView performance(PlayerPerformance value) {
        return new PerformanceView(value.getId(), value.getPlayer().getId(), value.getGameweek().getId(),
                value.getMinutesPlayed(), value.getGoals(), value.getAssists(), value.isCleanSheet(),
                value.getYellowCards(), value.getRedCards(), value.getPoints());
    }

    public static TransferView transfer(Transfer value) {
        return new TransferView(value.getId(), value.getTeam().getId(), value.getPlayerOut().getId(),
                value.getPlayerIn().getId(), value.getGameweek().getId(),
                value.getPriceOut(), value.getPriceIn(), value.getCreatedAt());
    }

    public static LeagueView league(League value) {
        return new LeagueView(value.getId(), value.getOwner().getId(), value.getName(),
                value.getType(), value.getJoinCode());
    }
}
