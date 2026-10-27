package com.nnm.bot.admin.repository.bot.role;

import com.nnm.bot.admin.pojo.entity.bot.role.BotRobotRoleInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface BotRobotRoleInstanceRepository extends JpaRepository<BotRobotRoleInstance, Long> {

    List<BotRobotRoleInstance> findAllByRobotRoleId(Long robotRoleId);

    List<BotRobotRoleInstance> findAllByRobotRoleIdIn(Collection<Long> robotRoleIds);

    List<BotRobotRoleInstance> findAllByBotInstanceId(Long botInstanceId);

    boolean existsByRobotRoleId(Long robotRoleId);

    void deleteAllByRobotRoleId(Long robotRoleId);
}
