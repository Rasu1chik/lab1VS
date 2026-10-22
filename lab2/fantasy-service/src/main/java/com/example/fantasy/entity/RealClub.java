package com.example.fantasy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "real_clubs")
@Getter @Setter
public class RealClub {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Size(max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    @NotBlank @Size(min = 2, max = 3)
    @Column(nullable = false, unique = true, length = 3)
    private String shortName;
    @NotBlank @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String country;
    @OneToMany(mappedBy = "club")
    private List<Player> players = new ArrayList<>();
}
