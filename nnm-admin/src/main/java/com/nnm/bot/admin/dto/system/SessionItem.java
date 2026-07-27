package com.nnm.bot.admin.dto.system;

import java.time.LocalDateTime;

public record SessionItem(
        String token,
        Long userId,
        String userName,
        String deviceType,
        LocalDateTime loginTime,
        long timeout
) {
}
