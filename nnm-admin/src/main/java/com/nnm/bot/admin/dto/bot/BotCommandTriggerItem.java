package com.nnm.bot.admin.dto.bot;

import com.nnm.bot.core.constant.CommandBoundaryPolicy;

/** 管理端展示的命令触发词。 */
public record BotCommandTriggerItem(
        String name,
        CommandBoundaryPolicy boundaryPolicy
) {
}
