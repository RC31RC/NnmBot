package com.nnm.bot.admin.dto.system;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

/** 修改用户系统角色的请求。 */
public record UserRoleRequest(
        /** 要分配的角色编码。 */
        @NotNull
        Set<String> roleCodes) {
}
