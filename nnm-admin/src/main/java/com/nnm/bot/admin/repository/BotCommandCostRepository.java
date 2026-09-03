package com.nnm.bot.admin.repository;

import com.nnm.bot.admin.domain.BotCommandCost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BotCommandCostRepository extends JpaRepository<BotCommandCost, Long> {

    Optional<BotCommandCost> findByCommandId(Long commandId);
}
