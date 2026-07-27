package com.nnm.bot.admin.dto.system;

import jakarta.validation.constraints.NotNull;

public record UserStatusRequest(@NotNull Boolean enabled) {
}
