package com.nnm.bot.admin.dto.bot;

import com.nnm.bot.core.constant.CommandBoundaryPolicy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** 保存命令触发词的请求。 */
public record BotCommandTriggerSaveRequest(
        @NotBlank @Size(max = 100) String name,
        @NotNull CommandBoundaryPolicy boundaryPolicy
) {
}
