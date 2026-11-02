package com.nnm.bot.admin.pojo.dto.system.notification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

/** 超级管理员发布的公告及该条公告自己的到期时间。 */
public record AdminNoticePublishDTO(@NotBlank @Size(max = 200) String title, @NotBlank @Size(max = 4000) String content,
    @NotNull Instant expiresAt) {
}
