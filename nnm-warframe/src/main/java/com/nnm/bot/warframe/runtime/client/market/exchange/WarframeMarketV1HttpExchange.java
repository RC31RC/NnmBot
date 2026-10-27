package com.nnm.bot.warframe.runtime.client.market.exchange;

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

    /**
     * 获取杜卡德金币。
     *
     * @param platform 市场或官方接口使用的平台代码。
     * @param language 本地化语言代码。
     * @param userAgent 用户代理人物。
     * @param authorization 授权。
     * @return 响应实体对象。
     */
    @GetExchange("/tools/ducats")
    ResponseEntity<String> fetchDucats(
            @RequestHeader("Platform")
            String platform,
            @RequestHeader("Language")
            String language,
            @RequestHeader("User-Agent")
            String userAgent,
            @RequestHeader(value = "Authorization", required = false)
            String authorization);

    /**
     * 取得市场物品的成交统计。
     *
     * @param slug 市场物品的地址标识。
     * @param platform 市场或官方接口使用的平台代码。
     * @param language 本地化语言代码。
     * @param userAgent 用户代理人物。
     * @param authorization 授权。
     * @return 响应实体对象。
     */
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

    /**
     * 查询指定物品及词条条件的市场拍卖。
     *
     * @param type 目标业务类型。
     * @param weaponUrlName 武器地址名称。
     * @param polarity 极性。
     * @param buyoutPolicy 一口价策略。
     * @param sortBy 排序按。
     * @param positiveStats 正面词条统计。
     * @param negativeStats 负面词条统计。
     * @param platform 市场或官方接口使用的平台代码。
     * @param language 本地化语言代码。
     * @param userAgent 用户代理人物。
     * @param authorization 授权。
     * @return 响应实体对象。
     */
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
