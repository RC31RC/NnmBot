package com.nnm.bot.admin.dto.system;

public record AuthItem(
        Long id,
        String title,
        String authMark,
        boolean enabled
) {
}
