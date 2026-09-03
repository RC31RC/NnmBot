package com.nnm.bot.core.plugin.model;

import java.util.Objects;

/**
 * 单次消息匹配结果。
 *
 * @param command 匹配到的运行时命令
 * @param invocation 传给插件的调用参数
 */
public record CommandMatch(
        RuntimeCommand command,
        CommandInvocation invocation
) {

    public CommandMatch {
        command = Objects.requireNonNull(command, "command must not be null");
        invocation = Objects.requireNonNull(invocation, "invocation must not be null");
    }
}
