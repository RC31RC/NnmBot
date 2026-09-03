package com.nnm.bot.core.group.service;

import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandResult;

/**
 * 群组成员命令服务，与后台系统用户和 RBAC 业务相互独立。
 */
public interface GroupCommandService {

    CommandResult signIn(BotMessageContext context);

    CommandResult currentPoints(BotMessageContext context);

    CommandResult bindQq(BotMessageContext context, String qqUserId);
}
