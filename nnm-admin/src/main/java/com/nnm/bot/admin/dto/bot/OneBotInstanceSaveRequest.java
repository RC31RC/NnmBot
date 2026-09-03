package com.nnm.bot.admin.dto.bot;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** 保存 OneBot 机器人实例的请求。 */
public record OneBotInstanceSaveRequest(
        /** 系统内部实例编码。 */
        @NotBlank @Size(max = 100) String instanceId,
        /** 实例展示名称。 */
        @NotBlank @Size(max = 100) String displayName,
        /** 平台侧机器人 QQ 号。 */
        @NotBlank @Pattern(regexp = "[1-9]\\d*", message = "必须是正整数 QQ 号")
        @Size(max = 100) String botAccountId,
        /** 是否启用实例。 */
        boolean enabled,
        /** OneBot 正向 WebSocket 地址。 */
        @NotBlank @Size(max = 500)
        @Pattern(regexp = "(?i)wss?://.+", message = "必须使用 ws 或 wss 协议")
        String connectionUrl,
        /** OneBot 访问令牌。 */
        @Size(max = 1000) String accessToken,
        /** 断线重连间隔秒数。 */
        @Min(1) @Max(3600) long reconnectIntervalSeconds) {
}
