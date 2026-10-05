package com.example.fantasy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "squad_players")
public class SquadPlayer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fantasy_team_id", nullable = false)
    private FantasyTeam team;
    @NotNull @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;
    @NotNull @Column(nullable = false, precision = 4, scale = 1)
    private BigDecimal purchasePrice;
    @Column(name = "is_captain", nullable = false)
    private boolean captain;
    @Column(name = "is_starter", nullable = false)
    private boolean starter;
    @Column(nullable = false)
    private Instant addedAt;

    public Long getId() { return id; }
    public FantasyTeam getTeam() { return team; }
    public void setTeam(FantasyTeam team) { this.team = team; }
    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }
    public boolean isCaptain() { return captain; }
    public void setCaptain(boolean captain) { this.captain = captain; }
    public boolean isStarter() { return starter; }
    public void setStarter(boolean starter) { this.starter = starter; }
    public Instant getAddedAt() { return addedAt; }
    public void setAddedAt(Instant addedAt) { this.addedAt = addedAt; }
}
