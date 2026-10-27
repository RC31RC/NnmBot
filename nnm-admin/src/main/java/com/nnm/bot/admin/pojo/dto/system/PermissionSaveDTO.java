package com.nnm.bot.admin.pojo.dto.system;

import com.nnm.bot.admin.constant.PermissionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** 保存权限的请求。 */
public record PermissionSaveDTO(
        /** 所属菜单主键。 */
        @NotNull
        Long menuId,
        /** 稳定权限码。 */
        @NotBlank
        @Size(max = 100)
        String code,
        /** 权限名称。 */
        @NotBlank
        @Size(max = 100)
        String name,
        /** 权限类型。 */
        @NotNull
        PermissionType type,
        /** 是否启用。 */
        @NotNull
        Boolean enabled
) {
}
