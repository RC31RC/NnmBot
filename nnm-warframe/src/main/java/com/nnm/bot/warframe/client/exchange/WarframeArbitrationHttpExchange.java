package com.nnm.bot.warframe.client.exchange;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/** browse.wf 仲裁文本排期的 HTTP 协议定义。 */
@HttpExchange(accept = MediaType.TEXT_PLAIN_VALUE)
public interface WarframeArbitrationHttpExchange {

    /**
     * 地址和查询参数由配置提供，因此方法本身使用根地址请求。
     */
    @GetExchange
    ResponseEntity<String> fetch();
}
