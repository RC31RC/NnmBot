package com.nnm.bot.core.plugin.model;

import com.nnm.bot.core.message.model.BotMessageContext;

import java.util.Objects;

/**
 * 注册表完成命令匹配后传给插件的稳定调用参数。
 *
 * @param context 当前消息上下文
 * @param commandCode 匹配到的稳定命令编码
 * @param matchedAlias 实际匹配到的命令名称
 * @param arguments 命令参数
 */
public record CommandInvocation(
        BotMessageContext context,
        String commandCode,
        String matchedAlias,
        String arguments
) {

    public CommandInvocation {
        context = Objects.requireNonNull(context, "context must not be null");
        commandCode = requireText(commandCode, "commandCode");
        matchedAlias = requireText(matchedAlias, "matchedAlias");
        arguments = Objects.requireNonNullElse(arguments, "").trim();
    }

    private static String requireText(String value, String field) {
        String normalized = Objects.requireNonNull(
                value, field + " must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return normalized;
    }
}
