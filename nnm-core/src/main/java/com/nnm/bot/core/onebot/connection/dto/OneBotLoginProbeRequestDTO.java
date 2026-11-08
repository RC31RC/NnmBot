package com.nnm.bot.core.onebot.connection.dto;

/** OneBot 11 get_login_info 请求，params 按协议发送空对象。 */
public record OneBotLoginProbeRequestDTO(String action, EmptyParamsDTO params, String echo) {
    public record EmptyParamsDTO() {
    }
}
