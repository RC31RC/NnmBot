package com.nnm.bot.core.onebot.connection.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** OneBot 11 get_login_info 结果；user_id 可由协议实现返回数字或字符串。 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OneBotLoginProbeDTO(String echo, String status, String retcode, DataDTO data) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DataDTO(@JsonProperty("user_id") String userId) {
    }
}
