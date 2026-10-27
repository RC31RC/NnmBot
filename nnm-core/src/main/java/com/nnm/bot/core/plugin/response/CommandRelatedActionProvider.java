package com.nnm.bot.core.plugin.response;

import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.ReplyAction;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import java.util.List;

/**
 * 根据已完成的命令结果生成末尾导航动作。实现只能返回受控的普通命令/链接动作，不能执行目标命令。
 */
public interface CommandRelatedActionProvider {
    List<ReplyAction> actions(BotMessageContext context, RuntimeCommand current, CommandResult result);
}
