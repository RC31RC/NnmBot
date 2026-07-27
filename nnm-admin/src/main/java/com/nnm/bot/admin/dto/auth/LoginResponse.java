package com.nnm.bot.admin.dto.auth;

public record LoginResponse(
        String token,
        String refreshToken
) {
}
