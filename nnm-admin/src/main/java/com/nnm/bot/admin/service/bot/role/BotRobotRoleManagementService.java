package com.nnm.bot.admin.service.bot.role;

import com.nnm.bot.admin.pojo.dto.bot.role.BotRobotRoleDTO;
import com.nnm.bot.admin.pojo.dto.bot.role.BotRobotRoleSaveDTO;

import java.util.List;

public interface BotRobotRoleManagementService {

    List<BotRobotRoleDTO> list();

    long create(BotRobotRoleSaveDTO request);

    void update(long id, BotRobotRoleSaveDTO request);

    void assignInstanceRoles(long instanceId, List<Long> roleIds);

    void delete(long id);
}
