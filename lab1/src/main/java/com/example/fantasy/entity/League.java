package com.example.fantasy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "leagues")
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

    public Long getId() { return id; }
    public Manager getOwner() { return owner; }
    public void setOwner(Manager owner) { this.owner = owner; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LeagueType getType() { return type; }
    public void setType(LeagueType type) { this.type = type; }
    public String getJoinCode() { return joinCode; }
    public void setJoinCode(String joinCode) { this.joinCode = joinCode; }
    public Set<FantasyTeam> getTeams() { return teams; }
}
