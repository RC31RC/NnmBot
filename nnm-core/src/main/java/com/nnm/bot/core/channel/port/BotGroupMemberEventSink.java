package com.nnm.bot.core.channel.port;

import com.nnm.bot.core.channel.model.BotDiscoveredMember;
import com.nnm.bot.core.channel.constant.BotPlatform;

/** 接收成员增量事件，不自动创建作用域或系统用户。 */
public interface BotGroupMemberEventSink {

    /** 接收平台通知的成员加入事件。
     *
     * @param platform 平台类型
     * @param botAccountId 接收事件的机器人账号
     * @param scopeKey 平台群组或作用域标识
     * @param member 加入成员的已解析信息
     */
    void handleMemberJoined(BotPlatform platform, String botAccountId, String scopeKey, BotDiscoveredMember member);

    /** 接收平台通知的成员退出事件。
     *
     * @param platform 平台类型
     * @param botAccountId 接收事件的机器人账号
     * @param scopeKey 平台群组或作用域标识
     * @param platformUserId 退出成员的平台用户标识
     */
    void handleMemberLeft(BotPlatform platform, String botAccountId, String scopeKey, String platformUserId);
}
