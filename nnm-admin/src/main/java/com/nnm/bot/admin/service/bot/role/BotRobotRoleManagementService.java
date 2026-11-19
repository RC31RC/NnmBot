package com.nnm.bot.admin.service.bot.role;

import com.nnm.bot.admin.pojo.dto.bot.role.BotRobotRoleDTO;
import com.nnm.bot.admin.pojo.dto.bot.role.BotRobotRoleSaveDTO;

import java.util.List;

/** 管理机器人实例角色及其命令能力绑定。 */
public interface BotRobotRoleManagementService {

    /** 查询角色及关联的实例、命令。
     * @return 机器人角色列表
     */
    List<BotRobotRoleDTO> list();

    /** 创建机器人角色。
     * @param request 角色及关联配置
     * @return 新角色主键
     */
    long create(BotRobotRoleSaveDTO request);

    /** 更新机器人角色及关联配置。
     * @param id 角色主键
     * @param request 更新内容
     */
    void update(long id, BotRobotRoleSaveDTO request);

    /** 原子替换实例拥有的角色集合。
     * @param instanceId 机器人实例主键
     * @param roleIds 目标角色主键集合
     */
    void assignInstanceRoles(long instanceId, List<Long> roleIds);

    /** 删除未被实例使用的机器人角色。
     * @param id 角色主键
     */
    void delete(long id);
}
