package com.example.fantasy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "real_clubs")
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

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getShortName() { return shortName; }
    public void setShortName(String shortName) { this.shortName = shortName; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
