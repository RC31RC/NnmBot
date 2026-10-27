package com.nnm.bot.admin.pojo.vo.bot.scope;

import com.nnm.bot.core.constant.BotPlatform;

/** 平台作用域管理列表项。 */
public record BotScopeVO(
        /** 作用域主键。 */
        long id,
        /** 所属平台。 */
        BotPlatform platform,
        /** 作用域类型。 */
        String scopeType,
        /** 平台侧作用域标识。 */
        String scopeKey,
        /** 绑定的机器人实例主键。 */
        long botInstanceId,
        /** 机器人实例编码。 */
        String instanceId,
        /** 作用域显示名称。 */
        String displayName,
        /** 是否启用。 */
        boolean enabled,
        /** 成员数量。 */
        long memberCount) {
}
