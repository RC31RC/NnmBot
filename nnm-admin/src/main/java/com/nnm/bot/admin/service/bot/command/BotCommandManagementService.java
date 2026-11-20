package com.nnm.bot.admin.service.bot.command;

import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandCostDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandCostSaveDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandSaveDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandRoleDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandRoleSaveDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandSystemRoleSaveDTO;

import java.util.List;

/** 管理命令启用状态、角色授权和执行成本配置。 */
public interface BotCommandManagementService {

    /**
     * 查询全部命令管理信息。
     *
     * @return 命令配置列表
     */
    List<BotCommandDTO> commands();

    /**
     * 更新命令基础配置。
     *
     * @param code 命令稳定编码
     * @param request 更新内容
     */
    void update(String code, BotCommandSaveDTO request);

    /**
     * 查询命令的机器人角色授权。
     *
     * @param code 命令稳定编码
     * @return 已授权角色列表
     */
    List<BotCommandRoleDTO> roles(String code);

    /**
     * 替换命令的机器人角色授权。
     *
     * @param code 命令稳定编码
     * @param request 目标角色集合
     */
    void updateRoles(String code, BotCommandRoleSaveDTO request);

    /**
     * 查询命令的系统角色授权主键。
     *
     * @param code 命令稳定编码
     * @return 系统角色主键列表
     */
    List<Long> systemRoleIds(String code);

    /**
     * 替换命令的系统角色授权。
     *
     * @param code 命令稳定编码
     * @param request 目标系统角色集合
     */
    void updateSystemRoles(String code, BotCommandSystemRoleSaveDTO request);

    /**
     * 查询命令执行成本配置。
     *
     * @param code 命令稳定编码
     * @return 命令成本配置
     */
    BotCommandCostDTO cost(String code);

    /**
     * 更新命令执行成本配置。
     *
     * @param code 命令稳定编码
     * @param request 成本参数
     */
    void updateCost(String code, BotCommandCostSaveDTO request);
}
