package com.nnm.bot.admin.pojo.dto.bot.group;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BotGroupBotUpdateDTO(
        @NotNull
        Boolean enabled,
        @Min(0)
        @Max(10000)
        int replyPriority,
        String confirmationToken) {
    public BotGroupBotUpdateDTO(Boolean enabled, int replyPriority) {
        this(enabled, replyPriority, null);
    }
}
