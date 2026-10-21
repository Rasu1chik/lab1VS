package com.example.fantasy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "leagues")
@Getter @Setter
public class League {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Manager owner;
    @NotBlank @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;
    @NotNull @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private LeagueType type;
    @Size(max = 8) @Column(unique = true, length = 8)
    private String joinCode;
    @ManyToMany
    @JoinTable(name = "league_teams", joinColumns = @JoinColumn(name = "league_id"), inverseJoinColumns = @JoinColumn(name = "fantasy_team_id"))
    private Set<FantasyTeam> teams = new HashSet<>();
}
