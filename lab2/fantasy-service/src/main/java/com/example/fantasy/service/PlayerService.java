package com.example.fantasy.service;

import com.example.fantasy.dto.ApiDto.*;
import com.example.fantasy.entity.Player;
import com.example.fantasy.entity.Position;
import com.example.fantasy.exception.ApiException;
import com.example.fantasy.repository.PlayerRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository players;
    private final ClubService clubService;

    public Player player(Long id) { return players.findById(id).orElseThrow(() -> ApiException.missing("Игрок")); }

    public PlayerView create(PlayerInput input) {
        Player value = new Player();
        apply(value, input);
        return Views.player(players.save(value));
    }

    public PlayerView update(Long id, PlayerInput input) {
        Player value = player(id);
        apply(value, input);
        return Views.player(value);
    }

    private void apply(Player value, PlayerInput input) {
        value.setClub(clubService.club(input.clubId()));
        value.setFirstName(input.firstName());
        value.setLastName(input.lastName());
        value.setPosition(input.position());
        value.setStatus(input.status());
        value.setPrice(input.price());
    }

    public void delete(Long id) { players.delete(player(id)); players.flush(); }
    @Transactional(readOnly = true)
    public PlayerView get(Long id) { return Views.player(player(id)); }
    @Transactional(readOnly = true)
    public ScrollView<PlayerView> list(Position position, Long clubId, BigDecimal maxPrice, String name, Pageable pageable) {
        Slice<PlayerView> slice = players.search(position, clubId, maxPrice,
                name == null || name.isBlank() ? null : name.trim(), pageable).map(Views::player);
        return new ScrollView<>(slice.getContent(), slice.getNumber(), slice.getSize(), slice.hasNext());
    }
}
