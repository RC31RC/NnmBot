package com.nnm.bot.warframe.client.exchange;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/** Warframe Market v2 接口的 HTTP 协议定义。 */
@HttpExchange(accept = MediaType.APPLICATION_JSON_VALUE)
public interface WarframeMarketV2HttpExchange {

    @GetExchange("/orders/item/{slug}")
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

    @GetExchange("/items/{slug}")
    ResponseEntity<String> fetchItem(
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

    @GetExchange("/items/{slug}/set")
    ResponseEntity<String> fetchItemSet(
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

    @GetExchange("/items")
    ResponseEntity<String> fetchItems(
            @RequestHeader("Platform")
            String platform,
            @RequestHeader("Language")
            String language,
            @RequestHeader("User-Agent")
            String userAgent,
            @RequestHeader(value = "Authorization", required = false)
            String authorization);

    @GetExchange("/{type}/weapons")
    ResponseEntity<String> fetchAuctionWeapons(
            @PathVariable("type")
            String type,
            @RequestHeader("Platform")
            String platform,
            @RequestHeader("Language")
            String language,
            @RequestHeader("User-Agent")
            String userAgent,
            @RequestHeader(value = "Authorization", required = false)
            String authorization);
}
