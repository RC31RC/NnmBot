package com.nnm.bot.warframe.client.exchange;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/** Warframe Market v1 接口的 HTTP 协议定义。 */
@HttpExchange(accept = MediaType.APPLICATION_JSON_VALUE)
public interface WarframeMarketV1HttpExchange {

    @GetExchange("/items/{slug}/orders")
    ResponseEntity<String> fetchOrders(
            @PathVariable("slug")
            String slug,
            @RequestHeader("Platform")
            String platform,
            @RequestHeader("Language")
            String language,
            @RequestHeader("User-Agent")
            String userAgent,
            @RequestHeader(value = "Authorization", required = false)
            String authorization);

    @GetExchange("/items/{slug}/statistics")
    ResponseEntity<String> fetchStatistics(
            @PathVariable("slug")
            String slug,
            @RequestHeader("Platform")
            String platform,
            @RequestHeader("Language")
            String language,
            @RequestHeader("User-Agent")
            String userAgent,
            @RequestHeader(value = "Authorization", required = false)
            String authorization);

    @GetExchange("/auctions/search")
    ResponseEntity<String> searchAuctions(
            @RequestParam("type")
            String type,
            @RequestParam("weapon_url_name")
            String weaponUrlName,
            @RequestParam("polarity")
            String polarity,
            @RequestParam("buyout_policy")
            String buyoutPolicy,
            @RequestParam("sort_by")
            String sortBy,
            @RequestParam(value = "positive_stats", required = false)
            String positiveStats,
            @RequestParam(value = "negative_stats", required = false)
            String negativeStats,
            @RequestHeader("Platform")
            String platform,
            @RequestHeader("Language")
            String language,
            @RequestHeader("User-Agent")
            String userAgent,
            @RequestHeader(value = "Authorization", required = false)
            String authorization);
}
