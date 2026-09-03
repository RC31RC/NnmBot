package com.nnm.bot.admin.service;

import com.nnm.bot.admin.dto.bot.BotRobotRoleItem;
import com.nnm.bot.admin.dto.bot.BotRobotRoleSaveRequest;

import java.util.List;

public interface BotRobotRoleManagementService {

    List<BotRobotRoleItem> list();

    long create(BotRobotRoleSaveRequest request);

    void update(long id, BotRobotRoleSaveRequest request);

    void assignInstanceRoles(long instanceId, List<Long> roleIds);

    void delete(long id);
}
