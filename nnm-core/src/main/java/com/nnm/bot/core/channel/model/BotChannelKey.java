package com.nnm.bot.core.channel.model;

import cn.hutool.core.text.CharSequenceUtil;

import com.nnm.bot.core.channel.constant.BotPlatform;
import org.springframework.util.Assert;

/**
 * 机器人渠道账号的全局路由键。
 *
 * @param platform 接入平台
 * @param botAccountId 平台侧机器人账号
 */
public record BotChannelKey(BotPlatform platform, String botAccountId) {

    public BotChannelKey {
        Assert.notNull(platform, "platform must not be null");
        Assert.hasText(botAccountId, "botAccountId must not be blank");
        botAccountId = CharSequenceUtil.trim(botAccountId);
    }
}
