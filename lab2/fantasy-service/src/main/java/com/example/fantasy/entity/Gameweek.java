package com.example.fantasy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "gameweeks")
@Getter @Setter
public class Gameweek {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Min(1) @Column(nullable = false, unique = true)
    private int number;
    @NotNull @Column(nullable = false)
    private Instant deadline;
    @NotNull @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GameweekStatus status;
}
