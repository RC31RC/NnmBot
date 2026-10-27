package com.nnm.bot.admin.pojo.dto.system;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

/** 保存角色菜单和权限授权关系的请求。 */
public record RoleGrantSaveDTO(
        /** 授予角色的菜单主键。 */
        @NotNull
        Set<Long> menuIds,
        /** 授予角色的权限主键。 */
        @NotNull
        Set<Long> permissionIds
) {
}
