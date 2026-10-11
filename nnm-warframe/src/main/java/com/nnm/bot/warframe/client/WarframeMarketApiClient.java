package com.nnm.bot.warframe.client;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.warframe.client.exchange.WarframeMarketV1HttpExchange;
import com.nnm.bot.warframe.client.exchange.WarframeMarketV2HttpExchange;
import com.nnm.bot.warframe.config.properties.WarframeMarketApiProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Warframe Market HTTP 客户端。
 *
 * <p>v1 和 v2 是同一站点的两套协议，不应与 Warframe 官方 API 共用客户端。
 * 具体响应 JSON 的解析由应用层 Market Service 负责。</p>
 */
@Slf4j
@Component
public class WarframeMarketApiClient {

    @Resource
    private HttpClient httpClient;
    @Resource
    private WarframeMarketV1HttpExchange v1HttpExchange;
    @Resource
    private WarframeMarketV2HttpExchange v2HttpExchange;
    @Resource
    private WarframeMarketApiProperties properties;

    @PostConstruct
    void validateProperties() {
        properties.validate();
    }

    public HttpClient.HttpResult fetchMarketOrdersResult(String itemUrlName) {
        return fetchMarketOrdersResult(itemUrlName, properties.getPlatform());
    }

    public HttpClient.HttpResult fetchMarketOrdersResult(String itemUrlName, String platform) {
        String requestPlatform = StrUtil.isBlank(platform)
                ? properties.getPlatform()
                : platform.trim().toLowerCase(Locale.ROOT);
        boolean v2 = "v2".equalsIgnoreCase(properties.getVersion());
        String path = v2
                ? "/orders/item/" + itemUrlName
                : "/items/" + itemUrlName + "/orders";
        if (v2) {
            return execute(
                    buildUrl(properties.getV2BaseUrl(), path),
                    "v2",
                    itemUrlName,
                    () -> v2HttpExchange.fetchOrders(
                            itemUrlName,
                            requestPlatform,
                            properties.getLanguage(),
                            properties.getUserAgent(),
                            authorization()));
        }
        return execute(
                buildUrl(properties.getV1BaseUrl(), path),
                "v1",
                itemUrlName,
                () -> v1HttpExchange.fetchOrders(
                        itemUrlName,
                        requestPlatform,
                        properties.getLanguage(),
                        properties.getUserAgent(),
                        authorization()));
    }

    /**
     * 目录接口固定使用 v2，因为完整物品模型和目录集合只在新协议中定义。
     */
    public HttpClient.HttpResult fetchMarketItemResult(String slug) {
        return executeV2(
                "/items/" + slug,
                "item " + slug,
                () -> v2HttpExchange.fetchItem(
                        slug,
                        properties.getPlatform(),
                        properties.getLanguage(),
                        properties.getUserAgent(),
                        authorization()));
    }

    public HttpClient.HttpResult fetchMarketItemSetResult(String slug) {
        return executeV2(
                "/items/" + slug + "/set",
                "item set " + slug,
                () -> v2HttpExchange.fetchItemSet(
                        slug,
                        properties.getPlatform(),
                        properties.getLanguage(),
                        properties.getUserAgent(),
                        authorization()));
    }

    public HttpClient.HttpResult fetchMarketItemsResult() {
        return executeV2(
                "/items",
                "items",
                () -> v2HttpExchange.fetchItems(
                        properties.getPlatform(),
                        properties.getLanguage(),
                        properties.getUserAgent(),
                        authorization()));
    }

    /** 与参考项目一致，按拍卖类型读取独立目录，不从物品名称猜测武器 slug。 */
    public HttpClient.HttpResult fetchAuctionWeaponsResult(String type) {
        if (!List.of("riven", "lich", "sister").contains(type)) {
            throw new IllegalArgumentException("Unsupported auction type: " + type);
        }
        return executeV2(
                "/" + type + "/weapons",
                type + " weapons",
                () -> v2HttpExchange.fetchAuctionWeapons(
                        type,
                        properties.getPlatform(),
                        properties.getLanguage(),
                        properties.getUserAgent(),
                        authorization()));
    }

    /**
     * 统计接口目前仍由 v1 提供，和 v2 订单接口分开管理。
     */
    public HttpClient.HttpResult fetchMarketStatisticsResult(String slug) {
        return executeV1(
                "/items/" + slug + "/statistics",
                "statistics " + slug,
                () -> v1HttpExchange.fetchStatistics(
                        slug,
                        properties.getPlatform(),
                        properties.getLanguage(),
                        properties.getUserAgent(),
                        authorization()));
    }

    /**
     * 拍卖搜索接口仍由 v1 提供，紫卡和玄骸/姐妹共用该协议。
     */
    public HttpClient.HttpResult fetchAuctionSearchResult(
            String type, String weaponUrlName, List<String> positiveStats,
            List<String> negativeStats) {
        String positiveStatsValue = joinStats(positiveStats);
        String negativeStatsValue = joinStats(negativeStats);
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUri(properties.getV1BaseUrl())
                .path("/auctions/search")
                .queryParam("type", type)
                .queryParam("weapon_url_name", weaponUrlName)
                .queryParam("polarity", "any")
                .queryParam("buyout_policy", "direct")
                .queryParam("sort_by", "price_asc");
        if (positiveStatsValue != null) {
            builder.queryParam("positive_stats", positiveStatsValue);
        }
        if (negativeStatsValue != null) {
            builder.queryParam("negative_stats", negativeStatsValue);
        }
        String url = builder.build().encode().toUriString();
        return execute(
                url,
                "v1",
                "auction search " + weaponUrlName,
                () -> v1HttpExchange.searchAuctions(
                        type,
                        weaponUrlName,
                        "any",
                        "direct",
                        "price_asc",
                        positiveStatsValue,
                        negativeStatsValue,
                        properties.getPlatform(),
                        properties.getLanguage(),
                        properties.getUserAgent(),
                        authorization()));
    }

    public String configuredMarketPlatform() {
        return properties.getPlatform();
    }

    private HttpClient.HttpResult executeV2(
            String path, String description, Supplier<ResponseEntity<String>> request) {
        return execute(
                buildUrl(properties.getV2BaseUrl(), path),
                "v2",
                description,
                request);
    }

    private HttpClient.HttpResult executeV1(
            String path, String description, Supplier<ResponseEntity<String>> request) {
        return execute(
                buildUrl(properties.getV1BaseUrl(), path),
                "v1",
                description,
                request);
    }

    private HttpClient.HttpResult execute(
            String url,
            String version,
            String description,
            Supplier<ResponseEntity<String>> request) {
        HttpClient.HttpResult result = httpClient.executeGet(url, request);
        if (result != null && result.isTooManyRequests()) {
            log.warn("Warframe.market rate limited ({}): {}", version, description);
        } else if (result != null && !result.isSuccess() && result.statusCode() != 404) {
            log.warn("Failed to fetch Warframe Market {} via {}: {}",
                    description, version, result.statusCode());
        }
        return result;
    }

    private String authorization() {
        return StrUtil.isBlank(properties.getApiKey())
                ? null
                : "Bearer " + properties.getApiKey().trim();
    }

    private static String joinStats(List<String> stats) {
        return stats == null || stats.isEmpty() ? null : String.join(",", stats);
    }

    private static String buildUrl(URI baseUrl, String path) {
        return UriComponentsBuilder.fromUri(baseUrl)
                .path(path)
                .build()
                .encode()
                .toUriString();
    }
}
