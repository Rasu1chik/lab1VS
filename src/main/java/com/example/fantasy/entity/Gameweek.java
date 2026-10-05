package com.example.fantasy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "gameweeks")
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

    public Long getId() { return id; }
    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }
    public Instant getDeadline() { return deadline; }
    public void setDeadline(Instant deadline) { this.deadline = deadline; }
    public GameweekStatus getStatus() { return status; }
    public void setStatus(GameweekStatus status) { this.status = status; }
}
