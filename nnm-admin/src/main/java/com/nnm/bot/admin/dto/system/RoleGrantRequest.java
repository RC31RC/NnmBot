package com.nnm.bot.admin.dto.system;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record RoleGrantRequest(
        @NotNull Set<Long> menuIds,
        @NotNull Set<Long> permissionIds
) {
}
