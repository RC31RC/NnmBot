package com.nnm.bot.admin.dto.bot;

import com.nnm.bot.core.constant.ChannelOutputMode;
import jakarta.validation.constraints.NotNull;

/** 保存命令渠道输出方式的请求。 */
public record BotCommandChannelOutputSaveRequest(
        /** 要使用的输出方式。 */
        @NotNull ChannelOutputMode outputMode) {
}
