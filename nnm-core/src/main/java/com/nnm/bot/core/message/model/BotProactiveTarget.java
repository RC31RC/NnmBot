package com.nnm.bot.core.message.model;

import com.nnm.bot.core.constant.BotPlatform;
import java.util.List;
import java.util.Objects;

/** 主动群目标带账号作用域；QQ OpenID 不能跨应用当作同一群。 */
public record BotProactiveTarget(BotPlatform platform, String botAccountId, String conversationId,
                                 List<String> mentionUserIds) {
    public BotProactiveTarget {
        Objects.requireNonNull(platform, "platform");
        if (botAccountId == null || botAccountId.isBlank() || conversationId == null || conversationId.isBlank()) {
            throw new IllegalArgumentException("Proactive account and conversation are required");
        }
        mentionUserIds = List.copyOf(mentionUserIds);
    }

    public String identity() {
        return platform + ":GROUP:" + (platform == BotPlatform.QQ_OFFICIAL ? botAccountId + ":" : "")
                + conversationId;
    }
}
