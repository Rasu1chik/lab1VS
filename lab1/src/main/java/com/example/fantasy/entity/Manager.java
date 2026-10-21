package com.example.fantasy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "managers")
public class Manager {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    @NotBlank @Email
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private Instant createdAt;
    @OneToMany(mappedBy = "manager")
    private List<FantasyTeam> teams = new ArrayList<>();

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
