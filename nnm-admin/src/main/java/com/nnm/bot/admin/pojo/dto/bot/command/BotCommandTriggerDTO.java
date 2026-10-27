package com.nnm.bot.admin.pojo.dto.bot.command;

import com.nnm.bot.core.constant.CommandBoundaryPolicy;

/** 管理端展示的命令触发词。 */
public record BotCommandTriggerDTO(
        String name,
        CommandBoundaryPolicy boundaryPolicy
) {
}
