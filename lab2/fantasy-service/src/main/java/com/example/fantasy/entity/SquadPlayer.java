package com.example.fantasy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "squad_players")
@Getter @Setter
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
}
