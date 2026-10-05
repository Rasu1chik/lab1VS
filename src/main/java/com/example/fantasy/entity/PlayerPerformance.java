package com.example.fantasy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "player_performances")
public class PlayerPerformance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;
    @NotNull @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gameweek_id", nullable = false)
    private Gameweek gameweek;
    @Min(0) @Max(120) @Column(nullable = false)
    private int minutesPlayed;
    @Min(0) @Column(nullable = false)
    private int goals;
    @Min(0) @Column(nullable = false)
    private int assists;
    @Column(nullable = false)
    private boolean cleanSheet;
    @Min(0) @Column(nullable = false)
    private int yellowCards;
    @Min(0) @Column(nullable = false)
    private int redCards;
    @Column(nullable = false)
    private int points;

    public Long getId() { return id; }
    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }
    public Gameweek getGameweek() { return gameweek; }
    public void setGameweek(Gameweek gameweek) { this.gameweek = gameweek; }
    public int getMinutesPlayed() { return minutesPlayed; }
    public void setMinutesPlayed(int minutesPlayed) { this.minutesPlayed = minutesPlayed; }
    public int getGoals() { return goals; }
    public void setGoals(int goals) { this.goals = goals; }
    public int getAssists() { return assists; }
    public void setAssists(int assists) { this.assists = assists; }
    public boolean isCleanSheet() { return cleanSheet; }
    public void setCleanSheet(boolean cleanSheet) { this.cleanSheet = cleanSheet; }
    public int getYellowCards() { return yellowCards; }
    public void setYellowCards(int yellowCards) { this.yellowCards = yellowCards; }
    public int getRedCards() { return redCards; }
    public void setRedCards(int redCards) { this.redCards = redCards; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
}
