package com.nnm.bot.warframe.runtime.client.worldstate.exchange;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/** 官方 WorldStateDTO 接口的 HTTP 协议定义。 */
@HttpExchange(accept = MediaType.APPLICATION_JSON_VALUE)
public interface WarframeWorldStateHttpExchange {

    /**
     * 按配置获取外部响应。
     *
     * @param language 本地化语言代码。
     * @return 响应实体对象。
     */
    @GetExchange
    ResponseEntity<String> fetch(
            @RequestHeader("Accept-Language")
            String language);
}
