package com.nnm.bot.admin.pojo.dto.bot.scope;

import java.util.List;

/** 发现到的平台作用域。 */
public record BotScopeDiscoveryScopeDTO(
        /** 作用域类型。 */
        String scopeType,
        /** 平台侧作用域标识。 */
        String scopeKey,
        /** 作用域显示名称。 */
        String displayName,
        /** 平台返回的成员数量。 */
        Integer memberCount,
        /** 是否已经创建为系统作用域。 */
        boolean added,
        /** 已创建的系统作用域主键。 */
        Long scopeId,
        /** 发现到的成员列表。 */
        List<BotScopeDiscoveryMemberDTO> members) {
}
