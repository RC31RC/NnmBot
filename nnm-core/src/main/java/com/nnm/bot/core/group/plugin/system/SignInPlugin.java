package com.nnm.bot.core.group.plugin.system;

import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.system.SystemCommandCodes;
import com.nnm.bot.core.group.service.GroupCommandService;
import jakarta.annotation.Resource;

/**
 * 群组签到命令。签到对象是逻辑群组成员，不是后台系统用户。
 */
@BotPluginCommand(
        code = SystemCommandCodes.SIGN_IN,
        name = "签到",
        gameCode = "system",
        order = Integer.MIN_VALUE + 2)
public class SignInPlugin extends BotPlugin {

    @Resource
    private GroupCommandService groupCommandService;

    @Override
    public CommandResult handle(CommandInvocation invocation) {
        return groupCommandService.signIn(invocation.context());
    }

}
