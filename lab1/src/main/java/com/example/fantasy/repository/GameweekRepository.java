package com.example.fantasy.repository;

import com.example.fantasy.entity.*;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;

public interface GameweekRepository extends JpaRepository<Gameweek, Long> {
    Optional<Gameweek> findFirstByStatusOrderByNumberAsc(GameweekStatus status);
    Optional<Gameweek> findFirstByStatusNotOrderByNumberAsc(GameweekStatus status);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select g from Gameweek g where g.id = :id")
    Optional<Gameweek> findLockedById(Long id);
}
