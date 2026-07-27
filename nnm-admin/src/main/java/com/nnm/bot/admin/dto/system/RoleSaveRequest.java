package com.nnm.bot.admin.dto.system;

import com.nnm.bot.admin.domain.DataScope;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RoleSaveRequest(
        @NotBlank @Size(max = 100) String roleName,
        @NotBlank @Size(max = 50) String roleCode,
        @Size(max = 255) String description,
        @NotNull Boolean enabled,
        @NotNull DataScope dataScope
) {
}
