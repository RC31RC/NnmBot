package com.nnm.bot.core.group.repository;

import com.nnm.bot.core.group.domain.BotGroup;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BotGroupRepository extends JpaRepository<BotGroup, Long> {

    // Serialize group topology writes through transaction commit, including changes in different groups.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select g from BotGroup g order by g.id")
    List<BotGroup> lockTopology();

    List<BotGroup> findAllByOrderByNameAscIdAsc();

    Optional<BotGroup> findByCodeIgnoreCase(String code);
}
