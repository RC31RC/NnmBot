package com.nnm.bot.core.group.plugin.system;

import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.system.SystemCommandCodes;
import com.nnm.bot.core.group.service.GroupCommandService;
import jakarta.annotation.Resource;

/**
 * 群组成员身份绑定命令。绑定资料属于群组成员，不会创建后台系统用户。
 */
@BotPluginCommand(
        code = SystemCommandCodes.BIND,
        matchType = MatchType.PREFIX,
        name = "绑定",
        gameCode = "system",
        order = Integer.MIN_VALUE + 3)
public class BindQqPlugin extends BotPlugin {

    @Resource
    private GroupCommandService groupCommandService;

    @Override
    public CommandResult handle(CommandInvocation invocation) {
        String qqNumber = invocation.arguments();
        if (!qqNumber.matches("\\d{5,20}")) {
            return CommandResult.text("绑定命令格式：绑定 QQ号");
        }
        return groupCommandService.bindQq(invocation.context(), qqNumber);
    }

}
