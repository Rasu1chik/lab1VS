package com.example.fantasy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "fantasy_teams")
@Getter @Setter
public class FantasyTeam {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull @Column(name = "manager_id", nullable = false)
    private Long managerId;
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
}
