package com.nnm.bot.admin.dto.bot;

import jakarta.validation.constraints.NotNull;

/** 修改平台作用域的请求。 */
public record BotScopeUpdateRequest(
        /** 作用域显示名称。 */
        String displayName,
        /** 是否启用。 */
        @NotNull Boolean enabled) {
}
