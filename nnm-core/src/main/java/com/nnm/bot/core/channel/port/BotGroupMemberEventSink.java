package com.nnm.bot.core.channel.port;

import com.nnm.bot.core.channel.model.BotDiscoveredMember;
import com.nnm.bot.core.constant.BotPlatform;

/** Receives incremental membership changes without creating scopes or system users. */
public interface BotGroupMemberEventSink {

    void handleMemberJoined(BotPlatform platform, String botAccountId,
                            String scopeKey, BotDiscoveredMember member);

    void handleMemberLeft(BotPlatform platform, String botAccountId,
                          String scopeKey, String platformUserId);
}
