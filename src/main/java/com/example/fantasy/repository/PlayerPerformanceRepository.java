package com.example.fantasy.repository;

import com.example.fantasy.entity.*;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;

public interface PlayerPerformanceRepository extends JpaRepository<PlayerPerformance, Long> {
    Page<PlayerPerformance> findByGameweekId(Long gameweekId, Pageable pageable);
    List<PlayerPerformance> findByGameweekId(Long gameweekId);
    Optional<PlayerPerformance> findByGameweekIdAndPlayerId(Long gameweekId, Long playerId);
}
