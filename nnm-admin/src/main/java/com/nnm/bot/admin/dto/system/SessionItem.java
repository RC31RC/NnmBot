package com.nnm.bot.admin.dto.system;

import java.time.LocalDateTime;

/** 在线登录会话列表项。 */
public record SessionItem(
        /** 会话令牌。 */
        String token,
        /** 登录用户主键。 */
        Long userId,
        /** 登录用户名。 */
        String userName,
        /** 登录设备类型。 */
        String deviceType,
        /** 登录时间。 */
        LocalDateTime loginTime,
        /** 会话剩余有效时间。 */
        long timeout
) {
}
