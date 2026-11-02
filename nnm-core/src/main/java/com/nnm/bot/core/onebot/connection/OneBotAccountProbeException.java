package com.nnm.bot.core.onebot.connection;

import com.nnm.bot.common.exception.SystemException;

/** OneBot 账号探测失败，包含连接、超时或协议响应错误。 */
public class OneBotAccountProbeException extends SystemException {

    public OneBotAccountProbeException(String message) {
        super(message);
    }

    public OneBotAccountProbeException(String message, Throwable cause) {
        super(message, cause);
    }
}
