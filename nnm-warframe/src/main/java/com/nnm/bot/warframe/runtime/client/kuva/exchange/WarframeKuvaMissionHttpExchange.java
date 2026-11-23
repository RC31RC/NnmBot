package com.nnm.bot.warframe.runtime.client.kuva.exchange;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/** WFCD 赤毒任务流的 HTTP 协议定义。 */
@HttpExchange(accept = MediaType.APPLICATION_JSON_VALUE)
public interface WarframeKuvaMissionHttpExchange {

    /**
     * 获取当前赤毒任务流文本。
     *
     * @return 上游 HTTP 响应。
     */
    @GetExchange
    ResponseEntity<String> fetch();
}
