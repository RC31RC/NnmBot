package com.nnm.bot.core.plugin.execution;

import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandMatch;
import com.nnm.bot.core.plugin.model.CommandResult;
import java.util.Objects;

/**
 * 将一个 Plugin 与它自己的准备输入绑定，避免异构注册表丢失泛型关系。
 *
 * @param match 已完成准入与认领的命令
 * @param plugin 该命令对应的 Plugin
 * @param input 事务外准备的输入
 * @param <P> 输入的具体类型
 */
public record PreparedCommand<P>(CommandMatch match, BotPlugin<P> plugin, P input) {

    public PreparedCommand {
        Objects.requireNonNull(match, "命令不能为空");
        if (match.command().plugin() != plugin) {
            throw new IllegalArgumentException("准备输入必须属于当前命令的 Plugin");
        }
    }

    /** 捕获注册表通配类型，在同一个类型关系内完成准备。 */
    public static PreparedCommand<?> prepare(CommandMatch match) {
        return prepare(match, match.command().plugin());
    }

    /** 通过泛型捕获绑定 Plugin 与返回值，不使用未经检查的强制转换。 */
    private static <P> PreparedCommand<P> prepare(CommandMatch match, BotPlugin<P> plugin) {
        return new PreparedCommand<>(match, plugin, plugin.prepare(match.invocation()));
    }

    /** 在调用方的业务事务中执行已绑定的 Plugin。 */
    public CommandResult execute() {
        return plugin.handle(match.invocation(), input);
    }
}
