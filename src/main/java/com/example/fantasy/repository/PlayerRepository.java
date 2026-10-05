package com.example.fantasy.repository;

import com.example.fantasy.entity.*;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    @Query("select p from Player p where (:position is null or p.position = :position) " +
            "and (:clubId is null or p.club.id = :clubId) and (:maxPrice is null or p.price <= :maxPrice)")
    Slice<Player> search(Position position, Long clubId, BigDecimal maxPrice, Pageable pageable);
}
