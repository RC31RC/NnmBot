package com.nnm.bot.admin.dto.bot;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** 保存 QQ 官方机器人实例的请求。 */
public record QqOfficialInstanceSaveRequest(
        /** 系统内部实例编码。 */
        @NotBlank @Size(max = 100) String instanceId,
        /** 实例展示名称。 */
        @NotBlank @Size(max = 100) String displayName,
        /** 是否启用实例。 */
        boolean enabled,
        /** QQ 官方机器人应用 ID。 */
        @NotBlank @Size(max = 200) String appId,
        /** QQ 官方机器人应用密钥。 */
        @Size(max = 1000) String clientSecret,
        /** 获取访问令牌的地址。 */
        @NotBlank @Size(max = 500)
        @Pattern(regexp = "(?i)https?://.+", message = "必须使用 http 或 https 协议")
        String tokenUrl,
        /** QQ 官方机器人 API 基础地址。 */
        @NotBlank @Size(max = 500)
        @Pattern(regexp = "(?i)https?://.+", message = "必须使用 http 或 https 协议")
        String apiBaseUrl,
        /** QQ 官方机器人网关地址。 */
        @Size(max = 500) String gatewayUrl,
        /** 订阅事件意图位掩码。 */
        @Min(0) long intents,
        /** 网关连接超时秒数。 */
        @Min(1) @Max(120) long gatewayConnectTimeoutSeconds,
        /** 断线重连延迟秒数。 */
        @Min(0) @Max(3600) long reconnectDelaySeconds) {
}
