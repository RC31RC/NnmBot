package com.nnm.bot.admin.service.bot.command;

import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandCostDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandCostSaveDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandSaveDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandRoleDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandRoleSaveDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandSystemRoleSaveDTO;

import java.util.List;

public interface BotCommandManagementService {

    List<BotCommandDTO> commands();

    void update(String code, BotCommandSaveDTO request);

    List<BotCommandRoleDTO> roles(String code);

    void updateRoles(String code, BotCommandRoleSaveDTO request);

    List<Long> systemRoleIds(String code);

    void updateSystemRoles(String code, BotCommandSystemRoleSaveDTO request);

    BotCommandCostDTO cost(String code);

    void updateCost(String code, BotCommandCostSaveDTO request);
}
