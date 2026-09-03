package com.nnm.bot.admin.dto.bot;

/** 平台作用域成员列表项。 */
public record BotScopeMemberItem(
        /** 成员关系主键。 */
        long id,
        /** 关联系统用户主键。 */
        long userId,
        /** 平台侧成员标识。 */
        String platformUserId,
        /** 作用域成员角色。 */
        String memberRole,
        /** 是否启用。 */
        boolean enabled) {
}
