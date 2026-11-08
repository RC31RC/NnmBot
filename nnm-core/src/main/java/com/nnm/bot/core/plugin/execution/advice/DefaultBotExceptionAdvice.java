package com.nnm.bot.core.plugin.execution.advice;

import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.core.plugin.execution.CommandRejectedException;
import com.nnm.bot.core.plugin.response.ReplyDeliveryException;
import lombok.extern.slf4j.Slf4j;

/** 默认机器人异常文案；业务模块可用更高优先级的 Advice 覆盖。 */
@Slf4j
@BotExceptionAdvice
public class DefaultBotExceptionAdvice {
    private static final String DEFAULT_FAILURE_NOTICE = "命令执行失败，请稍后再试";

    @BotExceptionHandler(CommandRejectedException.class)
    public BotFailureDecision rejected(CommandRejectedException failure) {
        BotFailureReply reply =
            failure.result() == null ? BotFailureReply.silent() : BotFailureReply.command(failure.result());
        return BotFailureDecision.complete(reply);
    }

    @BotExceptionHandler(BusinessException.class)
    public BotFailureDecision business(BusinessException failure) {
        log.info("命令业务拒绝 code={} reason={}", failure.getCode(), failure.getMessage());
        return BotFailureDecision.complete(BotFailureReply.notice(failure.getMessage()));
    }

    @BotExceptionHandler(ReplyDeliveryException.class)
    public BotFailureDecision delivery(ReplyDeliveryException failure) {
        // 平台可能已收到部分消息，不能因异常链上的系统错误再补发。
        log.error("命令回复投递失败，禁止补发", failure);
        return BotFailureDecision.rethrow(BotFailureReply.silent());
    }

    @BotExceptionHandler(SystemException.class)
    public BotFailureDecision system(SystemException failure) {
        log.error("命令系统异常 code={}", failure.getCode(), failure);
        return BotFailureDecision.rethrow(BotFailureReply.notice(failure.getDescription()));
    }

    @BotExceptionHandler(Exception.class)
    public BotFailureDecision unknown(Exception failure) {
        log.error("命令执行失败", failure);
        return BotFailureDecision.rethrow(BotFailureReply.notice(DEFAULT_FAILURE_NOTICE));
    }
}
