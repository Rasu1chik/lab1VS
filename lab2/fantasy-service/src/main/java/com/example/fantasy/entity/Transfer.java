package com.example.fantasy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "transfers")
@Getter @Setter
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
}
