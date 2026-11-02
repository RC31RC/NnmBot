package com.nnm.bot.core.plugin.response;

import com.nnm.bot.core.plugin.model.DeliveryReport;
import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.core.constant.BotChannelErrorCode;

/** 投递故障可能发生在部分消息被接受之后，携带原报告，禁止再补发通用错误回复。 */
public class ReplyDeliveryException extends SystemException {
    private final DeliveryReport report;

    public ReplyDeliveryException(DeliveryReport report, RuntimeException cause) {
        super(BotChannelErrorCode.MESSAGE_SEND_FAILED, "回复投递失败，不自动重发", cause);
        this.report = report;
    }

    public DeliveryReport report() {
        return report;
    }
}
