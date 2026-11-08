package com.nnm.bot.core.qqofficial.gateway.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Gateway 的 d 是按 op 区分的对象、数字或布尔值；仅在分发边界映射为具体 DTO。 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record QqOfficialGatewayFrameDTO(Integer op, String t, Integer s, @JsonProperty("d") Object data) {
}
