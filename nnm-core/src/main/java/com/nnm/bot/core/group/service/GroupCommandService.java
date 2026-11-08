package com.nnm.bot.core.group.service;

import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.group.pojo.dto.GroupCommandOutcomeDTO;

/**
 * 群组成员命令服务，与后台系统用户和 RBAC 业务相互独立。
 */
public interface GroupCommandService {

    GroupCommandOutcomeDTO signIn(BotMessageContext context);

    GroupCommandOutcomeDTO currentPoints(BotMessageContext context);

    GroupCommandOutcomeDTO bindQq(BotMessageContext context, String qqUserId);
}
