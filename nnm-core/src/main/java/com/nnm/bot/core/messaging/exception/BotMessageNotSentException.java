package com.nnm.bot.core.messaging.exception;

import com.nnm.bot.common.error.SystemException;
import com.nnm.bot.core.channel.constant.BotChannelErrorCode;

/** 仅用于适配器确认创建请求尚未提交的失败，不能用于请求超时或缺失回执。 */
public class BotMessageNotSentException extends SystemException {
    public BotMessageNotSentException(String message) {
        super(BotChannelErrorCode.MESSAGE_SEND_FAILED, message);
    }

    public BotMessageNotSentException(String message, Throwable cause) {
        super(BotChannelErrorCode.MESSAGE_SEND_FAILED, message, cause);
    }
}
