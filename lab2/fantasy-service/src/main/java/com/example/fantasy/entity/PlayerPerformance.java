package com.example.fantasy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "player_performances")
@Getter @Setter
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
}
