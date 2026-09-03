package com.nnm.bot.admin.repository;

import com.nnm.bot.admin.domain.BotCommand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BotCommandRepository extends JpaRepository<BotCommand, Long> {

    Optional<BotCommand> findByCodeIgnoreCase(String code);

    List<BotCommand> findAllByOrderByGameCodeAscOutputModeAscCodeAsc();

}
