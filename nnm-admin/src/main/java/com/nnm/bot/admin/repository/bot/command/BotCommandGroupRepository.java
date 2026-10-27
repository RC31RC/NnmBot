package com.nnm.bot.admin.repository.bot.command;

import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandGroup;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BotCommandGroupRepository extends JpaRepository<BotCommandGroup, Long> {
    Optional<BotCommandGroup> findByCodeIgnoreCase(String code);
    List<BotCommandGroup> findAllByOrderByCodeAsc();
}
