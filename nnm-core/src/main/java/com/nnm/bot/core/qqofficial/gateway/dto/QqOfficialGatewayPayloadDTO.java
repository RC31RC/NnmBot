package com.nnm.bot.core.qqofficial.gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/** QQ 官方 Gateway 的 op/d 协议封包。 */
public record QqOfficialGatewayPayloadDTO<T>(int op, T d) {
    public record IdentifyDTO(String token, long intents, int[] shard) {
    }

    public record ResumeDTO(String token, @JsonProperty("session_id") String sessionId, int seq) {
    }
}
