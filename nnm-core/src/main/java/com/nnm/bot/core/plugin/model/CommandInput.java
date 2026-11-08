package com.nnm.bot.core.plugin.model;

import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.core.constant.CommandExecutionConstants.CommandErrorCode;
import com.nnm.bot.core.constant.CommandExecutionConstants.CommandInputMode;

/** 参数来源标记与类型化输入；结构化输入可以为 null，以支持无参命令。 */
public record CommandInput<A>(CommandInputMode mode, A parameters) {
    public CommandInput {
        if (mode == null || (mode == CommandInputMode.TEXT && parameters != null))
            throw new SystemException(CommandErrorCode.COMMAND_INPUT_INVALID, "文本输入不能预先携带结构化参数，且必须指定输入模式");
    }

    /** 机器人文本在事务管线内解析。 */
    public static <A> CommandInput<A> text() {
        return new CommandInput<>(CommandInputMode.TEXT, null);
    }

    /** 可信协议边界已完成 DTO 绑定，跳过文本解析但保留统一校验。 */
    public static <A> CommandInput<A> structured(A parameters) {
        return new CommandInput<>(CommandInputMode.STRUCTURED, parameters);
    }
}
