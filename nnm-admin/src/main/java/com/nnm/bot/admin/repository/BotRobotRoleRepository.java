package com.nnm.bot.admin.repository;

import com.nnm.bot.admin.domain.BotRobotRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BotRobotRoleRepository extends JpaRepository<BotRobotRole, Long> {

    Optional<BotRobotRole> findByCodeIgnoreCase(String code);
}
