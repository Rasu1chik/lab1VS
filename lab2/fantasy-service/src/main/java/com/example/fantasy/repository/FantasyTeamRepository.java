package com.example.fantasy.repository;

import com.example.fantasy.entity.*;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;

public interface FantasyTeamRepository extends JpaRepository<FantasyTeam, Long> {
    boolean existsByManagerId(Long managerId);
    Page<FantasyTeam> findByManagerId(Long managerId, Pageable pageable);
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Optional<FantasyTeam> findWithLockById(Long id);
}
