package com.nnm.bot.warframe.runtime.client.worldstate.exchange;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/** browse.wf 入侵任务类型补充数据的 HTTP 协议定义。 */
@HttpExchange(accept = MediaType.APPLICATION_JSON_VALUE)
public interface WarframeInvasionHttpExchange {

    /**
     * 获取当前入侵双方任务类型。
     *
     * @return 上游 HTTP 响应。
     */
    @GetExchange
    ResponseEntity<String> fetch();
}
