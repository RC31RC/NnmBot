package com.nnm.bot.admin.dto.bot;

import jakarta.validation.constraints.Size;

/** 探测 OneBot 当前机器人账号的请求。 */
public record OneBotAccountProbeRequest(
        /** 已保存的实例主键。 */
        Long instanceId,
        /** 临时探测使用的 WebSocket 地址。 */
        @Size(max = 500)
        String connectionUrl,
        /** 临时探测使用的访问令牌。 */
        @Size(max = 1000)
        String accessToken) {
}
