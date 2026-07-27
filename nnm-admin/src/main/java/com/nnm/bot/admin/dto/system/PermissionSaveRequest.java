package com.nnm.bot.admin.dto.system;

import com.nnm.bot.admin.domain.PermissionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PermissionSaveRequest(
        @NotNull Long menuId,
        @NotBlank @Size(max = 100) String code,
        @NotBlank @Size(max = 100) String name,
        @NotNull PermissionType type,
        @NotNull Boolean enabled
) {
}
