package com.example.fantasy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "managers")
@Getter @Setter
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
}
