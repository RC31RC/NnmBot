package com.nnm.bot.core.plugin.model;

import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.message.model.BotIncomingAttachment;
import com.nnm.bot.core.message.model.BotMessageReference;
import java.util.List;

import java.util.Objects;

/**
 * 注册表完成命令匹配后传给插件的稳定调用参数。
 *
 * @param context 当前消息上下文
 * @param commandCode 匹配到的稳定命令编码
 * @param matchedAlias 实际匹配到的命令名称
 * @param arguments 命令参数
 */
public record CommandInvocation(BotMessageContext context, String commandCode, String matchedAlias, String arguments) {

    public CommandInvocation {
        context = Objects.requireNonNull(context, "context must not be null");
        commandCode = requireText(commandCode, "commandCode");
        matchedAlias = requireText(matchedAlias, "matchedAlias");
        arguments = Objects.requireNonNullElse(arguments, "").trim();
    }

    /** 返回本条消息的附件元数据，不执行下载。 */
    public List<BotIncomingAttachment> attachments() {
        return context.attachments();
    }

    /** 返回显式引用元数据，不自动读取原消息。 */
    public BotMessageReference reference() {
        return context.reference();
    }

    private static String requireText(String value, String field) {
        String normalized = Objects.requireNonNull(value, field + " must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return normalized;
    }
}
