package com.nnm.bot.admin.repository.bot.command;

import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandRobotRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Collection;

public interface BotCommandRobotRoleRepository extends JpaRepository<BotCommandRobotRole, Long> {

    List<BotCommandRobotRole> findAllByCommandId(Long commandId);

    List<BotCommandRobotRole> findAllByRobotRoleId(Long robotRoleId);

    List<BotCommandRobotRole> findAllByRobotRoleIdIn(Collection<Long> robotRoleIds);

    void deleteAllByCommandId(Long commandId);

    void deleteAllByRobotRoleId(Long robotRoleId);
}
