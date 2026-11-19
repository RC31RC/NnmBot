package com.nnm.bot.core.messaging.exception;

import com.nnm.bot.common.error.BusinessException;
import com.nnm.bot.core.channel.constant.BotChannelCapability;
import com.nnm.bot.core.channel.constant.BotChannelErrorCode;
import com.nnm.bot.core.channel.constant.BotPlatform;

/**
 * 渠道明确不具备某项消息能力时使用的异常。
 *
 * <p>
 * 它只表示“当前能力不可用”，不代表网络失败或业务执行失败； 响应策略可以据此进行受控降级，其他异常必须继续向上抛出。
 * </p>
 */
public class BotChannelCapabilityException extends BusinessException {

    public BotChannelCapabilityException(BotPlatform platform, BotChannelCapability capability, String detail) {
        super(BotChannelErrorCode.UNSUPPORTED_MESSAGE_CAPABILITY,
            "渠道 " + platform + " 不支持 " + capability + "：" + detail);
    }

    public BotChannelCapabilityException(BotPlatform platform, BotChannelCapability capability, String detail,
        Throwable cause) {
        super(BotChannelErrorCode.UNSUPPORTED_MESSAGE_CAPABILITY,
            "渠道 " + platform + " 不支持 " + capability + "：" + detail, cause);
    }
}
