package com.nnm.bot.core.channel.port;

import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotMemberQueryResult;
import com.nnm.bot.core.constant.BotPlatform;

/** 查询已知平台群组或其他支持成员列表的作用域。 */
public interface BotGroupMemberQueryProvider {

    BotPlatform platform();

    BotMemberQueryResult query(BotChannelInstanceRecord instance, String scopeKey);
}
