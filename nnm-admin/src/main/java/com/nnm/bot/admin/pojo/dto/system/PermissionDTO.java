package com.nnm.bot.admin.pojo.dto.system;

/** 权限列表项。 */
public record PermissionDTO(
        /** 权限主键。 */
        Long id,
        /** 权限编码。 */
        String code,
        /** 权限名称。 */
        String name,
        /** 权限类型。 */
        String type,
        /** 是否启用。 */
        boolean enabled
) {
}
