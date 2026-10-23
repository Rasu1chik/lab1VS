package com.example.fantasy;

import com.example.fantasy.entity.*;
import com.example.fantasy.exception.ApiException;
import com.example.fantasy.service.SquadValidator;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SquadValidatorTest {
    private final SquadValidator validator = new SquadValidator(new BigDecimal("100.0"), 3);

    @Test
    void acceptsValidSquadAndReturnsBudget() {
        var players = squad();
        assertThat(validator.validate(players, starters(), 1L)).isEqualByComparingTo("25.0");
    }

    @Test
    void rejectsWrongSizeAndCaptain() {
        var players = squad();
        assertThatThrownBy(() -> validator.validate(players.subList(0, 14), starters(), 1L))
                .isInstanceOf(ApiException.class);
        assertThatThrownBy(() -> validator.validateLineup(players, starters(), 15L))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void rejectsClubLimitAndBudget() {
        var players = squad();
        players.get(3).setClub(players.get(0).getClub());
        assertThatThrownBy(() -> validator.validateClubLimit(players)).isInstanceOf(ApiException.class);
        var expensive = squad();
        for (Player player : expensive) player.setPrice(new BigDecimal("15.0"));
        assertThatThrownBy(() -> validator.validate(expensive, starters(), 1L))
                .isInstanceOf(ApiException.class);
    }

    private Set<Long> starters() { return Set.of(1L,2L,3L,4L,5L,6L,7L,8L,9L,10L,11L); }

    private List<Player> squad() {
        List<Player> players = new ArrayList<>();
        Position[] positions = {Position.GK, Position.GK, Position.DEF, Position.DEF, Position.DEF,
                Position.DEF, Position.DEF, Position.MID, Position.MID, Position.MID, Position.MID,
                Position.MID, Position.FWD, Position.FWD, Position.FWD};
        for (int i = 0; i < 15; i++) {
            var club = new RealClub();
            setId(club, (long) (i / 3 + 1));
            var player = new Player();
            setId(player, (long) i + 1);
            player.setClub(club);
            player.setPosition(positions[i]);
            player.setStatus(PlayerStatus.AVAILABLE);
            player.setPrice(new BigDecimal("5.0"));
            players.add(player);
        }
        return players;
    }

    private void setId(Object value, Long id) {
        try {
            var field = value.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(value, id);
        } catch (ReflectiveOperationException exception) { throw new AssertionError(exception); }
    }
}
