package com.nnm.bot.core.group.plugin.system;

import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.constant.SystemCommandCodes;
import com.nnm.bot.core.group.service.GroupCommandService;
import com.nnm.bot.core.group.pojo.dto.GroupCommandOutcomeDTO;
import jakarta.annotation.Resource;

/**
 * 群组成员身份绑定命令。绑定资料属于群组成员，不会创建后台系统用户。
 */
@BotPluginCommand(code = SystemCommandCodes.BIND, matchType = MatchType.PREFIX)
public class BindQqPlugin extends BotPlugin<Void, String> {
    /**
     * 取得已匹配的命令参数，供业务命令解析。
     *
     * @param invocation 当前命令调用
     * @param prepared 无额外准备输入
     * @return 已规范化的命令参数
     */
    @Override
    protected String parse(CommandInvocation invocation, Void prepared) {
        String qqNumber = invocation.arguments() == null ? "" : invocation.arguments().trim();
        return qqNumber.matches("\\d{5,20}") ? qqNumber : null;
    }

    @Resource
    private GroupCommandService groupCommandService;

    @Override
    public CommandResult handle(CommandInvocation invocation, Void prepared, String parsed) {
        if (parsed == null) {
            return CommandResult.rejected("绑定命令格式：绑定 QQ号");
        }
        GroupCommandOutcomeDTO outcome = groupCommandService.bindQq(invocation.context(), parsed);
        return outcome.rejected() ? CommandResult.rejected(outcome.text()) : CommandResult.text(outcome.text());
    }

}
