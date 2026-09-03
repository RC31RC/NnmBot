package com.nnm.bot.admin.dto.bot;

import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.constant.ChannelOutputMode;

/** 命令在一个消息渠道上的输出配置。 */
/** 命令在单个渠道上的输出配置。 */
public record BotCommandChannelOutputItem(
        /** 渠道平台。 */
        BotPlatform platform,
        /** 渠道显示名称。 */
        String displayName,
        /** 是否显式配置过输出方式。 */
        boolean configured,
        /** 当前渠道输出方式。 */
        ChannelOutputMode outputMode) {
}
