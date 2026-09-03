package com.nnm.bot.core.constant;

/**
 * 渠道能力用于隔离不同协议的差异，业务功能应先检查能力再调用平台接口。
 */
public enum BotChannelCapability {
    TEXT_MESSAGE,
    IMAGE_MESSAGE,
    FILE_MESSAGE,
    MARKDOWN_MESSAGE,
    GROUP_MEMBER_LIST,
    GROUP_MEMBER_DETAIL
}
