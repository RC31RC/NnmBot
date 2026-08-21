package com.nnm.bot.core.onebot;

/** OneBot 账号探测失败，包含连接、超时或协议响应错误。 */
public class OneBotAccountProbeException extends RuntimeException {

    public OneBotAccountProbeException(String message) {
        super(message);
    }

    public OneBotAccountProbeException(String message, Throwable cause) {
        super(message, cause);
    }
}
