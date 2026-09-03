package com.nnm.bot.admin.dto.system;

import java.time.LocalDateTime;

/** 系统角色列表项。 */
public record RoleListItem(
        /** 角色主键。 */
        Long roleId,
        /** 角色名称。 */
        String roleName,
        /** 角色编码。 */
        String roleCode,
        /** 角色说明。 */
        String description,
        /** 是否启用。 */
        boolean enabled,
        /** 数据范围。 */
        String dataScope,
        /** 权限等级。 */
        int authorityLevel,
        /** 当前操作者是否可管理该角色。 */
        boolean manageable,
        /** 当前操作者是否可分配该角色。 */
        boolean assignable,
        /** 创建时间。 */
        LocalDateTime createTime
) {
}
