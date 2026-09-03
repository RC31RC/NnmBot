package com.nnm.bot.admin.repository;

import com.nnm.bot.admin.domain.BotCommandRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BotCommandRoleRepository extends JpaRepository<BotCommandRole, Long> {

    List<BotCommandRole> findAllByCommandId(Long commandId);

    void deleteAllByCommandId(Long commandId);
}
