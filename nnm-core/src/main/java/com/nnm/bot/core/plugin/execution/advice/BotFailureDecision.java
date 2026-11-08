package com.nnm.bot.core.plugin.execution.advice;

import com.nnm.bot.core.constant.CommandExecutionConstants.FailurePropagation;

import java.util.Objects;

/** 异常映射结果；回复内容与是否继续向外报告失败是两个独立维度。 */
public record BotFailureDecision(BotFailureReply reply, FailurePropagation propagation) {
    public BotFailureDecision {
        Objects.requireNonNull(reply, "reply");
        Objects.requireNonNull(propagation, "propagation");
    }

    public static BotFailureDecision complete(BotFailureReply reply) {
        return new BotFailureDecision(reply, FailurePropagation.COMPLETE);
    }

    public static BotFailureDecision rethrow(BotFailureReply reply) {
        return new BotFailureDecision(reply, FailurePropagation.RETHROW);
    }
}
