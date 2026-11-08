package com.nnm.bot.core.plugin.execution;

import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.core.constant.CommandExecutionConstants.CommandErrorCode;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInput;
import com.nnm.bot.core.plugin.model.CommandMatch;

/** 将命令、插件和输入类型绑定；所有协议调用均交给同一 Dispatcher。 */
public record CommandCall<P, A>(CommandMatch match, BotPlugin<P, A> plugin, CommandInput<A> input) {
    public CommandCall {
        if (match == null || plugin == null || input == null || match.command().plugin() != plugin)
            throw new SystemException(CommandErrorCode.COMMAND_INPUT_INVALID, "命令输入必须属于当前命令的 Plugin");
    }

    /** 捕获异构注册表中的插件类型，创建机器人文本调用。 */
    public static CommandCall<?, ?> text(CommandMatch match) {
        if (match == null)
            throw new SystemException(CommandErrorCode.COMMAND_INPUT_INVALID, "文本调用必须提供已匹配命令");
        return text(match, match.command().plugin());
    }

    private static <P, A> CommandCall<P, A> text(CommandMatch match, BotPlugin<P, A> plugin) {
        return new CommandCall<>(match, plugin, CommandInput.text());
    }

    /** 创建已绑定 DTO 的调用，仍须经过 Dispatcher 的准入与事务管线。 */
    public static <P, A> CommandCall<P, A> structured(CommandMatch match, BotPlugin<P, A> plugin, A parameters) {
        return new CommandCall<>(match, plugin, CommandInput.structured(parameters));
    }

    /** 更新最新配置或会话身份时保留输入与插件的泛型关系。 */
    public CommandCall<P, A> withMatch(CommandMatch latest) {
        return new CommandCall<>(latest, plugin, input);
    }
}
