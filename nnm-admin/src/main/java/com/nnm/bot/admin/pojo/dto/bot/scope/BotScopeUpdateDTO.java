package com.nnm.bot.admin.pojo.dto.bot.scope;

import jakarta.validation.constraints.NotNull;

/** 修改平台作用域的请求。 */
public record BotScopeUpdateDTO(
        /** 作用域显示名称。 */
        String displayName,
        /** 是否启用。 */
        @NotNull
        Boolean enabled) {
}
