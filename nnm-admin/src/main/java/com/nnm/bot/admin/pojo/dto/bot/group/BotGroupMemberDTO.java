package com.nnm.bot.admin.pojo.dto.bot.group;

import java.time.LocalDateTime;

/** 逻辑群组成员展示对象。 */
public record BotGroupMemberDTO(
        /** 成员主键。 */
        Long id,
        /** OneBot 使用的 QQ 号。 */
        String qqId,
        /** QQ 官方机器人使用的群成员 OpenID。 */
        String memberOpenid,
        /** KOOK 使用的用户 ID。 */
        String kookId,
        /** QQ 号是否已通过可信渠道核验。 */
        boolean qqIdVerified,
        /** QQ 号最近一次绑定时间。 */
        LocalDateTime qqBoundAt,
        /** 成员显示名称。 */
        String displayName,
        /** 群组成员角色。 */
        String memberRole,
        /** 是否启用。 */
        boolean enabled,
        /** 当前积分余额。 */
        long points,
        /** 累计获得积分。 */
        long totalEarned,
        /** 累计消耗积分。 */
        long totalSpent) {
}
