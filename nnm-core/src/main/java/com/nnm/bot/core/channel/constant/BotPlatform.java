package com.nnm.bot.core.channel.constant;

import java.util.Set;

/** 已接入机器人的平台及其协议固有能力集合。 */
public enum BotPlatform {
    ONEBOT(BotChannelCapability.TEXT_MESSAGE, BotChannelCapability.IMAGE_MESSAGE, BotChannelCapability.FILE_MESSAGE,
        BotChannelCapability.GROUP_MEMBER_LIST, BotChannelCapability.GROUP_MEMBER_DETAIL,
        BotChannelCapability.GROUP_DISCOVERY, BotChannelCapability.GROUP_LIST,
        BotChannelCapability.GROUP_MEMBER_EVENTS),
    QQ_OFFICIAL(BotChannelCapability.TEXT_MESSAGE, BotChannelCapability.IMAGE_MESSAGE,
        BotChannelCapability.FILE_MESSAGE, BotChannelCapability.MARKDOWN_MESSAGE, BotChannelCapability.GROUP_DISCOVERY,
        BotChannelCapability.GROUP_MEMBER_LIST, BotChannelCapability.GROUP_MEMBER_DETAIL,
        BotChannelCapability.GROUP_MEMBER_EVENTS, BotChannelCapability.BOT_GROUP_MEMBERSHIP_EVENTS);

    private final Set<BotChannelCapability> capabilities;

    BotPlatform(BotChannelCapability... capabilities) {
        this.capabilities = Set.of(capabilities);
    }

    /**
     * 返回协议固有能力，运行时实例配置不应修改该集合。
     */
    public Set<BotChannelCapability> capabilities() {
        return this.capabilities;
    }

}
