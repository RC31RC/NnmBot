package com.nnm.bot.admin.pojo.dto.bot.scope;

/** 发现到的平台成员。 */
public record BotScopeDiscoveryMemberDTO(
        /** 平台侧成员标识。 */
        String platformUserId,
        /** 成员显示名称。 */
        String displayName,
        /** 平台侧成员角色。 */
        String memberRole) {
}
