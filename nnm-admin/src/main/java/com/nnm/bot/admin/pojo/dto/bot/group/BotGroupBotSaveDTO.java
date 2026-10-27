package com.nnm.bot.admin.pojo.dto.bot.group;

import com.nnm.bot.core.constant.BotPlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** 保存逻辑群组与机器人实例绑定的请求。 */
public record BotGroupBotSaveDTO(
        /** 机器人实例主键。 */
        @NotNull
        Long botInstanceId,
        /** 机器人所属平台。 */
        @NotNull
        BotPlatform platform,
        /** 平台作用域类型。 */
        @NotBlank
        String scopeType,
        /** 平台侧作用域标识。 */
        @NotBlank
        String scopeKey,
        /** 作用域显示名称。 */
        String displayName,
        /** 是否启用绑定。 */
        @NotNull
        Boolean enabled,
        /** 是否同步该平台返回的成员。 */
        Boolean syncMembers,
        /** 对当前跨节点迁移方案的确认标识。 */
        String confirmationToken) {
    public BotGroupBotSaveDTO(Long botInstanceId, BotPlatform platform, String scopeType, String scopeKey,
                                  String displayName, Boolean enabled, Boolean syncMembers) {
        this(botInstanceId, platform, scopeType, scopeKey, displayName, enabled, syncMembers, null);
    }
}
