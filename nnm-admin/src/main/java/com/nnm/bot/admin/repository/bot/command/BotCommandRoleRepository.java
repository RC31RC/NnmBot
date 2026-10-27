package com.nnm.bot.admin.repository.bot.command;

import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BotCommandRoleRepository extends JpaRepository<BotCommandRole, Long> {

    List<BotCommandRole> findAllByCommandId(Long commandId);

    void deleteAllByCommandId(Long commandId);
}
