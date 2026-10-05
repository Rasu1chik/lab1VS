package com.example.fantasy.service;

import com.example.fantasy.dto.ApiDto.*;
import com.example.fantasy.entity.*;
import com.example.fantasy.exception.ApiException;
import com.example.fantasy.repository.*;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CatalogService {
    private final ManagerRepository managers;
    private final RealClubRepository clubs;
    private final PlayerRepository players;

    public CatalogService(ManagerRepository managers, RealClubRepository clubs, PlayerRepository players) {
        this.managers = managers;
        this.clubs = clubs;
        this.players = players;
    }

    public Manager manager(Long id) { return managers.findById(id).orElseThrow(() -> ApiException.missing("Менеджер")); }
    public RealClub club(Long id) { return clubs.findById(id).orElseThrow(() -> ApiException.missing("Клуб")); }
    public Player player(Long id) { return players.findById(id).orElseThrow(() -> ApiException.missing("Игрок")); }

    public ManagerView createManager(ManagerInput input) {
        Manager value = new Manager();
        value.setCreatedAt(Instant.now());
        apply(value, input);
        return Views.manager(managers.save(value));
    }

    public ManagerView updateManager(Long id, ManagerInput input) {
        Manager value = manager(id);
        apply(value, input);
        return Views.manager(value);
    }

    private void apply(Manager value, ManagerInput input) {
        value.setUsername(input.username());
        value.setEmail(input.email());
    }

    public void deleteManager(Long id) { managers.delete(manager(id)); managers.flush(); }
    @Transactional(readOnly = true)
    public ManagerView getManager(Long id) { return Views.manager(manager(id)); }
    @Transactional(readOnly = true)
    public Page<ManagerView> managers(Pageable pageable) { return managers.findAll(pageable).map(Views::manager); }

    public ClubView createClub(ClubInput input) {
        RealClub value = new RealClub();
        apply(value, input);
        return Views.club(clubs.save(value));
    }

    public ClubView updateClub(Long id, ClubInput input) {
        RealClub value = club(id);
        apply(value, input);
        return Views.club(value);
    }

    private void apply(RealClub value, ClubInput input) {
        value.setName(input.name());
        value.setShortName(input.shortName());
        value.setCountry(input.country());
    }

    public void deleteClub(Long id) { clubs.delete(club(id)); clubs.flush(); }
    @Transactional(readOnly = true)
    public ClubView getClub(Long id) { return Views.club(club(id)); }
    @Transactional(readOnly = true)
    public Page<ClubView> clubs(Pageable pageable) { return clubs.findAll(pageable).map(Views::club); }

    public PlayerView createPlayer(PlayerInput input) {
        Player value = new Player();
        apply(value, input);
        return Views.player(players.save(value));
    }

    public PlayerView updatePlayer(Long id, PlayerInput input) {
        Player value = player(id);
        apply(value, input);
        return Views.player(value);
    }

    private void apply(Player value, PlayerInput input) {
        value.setClub(club(input.clubId()));
        value.setFirstName(input.firstName());
        value.setLastName(input.lastName());
        value.setPosition(input.position());
        value.setStatus(input.status());
        value.setPrice(input.price());
    }

    public void deletePlayer(Long id) { players.delete(player(id)); players.flush(); }
    @Transactional(readOnly = true)
    public PlayerView getPlayer(Long id) { return Views.player(player(id)); }
    @Transactional(readOnly = true)
    public ScrollView<PlayerView> players(Position position, Long clubId, BigDecimal maxPrice, Pageable pageable) {
        Slice<PlayerView> slice = players.search(position, clubId, maxPrice, pageable).map(Views::player);
        return new ScrollView<>(slice.getContent(), slice.getNumber(), slice.getSize(), slice.hasNext());
    }
}
