package com.nnm.bot.core.onebot.connection;

import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.core.constant.BotChannelErrorCode;

/** OneBot 账号探测失败，包含连接、超时或协议响应错误。 */
public class OneBotAccountProbeException extends SystemException {

    public OneBotAccountProbeException(String message) {
        super(BotChannelErrorCode.INSTANCE_START_FAILED, message);
    }

    public OneBotAccountProbeException(String message, Throwable cause) {
        super(BotChannelErrorCode.INSTANCE_START_FAILED, message, cause);
    }
}
