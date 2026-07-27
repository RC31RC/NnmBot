package com.nnm.bot.admin.dto.auth;

import java.util.List;

public record UserInfoResponse(
        List<String> buttons,
        List<String> roles,
        Long userId,
        String userName,
        String email,
        String avatar
) {
}
