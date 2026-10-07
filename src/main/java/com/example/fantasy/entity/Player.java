package com.example.fantasy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "players")
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

    public Long getId() { return id; }
    public RealClub getClub() { return club; }
    public void setClub(RealClub club) { this.club = club; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }
    public PlayerStatus getStatus() { return status; }
    public void setStatus(PlayerStatus status) { this.status = status; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }
}
