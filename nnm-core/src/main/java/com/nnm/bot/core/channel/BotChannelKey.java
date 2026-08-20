package com.nnm.bot.core.channel;

import com.nnm.bot.core.message.BotPlatform;
import org.springframework.util.Assert;

/** 机器人渠道账号的全局路由键。 */
public record BotChannelKey(BotPlatform platform, String botAccountId) {

    public BotChannelKey {
        Assert.notNull(platform, "platform must not be null");
        Assert.hasText(botAccountId, "botAccountId must not be blank");
        botAccountId = botAccountId.trim();
    }
}
