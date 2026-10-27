package com.nnm.bot.admin.pojo.vo.bot.command;

import com.nnm.bot.core.constant.CommandBoundaryPolicy;

/** 管理端展示的命令触发词。 */
public record BotCommandTriggerVO(
        String name,
        CommandBoundaryPolicy boundaryPolicy
) {
}
