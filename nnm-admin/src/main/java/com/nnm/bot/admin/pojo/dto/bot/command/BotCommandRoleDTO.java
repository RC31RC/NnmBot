package com.nnm.bot.admin.pojo.dto.bot.command;

/** 命令与系统角色的关联展示对象。 */
public record BotCommandRoleDTO(
        /** 系统角色主键。 */
        long roleId,
        /** 系统角色编码。 */
        String roleCode,
        /** 系统角色名称。 */
        String roleName,
        /** 角色是否启用。 */
        boolean enabled,
        /** 当前命令是否已分配给该角色。 */
        boolean assigned) {
}
