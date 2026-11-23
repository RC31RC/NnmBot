package com.nnm.bot.warframe.runtime.client.exchange;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/** 配置型运行时数据端点的通用 GET 协议定义。 */
@HttpExchange
public interface WarframeRuntimeDataHttpExchange {

    /**
     * 获取指定来源的文本响应。
     *
     * @param headers 本次请求的协议头。
     * @return 上游 HTTP 响应。
     */
    @GetExchange
    ResponseEntity<String> fetch(@RequestHeader Map<String, String> headers);
}
