package com.nnm.bot.core.qqofficial.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** QQ 官方 Token 接口的固定响应合同。 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record QqOfficialTokenDTO(@JsonProperty("access_token") String accessToken,
    @JsonProperty("expires_in") Long expiresIn) {
}
