package com.nnm.bot.admin.dto.bot;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record QqOfficialInstanceSaveRequest(
        @NotBlank @Size(max = 100) String instanceId,
        @NotBlank @Size(max = 100) String displayName,
        boolean enabled,
        @NotBlank @Size(max = 200) String appId,
        @Size(max = 1000) String clientSecret,
        @NotBlank @Size(max = 500)
        @Pattern(regexp = "(?i)https?://.+", message = "必须使用 http 或 https 协议")
        String tokenUrl,
        @NotBlank @Size(max = 500)
        @Pattern(regexp = "(?i)https?://.+", message = "必须使用 http 或 https 协议")
        String apiBaseUrl,
        @Size(max = 500) String gatewayUrl,
        @Min(0) long intents,
        @Min(1) @Max(120) long gatewayConnectTimeoutSeconds,
        @Min(0) @Max(3600) long reconnectDelaySeconds) {
}
