package com.nnm.bot.admin.dto.bot;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record OneBotInstanceSaveRequest(
        @NotBlank @Size(max = 100) String instanceId,
        @NotBlank @Size(max = 100) String displayName,
        @NotBlank @Pattern(regexp = "[1-9]\\d*", message = "必须是正整数 QQ 号")
        @Size(max = 100) String botAccountId,
        boolean enabled,
        @NotBlank @Size(max = 500)
        @Pattern(regexp = "(?i)wss?://.+", message = "必须使用 ws 或 wss 协议")
        String connectionUrl,
        @Size(max = 1000) String accessToken,
        @Min(1) @Max(3600) long reconnectIntervalSeconds) {
}
