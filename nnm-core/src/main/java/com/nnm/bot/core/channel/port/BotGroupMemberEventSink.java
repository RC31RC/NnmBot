package com.nnm.bot.core.channel.port;

import com.nnm.bot.core.channel.model.BotDiscoveredMember;
import com.nnm.bot.core.constant.BotPlatform;

/** 接收成员增量事件，不自动创建作用域或系统用户。 */
public interface BotGroupMemberEventSink {

    void handleMemberJoined(BotPlatform platform, String botAccountId, String scopeKey, BotDiscoveredMember member);

    void handleMemberLeft(BotPlatform platform, String botAccountId, String scopeKey, String platformUserId);
}
