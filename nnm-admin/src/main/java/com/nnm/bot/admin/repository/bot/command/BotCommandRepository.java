package com.nnm.bot.admin.repository.bot.command;

import com.nnm.bot.admin.pojo.entity.bot.command.BotCommand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** 查询管理端维护的机器人命令定义。 */
public interface BotCommandRepository extends JpaRepository<BotCommand, Long> {

    Optional<BotCommand> findByCodeIgnoreCase(String code);

}
