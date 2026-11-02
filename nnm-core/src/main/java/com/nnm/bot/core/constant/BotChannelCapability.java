package com.nnm.bot.core.constant;

/**
 * 渠道能力用于隔离不同协议的差异，业务功能应先检查能力再调用平台接口。
 */
public enum BotChannelCapability {
    TEXT_MESSAGE, IMAGE_MESSAGE, FILE_MESSAGE, MARKDOWN_MESSAGE, NATIVE_KEYBOARD,
    /** 可发现的作用域，可能仅包含通过事件观察到的会话。 */
    GROUP_DISCOVERY,
    /** 由平台权威接口枚举机器人当前所在群组。 */
    GROUP_LIST, GROUP_MEMBER_LIST, GROUP_MEMBER_DETAIL,
    /** 普通成员入群或离群的事件能力。 */
    GROUP_MEMBER_EVENTS,
    /** 机器人自身被加入或移出群组的事件能力。 */
    BOT_GROUP_MEMBERSHIP_EVENTS
}
