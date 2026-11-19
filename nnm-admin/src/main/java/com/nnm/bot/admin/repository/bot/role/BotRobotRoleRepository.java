package com.nnm.bot.admin.repository.bot.role;

import com.nnm.bot.admin.pojo.entity.bot.role.BotRobotRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** 持久化机器人实例使用的命令能力角色。 */
public interface BotRobotRoleRepository extends JpaRepository<BotRobotRole, Long> {

    Optional<BotRobotRole> findByCodeIgnoreCase(String code);
}
