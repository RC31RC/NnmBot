package com.nnm.bot.admin.pojo.query.bot.command;

import com.nnm.bot.core.constant.BotMessageScope;
import com.nnm.bot.core.constant.BotPlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Map;

public record BotCommandGroupPreviewQuery(int version, String sourceCode,
        @NotNull
        BotPlatform platform,
        @NotNull
        BotMessageScope scope,
        @NotBlank
        String botAccountId,
        @NotBlank
        String senderId,
        @NotBlank
        String conversationId,
        @Size(max = 32)
        Map<String, String> actionContext) {
    public BotCommandGroupPreviewQuery {
        actionContext = actionContext == null ? Map.of() : Map.copyOf(actionContext);
        if (actionContext.size() > 32 || actionContext.entrySet().stream().anyMatch(entry ->
                !entry.getKey().matches("[a-z][a-zA-Z0-9_.]{0,63}") || entry.getValue().length() > 512)) {
            throw new IllegalArgumentException("预览上下文键或长度不合法");
        }
    }
}
