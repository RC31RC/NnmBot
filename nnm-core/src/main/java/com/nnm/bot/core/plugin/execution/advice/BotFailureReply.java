package com.nnm.bot.core.plugin.execution.advice;

import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.core.constant.CommandExecutionConstants.CommandErrorCode;

import java.util.Objects;

/** 显式区分完整命令回复、纯文字提示和静默处理；不决定事务或异常传播。 */
public sealed interface BotFailureReply
    permits BotFailureReply.Command, BotFailureReply.Notice, BotFailureReply.Silent {

    record Command(CommandResult result) implements BotFailureReply {
        public Command {
            Objects.requireNonNull(result, "result");
        }
    }

    record Notice(String text) implements BotFailureReply {
        public Notice {
            if (text == null || text.isBlank())
                throw new SystemException(CommandErrorCode.ADVICE_CONFIGURATION_INVALID,
                    "BotExceptionAdvice must provide non-blank notice text");
        }
    }

    record Silent() implements BotFailureReply {
    }

    static Command command(CommandResult result) {
        return new Command(result);
    }

    static Notice notice(String text) {
        return new Notice(text);
    }

    static Silent silent() {
        return new Silent();
    }
}
