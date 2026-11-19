package com.nnm.bot.admin.repository.bot.command;

import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandCost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** 持久化命令的群组积分消耗与奖励配置。 */
public interface BotCommandCostRepository extends JpaRepository<BotCommandCost, Long> {

    Optional<BotCommandCost> findByCommandId(Long commandId);
}
