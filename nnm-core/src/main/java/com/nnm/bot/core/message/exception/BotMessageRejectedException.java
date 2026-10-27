package com.nnm.bot.core.message.exception;

import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.constant.BotChannelErrorCode;

/** 只用于协议明确确认未接受的请求；缺失回执、超时和断线不能使用此类型。 */
public class BotMessageRejectedException extends BusinessException {
    private final boolean sameInstanceTextFallbackAllowed;

    public BotMessageRejectedException(String message, boolean sameInstanceTextFallbackAllowed) {
        super(BotChannelErrorCode.MESSAGE_SEND_FAILED, message);
        this.sameInstanceTextFallbackAllowed = sameInstanceTextFallbackAllowed;
    }

    public BotMessageRejectedException(String message, Throwable cause,
                                       boolean sameInstanceTextFallbackAllowed) {
        super(BotChannelErrorCode.MESSAGE_SEND_FAILED, message, cause);
        this.sameInstanceTextFallbackAllowed = sameInstanceTextFallbackAllowed;
    }

    public boolean sameInstanceTextFallbackAllowed() {
        return sameInstanceTextFallbackAllowed;
    }
}
