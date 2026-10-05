package com.example.fantasy.repository;

import com.example.fantasy.entity.*;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;

public interface SquadPlayerRepository extends JpaRepository<SquadPlayer, Long> {
    List<SquadPlayer> findByTeamId(Long teamId);
    Optional<SquadPlayer> findByTeamIdAndPlayerId(Long teamId, Long playerId);
}
