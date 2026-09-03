package com.nnm.bot.admin.dto.system;

import com.nnm.bot.admin.constant.DataScope;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/** 保存系统角色的请求。 */
public record RoleSaveRequest(
        /** 角色名称。 */
        @NotBlank @Size(max = 100) String roleName,
        /** 角色编码。 */
        @NotBlank @Size(max = 50) String roleCode,
        /** 角色说明。 */
        @Size(max = 255) String description,
        /** 是否启用。 */
        @NotNull Boolean enabled,
        /** 数据范围。 */
        @NotNull DataScope dataScope,
        /** 权限等级。 */
        @PositiveOrZero Integer authorityLevel
) {
}
