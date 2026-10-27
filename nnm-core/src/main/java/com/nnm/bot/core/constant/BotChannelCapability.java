package com.nnm.bot.core.constant;

/**
 * 渠道能力用于隔离不同协议的差异，业务功能应先检查能力再调用平台接口。
 */
public enum BotChannelCapability {
    TEXT_MESSAGE,
    IMAGE_MESSAGE,
    FILE_MESSAGE,
    MARKDOWN_MESSAGE,
    NATIVE_KEYBOARD,
    /** Discoverable scopes, possibly only those observed through events. */
    GROUP_DISCOVERY,
    /** An authoritative platform API enumerating the bot's current groups. */
    GROUP_LIST,
    GROUP_MEMBER_LIST,
    GROUP_MEMBER_DETAIL,
    /** Ordinary members joining or leaving a group. */
    GROUP_MEMBER_EVENTS,
    /** The bot itself being added to or removed from a group. */
    BOT_GROUP_MEMBERSHIP_EVENTS
}
