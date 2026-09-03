package com.nnm.bot.admin.dto.system;

/** 角色列表查询参数。 */
public record RoleQueryRequest(
        String roleName,
        String roleCode,
        Integer current,
        Integer size
) {
}
