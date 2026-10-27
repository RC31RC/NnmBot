package com.nnm.bot.core.channel.port;

import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotMemberQueryResult;
import com.nnm.bot.core.constant.BotPlatform;

/** Queries members for a known platform group or other member-bearing scope. */
public interface BotGroupMemberQueryProvider {

    BotPlatform platform();

    BotMemberQueryResult query(BotChannelInstanceRecord instance, String scopeKey);
}
