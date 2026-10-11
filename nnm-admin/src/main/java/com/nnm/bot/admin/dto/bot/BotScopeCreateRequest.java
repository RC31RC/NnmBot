package com.nnm.bot.admin.dto.bot;

import com.nnm.bot.core.constant.BotPlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** 创建平台作用域记录的请求。 */
public record BotScopeCreateRequest(
        /** 作用域所属平台。 */
        @NotNull
        BotPlatform platform,
        /** 作用域类型。 */
        @NotBlank
        String scopeType,
        /** 平台侧作用域标识。 */
        @NotBlank
        String scopeKey,
        /** 关联的机器人实例主键。 */
        @NotNull
        Long botInstanceId,
        /** 作用域显示名称。 */
        String displayName,
        /** 是否启用。 */
        @NotNull
        Boolean enabled,
        /** 是否同步平台成员。 */
        Boolean syncMembers) {
}
