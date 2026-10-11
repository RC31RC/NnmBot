package com.nnm.bot.warframe.client.exchange;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/** 官方 WorldState 接口的 HTTP 协议定义。 */
@HttpExchange(accept = MediaType.APPLICATION_JSON_VALUE)
public interface WarframeWorldStateHttpExchange {

    @GetExchange
    ResponseEntity<String> fetch(
            @RequestHeader("Accept-Language")
            String language);
}
