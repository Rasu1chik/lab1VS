package com.example.fantasy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "players")
@Getter @Setter
public class Player {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private RealClub club;
    @NotBlank @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String firstName;
    @NotBlank @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String lastName;
    @NotNull @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private Position position;
    @NotNull @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PlayerStatus status;
    @NotNull @DecimalMin("3.0") @DecimalMax("25.0")
    @Column(nullable = false, precision = 4, scale = 1)
    private BigDecimal price;
    @Column(nullable = false)
    private int totalPoints;
}
