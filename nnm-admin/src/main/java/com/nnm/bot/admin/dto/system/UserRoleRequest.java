package com.nnm.bot.admin.dto.system;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record UserRoleRequest(@NotNull Set<String> roleCodes) {
}
