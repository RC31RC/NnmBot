package com.nnm.bot.core.channel.messaging.model;

import java.time.Instant;
import java.util.UUID;

/**
 * OneBot 群组发现请求的跨实例消息载荷。
 *
 * <p>消息只携带机器人账号和过期时间，不携带凭据，也不传递可执行回调。</p>
 */
public record OneBotScopeDiscoveryRequest(UUID requestId, String accountId, Instant expiresAt) {
}
