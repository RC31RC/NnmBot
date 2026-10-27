package com.nnm.bot.admin.pojo.query.system;

/** 角色列表查询参数。 */
public record RoleQuery(
        String roleName,
        String roleCode,
        Integer current,
        Integer size
) {
}
