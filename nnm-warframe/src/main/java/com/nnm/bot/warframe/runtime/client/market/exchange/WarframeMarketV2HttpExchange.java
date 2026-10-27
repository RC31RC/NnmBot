package com.nnm.bot.warframe.runtime.client.market.exchange;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/** Warframe Market v2 接口的 HTTP 协议定义。 */
@HttpExchange(accept = MediaType.APPLICATION_JSON_VALUE)
public interface WarframeMarketV2HttpExchange {

    /**
     * 取得指定物品和平台的市场订单快照。
     *
     * @param slug 市场物品的地址标识。
     * @param platform 市场或官方接口使用的平台代码。
     * @param language 本地化语言代码。
     * @param userAgent 用户代理人物。
     * @param authorization 授权。
     * @return 响应实体对象。
     */
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

    /**
     * 取得市场物品的完整目录详情。
     *
     * @param slug 市场物品的地址标识。
     * @param platform 市场或官方接口使用的平台代码。
     * @param language 本地化语言代码。
     * @param userAgent 用户代理人物。
     * @param authorization 授权。
     * @return 响应实体对象。
     */
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

    /**
     * 取得市场套装及其部件详情。
     *
     * @param slug 市场物品的地址标识。
     * @param platform 市场或官方接口使用的平台代码。
     * @param language 本地化语言代码。
     * @param userAgent 用户代理人物。
     * @param authorization 授权。
     * @return 响应实体对象。
     */
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

    /**
     * 读取市场物品目录。
     *
     * @param platform 市场或官方接口使用的平台代码。
     * @param language 本地化语言代码。
     * @param userAgent 用户代理人物。
     * @param authorization 授权。
     * @return 响应实体对象。
     */
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

    /**
     * 读取指定拍卖类别可选的武器目录。
     *
     * @param type 目标业务类型。
     * @param platform 市场或官方接口使用的平台代码。
     * @param language 本地化语言代码。
     * @param userAgent 用户代理人物。
     * @param authorization 授权。
     * @return 响应实体对象。
     */
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
