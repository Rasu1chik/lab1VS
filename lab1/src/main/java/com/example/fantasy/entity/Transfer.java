package com.example.fantasy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "transfers")
public class Transfer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fantasy_team_id", nullable = false)
    private FantasyTeam team;
    @NotNull @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gameweek_id", nullable = false)
    private Gameweek gameweek;
    @NotNull @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_in_id", nullable = false)
    private Player playerIn;
    @NotNull @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_out_id", nullable = false)
    private Player playerOut;
    @NotNull @Column(nullable = false, precision = 4, scale = 1)
    private BigDecimal priceIn;
    @NotNull @Column(nullable = false, precision = 4, scale = 1)
    private BigDecimal priceOut;
    @Column(nullable = false)
    private Instant createdAt;

    public Long getId() { return id; }
    public FantasyTeam getTeam() { return team; }
    public void setTeam(FantasyTeam team) { this.team = team; }
    public Gameweek getGameweek() { return gameweek; }
    public void setGameweek(Gameweek gameweek) { this.gameweek = gameweek; }
    public Player getPlayerIn() { return playerIn; }
    public void setPlayerIn(Player playerIn) { this.playerIn = playerIn; }
    public Player getPlayerOut() { return playerOut; }
    public void setPlayerOut(Player playerOut) { this.playerOut = playerOut; }
    public BigDecimal getPriceIn() { return priceIn; }
    public void setPriceIn(BigDecimal priceIn) { this.priceIn = priceIn; }
    public BigDecimal getPriceOut() { return priceOut; }
    public void setPriceOut(BigDecimal priceOut) { this.priceOut = priceOut; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
