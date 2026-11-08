package com.nnm.bot.core.group.plugin.system;

import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.constant.SystemCommandCodes;
import com.nnm.bot.core.group.service.GroupCommandService;
import com.nnm.bot.core.group.pojo.dto.GroupCommandOutcomeDTO;
import com.nnm.bot.core.group.service.PointBalance;
import com.nnm.bot.core.plugin.model.PointCommandDisplay;
import jakarta.annotation.Resource;

/**
 * 群组签到命令。签到对象是逻辑群组成员，不是后台系统用户。
 */
@BotPluginCommand(code = SystemCommandCodes.SIGN_IN)
public class SignInPlugin extends BotPlugin<Void, Void> {

    @Resource
    private GroupCommandService groupCommandService;

    @Override
    public CommandResult handle(CommandInvocation invocation, Void prepared, Void parsed) {
        GroupCommandOutcomeDTO outcome = groupCommandService.signIn(invocation.context());
        if (outcome.rejected())
            return CommandResult.rejected(outcome.text());
        PointBalance balance = outcome.balance();
        return CommandResult.html(outcome.text(),
            new PointCommandDisplay(balance.points(), balance.totalEarned(), balance.totalSpent()));
    }

}
