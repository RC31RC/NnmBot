package com.nnm.bot.warframe.runtime.client.worldstate.exchange;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/** browse.wf 赏金轮换补充数据的 HTTP 协议定义。 */
@HttpExchange(accept = MediaType.APPLICATION_JSON_VALUE)
public interface WarframeBountyCycleHttpExchange {

    /**
     * 获取当前赏金轮换 JSON。
     *
     * @return 上游 HTTP 响应。
     */
    @GetExchange
    ResponseEntity<String> fetch();
}
