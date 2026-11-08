package com.nnm.bot.core.plugin.execution;

import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.constant.CommandExecutionConstants.CommandInputMode;
import com.nnm.bot.core.plugin.model.CommandMatch;
import com.nnm.bot.core.plugin.model.CommandResult;

/**
 * 将命令调用与自己的准备结果绑定，避免异构注册表丢失泛型关系。
 *
 * @param call 已完成准入的命令及参数来源
 * @param input 事务外准备结果
 * @param <P> 准备结果的类型
 * @param <A> 类型化参数的类型
 */
public record PreparedCommand<P, A>(CommandCall<P, A> call, P input) {
    /** 只有机器人文本需要准备消息或附件；已绑定 DTO 不再读取原消息。 */
    public static <P, A> PreparedCommand<P, A> prepare(CommandCall<P, A> call) {
        P prepared =
            call.input().mode() == CommandInputMode.TEXT ? call.plugin().prepare(call.match().invocation()) : null;
        return new PreparedCommand<>(call, prepared);
    }

    public CommandMatch match() {
        return call.match();
    }

    public BotPlugin<P, A> plugin() {
        return call.plugin();
    }

    /** 两种输入在同一个 Spring 事务代理内执行。 */
    public CommandResult execute() {
        return call.plugin().invoke(call.match().invocation(), input, call.input());
    }
}
