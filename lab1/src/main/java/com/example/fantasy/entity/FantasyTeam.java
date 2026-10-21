package com.example.fantasy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "fantasy_teams")
public class FantasyTeam {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private Manager manager;
    @NotBlank @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String name;
    @NotNull @DecimalMin("0.0")
    @Column(nullable = false, precision = 5, scale = 1)
    private BigDecimal budget;
    @Column(nullable = false)
    private int totalPoints;
    @Version @Column(nullable = false)
    private long version;
    @Column(nullable = false)
    private Instant createdAt;
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SquadPlayer> squad = new ArrayList<>();

    public Long getId() { return id; }
    public Manager getManager() { return manager; }
    public void setManager(Manager manager) { this.manager = manager; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }
    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }
    public long getVersion() { return version; }
    public void setVersion(long version) { this.version = version; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public List<SquadPlayer> getSquad() { return squad; }
}
