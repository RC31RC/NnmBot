package com.nnm.bot.warframe.runtime.client.steelincursion.exchange;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/** browse.wf 钢铁侵袭排期文本的 HTTP 协议定义。 */
@HttpExchange(accept = MediaType.TEXT_PLAIN_VALUE)
public interface WarframeSteelIncursionHttpExchange {

    /**
     * 获取钢铁侵袭排期文本。
     *
     * @param acceptEncoding 上游内容编码协商值。
     * @return 上游 HTTP 响应。
     */
    @GetExchange
    ResponseEntity<String> fetch(@RequestHeader("Accept-Encoding") String acceptEncoding);
}
