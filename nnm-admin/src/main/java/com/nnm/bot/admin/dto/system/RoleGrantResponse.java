package com.nnm.bot.admin.dto.system;

import java.util.List;
import java.util.Set;

/** 角色授权页面所需的菜单树和已选项。 */
public record RoleGrantResponse(
        /** 可授权菜单树。 */
        List<MenuItem> menuTree,
        /** 已授权菜单主键。 */
        Set<Long> menuIds,
        /** 已授权权限主键。 */
        Set<Long> permissionIds
) {
}
