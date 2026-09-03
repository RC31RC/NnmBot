package com.nnm.bot.admin.dto.bot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** 修改群组成员基础配置的请求，不包含积分余额。 */
public record BotGroupMemberUpdateRequest(
        /** 成员显示名称。 */
        String displayName,
        /** 群组成员角色。 */
        @NotBlank String memberRole,
        /** 是否启用成员。 */
        @NotNull Boolean enabled) {
}
