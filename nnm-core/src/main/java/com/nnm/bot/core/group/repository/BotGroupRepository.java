package com.nnm.bot.core.group.repository;

import com.nnm.bot.core.group.domain.BotGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BotGroupRepository extends JpaRepository<BotGroup, Long> {

    List<BotGroup> findAllByOrderByNameAscIdAsc();

    Optional<BotGroup> findByCodeIgnoreCase(String code);
}
