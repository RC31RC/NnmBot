package com.nnm.bot.admin.service;

import com.nnm.bot.admin.dto.bot.BotCommandItem;
import com.nnm.bot.admin.dto.bot.BotCommandChannelOutputItem;
import com.nnm.bot.admin.dto.bot.BotCommandChannelOutputSaveRequest;
import com.nnm.bot.admin.dto.bot.BotCommandCostItem;
import com.nnm.bot.admin.dto.bot.BotCommandCostSaveRequest;
import com.nnm.bot.admin.dto.bot.BotCommandSaveRequest;
import com.nnm.bot.admin.dto.bot.BotCommandRoleItem;
import com.nnm.bot.admin.dto.bot.BotCommandRoleSaveRequest;
import com.nnm.bot.admin.dto.bot.BotCommandSystemRoleSaveRequest;
import com.nnm.bot.core.constant.BotPlatform;

import java.util.List;

public interface BotCommandManagementService {

    List<BotCommandItem> commands();

    void update(String code, BotCommandSaveRequest request);

    List<BotCommandChannelOutputItem> channelOutputs(String code);

    void updateChannelOutput(String code, BotPlatform platform,
                             BotCommandChannelOutputSaveRequest request);

    List<BotCommandRoleItem> roles(String code);

    void updateRoles(String code, BotCommandRoleSaveRequest request);

    List<Long> systemRoleIds(String code);

    void updateSystemRoles(String code, BotCommandSystemRoleSaveRequest request);

    BotCommandCostItem cost(String code);

    void updateCost(String code, BotCommandCostSaveRequest request);
}
