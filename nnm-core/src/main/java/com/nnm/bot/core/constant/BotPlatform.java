package com.nnm.bot.core.constant;

import java.util.Set;

public enum BotPlatform {
    ONEBOT(
            BotChannelCapability.TEXT_MESSAGE,
            BotChannelCapability.IMAGE_MESSAGE,
            BotChannelCapability.FILE_MESSAGE,
            BotChannelCapability.GROUP_MEMBER_LIST,
            BotChannelCapability.GROUP_MEMBER_DETAIL),
    QQ_OFFICIAL(
            BotChannelCapability.TEXT_MESSAGE,
            BotChannelCapability.IMAGE_MESSAGE,
            BotChannelCapability.FILE_MESSAGE,
            BotChannelCapability.MARKDOWN_MESSAGE);

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
