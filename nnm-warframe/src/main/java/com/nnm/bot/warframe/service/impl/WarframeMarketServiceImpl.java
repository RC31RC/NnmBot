package com.nnm.bot.warframe.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.http.HttpClient;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.warframe.application.WarframeMarketService;
import com.nnm.bot.warframe.cache.WarframeCacheNames;
import com.nnm.bot.warframe.client.WarframeMarketApiClient;
import com.nnm.bot.warframe.config.properties.WarframeMarketApiProperties;
import com.nnm.bot.warframe.pojo.*;
import com.nnm.bot.warframe.parser.WarframeMarketItemParser;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Supplier;

/**
 * Warframe Market 统一业务服务：接收查询对象，完成缓存、响应解析、筛选和分页。
 * HTTP 地址及请求头仅由 ApiClient 管理，命令语法和摘要仅由 Plugin 处理。
 */
@Slf4j
@Service
public class WarframeMarketServiceImpl implements WarframeMarketService {
    private static final int AUCTION_PAGE_SIZE = 10;

    @Resource
    private WarframeMarketApiClient apiClient;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private WarframeMarketItemParser itemParser;
    @Resource
    private CacheManager cacheManager;
    @Resource
    private WarframeMarketApiProperties properties;

    /** 订单 API 返回全量挂单，分页必须在可见性、在线状态及等级筛选之后执行。 */
    @Override
    public Optional<WarframeMarketOrderResult> queryOrders(WarframeMarketOrderQuery query) {
        Objects.requireNonNull(query, "query");
        return fetchOrders(query.marketSlug(), query.platform()).map(snapshot -> {
            WarframeMarketItem details = snapshot.item();
            try {
                details = fetchItem(query.marketSlug()).orElse(details);
            } catch (RuntimeException exception) {
                // 目录是增强数据，详情接口故障不应阻断已有订单。
                log.warn("Unable to load market details for [{}]", query.marketSlug(), exception);
            }
            WarframeMarketItem item = details;
            List<WarframeMarketItemView> setItems = List.of();
            if (Boolean.TRUE.equals(item.setRoot()) || !item.setParts().isEmpty()) {
                try {
                    setItems = fetchItemSet(query.marketSlug())
                            .map(set -> set.items().stream().map(WarframeMarketItemView::from).toList())
                            .orElseGet(List::of);
                } catch (RuntimeException exception) {
                    // 部件详情是增强数据；上游故障时仍返回已获得的有效订单。
                    log.warn("Unable to load market set for [{}]", query.marketSlug(), exception);
                }
            }
            Integer maxRank = item.maxRank() == null ? snapshot.maxRank() : item.maxRank();
            List<WarframeMarketOrder> orders = snapshot.orders().stream()
                    .filter(order -> query.filter().accepts(order.orderType()))
                    .filter(WarframeMarketOrder::visible)
                    .filter(order -> !"offline".equalsIgnoreCase(order.status()))
                    .filter(order -> matchesMaxRank(query.maxLevel(), maxRank, item, order))
                    .sorted(Comparator.comparing((WarframeMarketOrder order) -> !"sell".equals(order.orderType()))
                            .thenComparingLong(order -> "sell".equals(order.orderType())
                                    ? order.platinum() : -(long) order.platinum())
                            .thenComparing(WarframeMarketOrder::userName))
                    .toList();
            PageResult<WarframeMarketOrderView> page = page(
                    orders.stream().map(WarframeMarketOrderView::from).toList(), query.page(),
                    properties.getOrderPageSize());
            String name = item.localizedName("zh-hans");
            if (name.isBlank()) name = snapshot.itemName();
            if (name.isBlank()) name = query.itemInput();
            return new WarframeMarketOrderResult(name, query.itemInput(), query.marketSlug(),
                    snapshot.platform(), query.filter().name(), query.maxLevel(), maxRank, page,
                    price(orders, "sell"), price(orders, "buy"),
                    WarframeMarketItemView.from(item), setItems);
        });
    }

    /** 正负词条交给上游过滤，本地只处理展示排序与分页。 */
    @Override
    public Optional<WarframeMarketAuctionResult> queryAuctions(WarframeMarketAuctionQuery query) {
        Objects.requireNonNull(query, "query");
        List<String> negative = query.requireNegative() && query.negativeStats().isEmpty()
                ? List.of("has") : query.negativeStats();
        return searchAuctions(query.type(), query.weaponSlug(), query.positiveStats(), negative)
                .map(snapshot -> {
                    List<WarframeMarketAuctionView> auctions = snapshot.auctions().stream()
                            .map(value -> parseAuction(objectMapper.valueToTree(value)))
                            // “负任意”不等于忽略条件，须在分页前筛出确实带负词条的紫卡。
                            .filter(auction -> !query.requireNegative()
                                    || auction.item().attributes().stream().anyMatch(attribute -> !attribute.positive()))
                            .sorted(Comparator.comparing(
                                    WarframeMarketServiceImpl::auctionPrice,
                                    Comparator.nullsLast(Integer::compareTo))
                                    .thenComparing(WarframeMarketAuctionView::id))
                            .toList();
                    return new WarframeMarketAuctionResult(query.type(), query.weaponName(),
                            query.weaponSlug(), query.positiveStats(), query.negativeStats(),
                            page(auctions, query.page()), query.requireNegative());
                });
    }

    @Override
    public Optional<WarframeMarketOrderSnapshot> fetchOrders(String slug, String platform) {
        if (blank(slug)) return Optional.empty();
        String normalizedSlug = norm(slug);
        String normalizedPlatform = blank(platform) ? norm(apiClient.configuredMarketPlatform()) : norm(platform);
        return cached(WarframeCacheNames.MARKET_ORDERS,
                List.of("orders", normalizedSlug, normalizedPlatform),
                () -> loadOrders(normalizedSlug, normalizedPlatform));
    }

    private Optional<WarframeMarketOrderSnapshot> loadOrders(String slug, String platform) {
        HttpClient.HttpResult response = apiClient.fetchMarketOrdersResult(slug, platform);
        if (!success(response)) return Optional.empty();
        try {
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode payload = root.path("payload");
            JsonNode item = payload.path("item");
            if (!item.isObject()) item = root.path("item");
            if (!item.isObject() && root.path("data").isObject()) item = root.path("data").path("item");
            JsonNode values = payload.path("orders");
            if (!values.isArray()) values = root.path("data");
            if (!values.isArray()) {
                throw new IllegalArgumentException("Market orders response must contain an orders array");
            }
            List<WarframeMarketOrder> orders = new ArrayList<>();
            if (values.isArray()) for (JsonNode order : values) orders.add(parseOrder(order));
            WarframeMarketItem details = itemParser.parse(item);
            String name = details.localizedName("zh-hans");
            if (name.isBlank()) name = details.localizedName("en");
            return Optional.of(new WarframeMarketOrderSnapshot(name, details.maxRank(),
                    blank(platform) ? apiClient.configuredMarketPlatform() : platform,
                    orders, response.body(), details, text(root, "apiVersion"), error(root)));
        } catch (Exception ex) {
            throw new WarframeMarketDataException("Failed to parse Warframe Market orders for " + slug, ex);
        }
    }

    @Override
    public Optional<WarframeMarketItem> fetchItem(String slug) {
        if (blank(slug)) return Optional.empty();
        return cached(WarframeCacheNames.METADATA, List.of("market:item", norm(slug)),
                () -> loadItem(norm(slug)));
    }

    private Optional<WarframeMarketItem> loadItem(String slug) {
        HttpClient.HttpResult response = apiClient.fetchMarketItemResult(slug.trim());
        if (!success(response)) return Optional.empty();
        try {
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode data = root.path("data");
            if (!data.isObject()) data = root.path("payload").path("item");
            return data.isObject() ? Optional.of(itemParser.parse(data)) : Optional.empty();
        } catch (Exception ex) {
            throw new WarframeMarketDataException("Failed to parse Warframe Market item for " + slug, ex);
        }
    }

    @Override
    public Optional<WarframeMarketItemSet> fetchItemSet(String slug) {
        if (blank(slug)) return Optional.empty();
        return cached(WarframeCacheNames.METADATA, List.of("market:set", norm(slug)),
                () -> loadItemSet(norm(slug)));
    }

    private Optional<WarframeMarketItemSet> loadItemSet(String slug) {
        HttpClient.HttpResult response = apiClient.fetchMarketItemSetResult(slug.trim());
        if (!success(response)) return Optional.empty();
        try {
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode data = root.path("data");
            if (!data.isObject() || !data.path("items").isArray()) return Optional.empty();
            List<WarframeMarketItem> items = new ArrayList<>();
            data.path("items").forEach(v -> items.add(itemParser.parse(v)));
            return Optional.of(new WarframeMarketItemSet(data.path("id").asText(""), items,
                    root.path("apiVersion").asText(""), error(root), response.body()));
        } catch (Exception ex) {
            throw new WarframeMarketDataException("Failed to parse Warframe Market item set for " + slug, ex);
        }
    }

    @Override
    public List<WarframeMarketItem> fetchItems() {
        return cached(WarframeCacheNames.METADATA, "market:items", () -> {
            List<WarframeMarketItem> items = loadItems();
            return items.isEmpty() ? Optional.empty() : Optional.of(items);
        }).orElseGet(List::of);
    }

    private List<WarframeMarketItem> loadItems() {
        HttpClient.HttpResult response = apiClient.fetchMarketItemsResult();
        return parseCatalog(response);
    }

    @Override
    public List<WarframeMarketItem> fetchAuctionWeapons(String type) {
        String normalized = norm(type);
        if (!List.of("riven", "lich", "sister").contains(normalized)) {
            throw new IllegalArgumentException("Unsupported auction type: " + type);
        }
        return cached(WarframeCacheNames.METADATA, List.of("market:auction-weapons", normalized), () -> {
            List<WarframeMarketItem> items = parseCatalog(apiClient.fetchAuctionWeaponsResult(normalized));
            return items.isEmpty() ? Optional.empty() : Optional.of(items);
        }).orElseGet(List::of);
    }

    /** 目录与拍卖武器均使用 v2 的 slug/i18n 结构，但缓存按用途和类型隔离。 */
    private List<WarframeMarketItem> parseCatalog(HttpClient.HttpResult response) {
        if (!success(response)) return List.of();
        try {
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode data = root.path("data");
            if (!data.isArray()) data = root.path("payload").path("items");
            if (!data.isArray()) return List.of();
            List<WarframeMarketItem> result = new ArrayList<>();
            data.forEach(v -> result.add(itemParser.parse(v)));
            return List.copyOf(result);
        } catch (Exception ex) {
            throw new WarframeMarketDataException("Failed to parse Warframe Market item catalog", ex);
        }
    }

    @Override
    public Optional<WarframeMarketAuctionSnapshot> searchAuctions(
            String type, String weapon, List<String> positive, List<String> negative) {
        if (blank(weapon)) return Optional.empty();
        String t = norm(type), w = norm(weapon);
        List<String> p = normalize(positive), n = normalize(negative);
        return cached(WarframeCacheNames.MARKET_AUCTIONS, List.of("auction", t, w, p, n),
                () -> loadAuctions(t, w, p, n));
    }

    private Optional<WarframeMarketAuctionSnapshot> loadAuctions(
            String t, String w, List<String> p, List<String> n) {
        HttpClient.HttpResult response = apiClient.fetchAuctionSearchResult(t, w, p, n);
        if (!success(response)) return Optional.empty();
        try {
            JsonNode values = objectMapper.readTree(response.body()).path("payload").path("auctions");
            if (!values.isArray()) {
                throw new IllegalArgumentException("Market auction response must contain an auctions array");
            }
            List<Map<String, Object>> auctions = values.isArray()
                    ? objectMapper.convertValue(values, new TypeReference<>() {}) : List.of();
            auctions = auctions.stream().filter(WarframeMarketServiceImpl::online).toList();
            return Optional.of(new WarframeMarketAuctionSnapshot(t, w, auctions, p, n, response.body()));
        } catch (Exception ex) {
            throw new WarframeMarketDataException("Failed to parse Warframe Market auctions for " + w, ex);
        }
    }

    @Override
    public Optional<WarframeMarketStatistics> fetchStatistics(String slug) {
        if (blank(slug)) return Optional.empty();
        return cached(WarframeCacheNames.MARKET_STATISTICS, List.of("statistics", norm(slug)),
                () -> loadStatistics(norm(slug)));
    }

    private Optional<WarframeMarketStatistics> loadStatistics(String slug) {
        HttpClient.HttpResult response = apiClient.fetchMarketStatisticsResult(slug.trim());
        if (!success(response)) return Optional.empty();
        try {
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode values = root.path("payload").path("statistics_live").path("90days");
            if (!values.isArray()) values = root.path("data").path("statistics");
            List<Map<String, Object>> entries = values.isArray()
                    ? objectMapper.convertValue(values, new TypeReference<>() {}) : List.of();
            return Optional.of(new WarframeMarketStatistics(slug.trim(), entries, response.body()));
        } catch (Exception ex) {
            throw new WarframeMarketDataException("Failed to parse Warframe Market statistics for " + slug, ex);
        }
    }

    @Override
    public void clearCache() {
        for (String name : List.of(WarframeCacheNames.METADATA, WarframeCacheNames.MARKET_ORDERS,
                WarframeCacheNames.MARKET_AUCTIONS, WarframeCacheNames.MARKET_STATISTICS)) {
            Cache cache = cacheManager.getCache(name);
            if (cache != null) cache.clear();
        }
    }

    /**
     * 显式缓存避免合并服务后内部调用绕过 Spring AOP，也不再依赖 SpEL 参数名。
     * 使用缓存自身的同键加载协调；请求失败不缓存，真实的空订单快照仍可缓存。
     */
    private <T> Optional<T> cached(String cacheName, Object key, Supplier<Optional<T>> loader) {
        Cache cache = Objects.requireNonNull(cacheManager.getCache(cacheName), cacheName);
        try {
            return Optional.ofNullable(cache.get(key, () -> loader.get().orElseThrow(UnavailableResult::new)));
        } catch (Cache.ValueRetrievalException exception) {
            if (exception.getCause() instanceof UnavailableResult) return Optional.empty();
            if (exception.getCause() instanceof RuntimeException cause) throw cause;
            throw exception;
        }
    }

    private static final class UnavailableResult extends RuntimeException {}

    private static <T> PageResult<T> page(List<T> values, int page) {
        return page(values, page, AUCTION_PAGE_SIZE);
    }

    private static <T> PageResult<T> page(List<T> values, int page, int size) {
        // 先以 long 运算，避免用户输入超大页码导致负数下标。
        int from = (int) Math.min(((long) page - 1) * size, values.size());
        return PageResult.of(values.subList(from, Math.min(from + size, values.size())), page, size, values.size());
    }

    private static String price(List<WarframeMarketOrder> orders, String type) {
        return orders.stream().filter(order -> type.equals(order.orderType())).findFirst()
                .map(order -> order.platinum() + " 白金").orElse("");
    }

    private static boolean matchesMaxRank(boolean requested, Integer maxRank,
                                          WarframeMarketItem item, WarframeMarketOrder order) {
        if (!requested) return true;
        if (maxRank != null && !maxRank.equals(order.rank())) return false;
        // 阿耶檀识塑像的满级是两种星星分别装满，不可将星星数量与 Mod 等级比较。
        return (item.maxAmberStars() == null || item.maxAmberStars().equals(order.amberStars()))
                && (item.maxCyanStars() == null || item.maxCyanStars().equals(order.cyanStars()));
    }

    private WarframeMarketAuctionView parseAuction(JsonNode value) {
        JsonNode owner = value.path("owner"), item = value.path("item");
        List<WarframeMarketAuctionView.Attribute> attributes = new ArrayList<>();
        for (JsonNode attribute : item.path("attributes")) {
            attributes.add(new WarframeMarketAuctionView.Attribute(
                    text(attribute, "url_name"), attribute.path("value").asDouble(), bool(attribute, "positive")));
        }
        String status = text(owner, "status");
        String statusText = switch (status.toLowerCase(Locale.ROOT)) {
            case "online" -> "在线";
            case "ingame" -> "游戏中";
            case "offline" -> "离线";
            default -> status;
        };
        return new WarframeMarketAuctionView(text(value, "id"), text(owner, "ingame_name"), status, statusText,
                integerOrNull(value, "starting_price"), integerOrNull(value, "buyout_price"),
                integerOrNull(value, "top_bid"), bool(value, "is_direct_sell"), text(value, "created"),
                new WarframeMarketAuctionView.AuctionItem(text(item, "type"), text(item, "weapon_url_name"),
                        text(item, "name"), integerOrNull(item, "mod_rank"), integerOrNull(item, "re_rolls"),
                        integerOrNull(item, "mastery_level"), text(item, "polarity"), text(item, "element"),
                        integerOrNull(item, "damage"), item.has("having_ephemera") ? bool(item, "having_ephemera") : null,
                        text(item, "quirk"), attributes));
    }

    private static Integer auctionPrice(WarframeMarketAuctionView auction) {
        return auction.buyoutPrice() == null ? auction.startingPrice() : auction.buyoutPrice();
    }

    private WarframeMarketOrder parseOrder(JsonNode o) {
        boolean v2 = o.has("type") || o.has("rank") || o.has("modRank");
        JsonNode user = o.path("user");
        return new WarframeMarketOrder(text(o, "id", "_id"),
                text(o, v2 ? "type" : "order_type").toLowerCase(Locale.ROOT),
                integer(o, "platinum"), integer(o, "quantity"),
                integerOrNull(o, v2 ? "perTrade" : "per_trade"), text(o, "subtype"),
                integerOrNull(o, "rank", "modRank", "mod_rank"), integerOrNull(o, "charges"),
                integerOrNull(o, v2 ? "amberStars" : "amber_stars"),
                integerOrNull(o, v2 ? "cyanStars" : "cyan_stars"), !o.has("visible") || bool(o, "visible"),
                text(o, "createdAt", "created_at"), text(o, "updatedAt", "updated_at"),
                text(o, "itemId", "item_id"), text(o, "groupId", "group_id"),
                new WarframeMarketUser(text(user, "id", "_id"),
                        text(user, v2 ? "ingameName" : "ingame_name", "name"),
                        text(user, "slug"), integerOrNull(user, "reputation"), text(user, "platform"),
                        bool(user, "crossplay"), text(user, "locale"), text(user, "status"),
                        text(user, "avatar"), text(user, "lastSeen", "last_seen"),
                        rawObject(user, "activity"), raw(user)), raw(o));
    }

    private Map<String, Object> raw(JsonNode node) {
        return node == null || !node.isObject() ? Map.of() : objectMapper.convertValue(node, new TypeReference<>() {});
    }
    private Map<String, Object> rawObject(JsonNode node, String name) {
        return raw(node == null ? null : node.get(name));
    }
    private static boolean online(Map<String, Object> value) {
        Object owner = value.get("owner");
        return !(owner instanceof Map<?, ?> map) || !"offline".equalsIgnoreCase(String.valueOf(map.get("status")));
    }
    private static List<String> normalize(List<String> values) {
        return values == null ? List.of() : values.stream().filter(v -> !blank(v)).map(WarframeMarketServiceImpl::norm)
                .distinct().sorted().toList();
    }
    private static String norm(String value) { return value == null ? "" : value.trim().toLowerCase(Locale.ROOT); }
    private static boolean success(HttpClient.HttpResult r) { return r != null && r.isSuccess() && !blank(r.body()); }
    private static boolean blank(String value) { return value == null || value.isBlank(); }
    private static String text(JsonNode node, String... names) {
        for (String name : names) {
            JsonNode value = node == null ? null : node.get(name);
            if (value != null && !value.isNull()) return value.asText("");
        }
        return "";
    }
    private static int integer(JsonNode node, String name) {
        Integer value = integerOrNull(node, name); return value == null ? 0 : value;
    }
    private static Integer integerOrNull(JsonNode node, String... names) {
        for (String name : names) {
            JsonNode value = node == null ? null : node.get(name);
            if (value != null && value.isIntegralNumber()) return value.asInt();
        }
        return null;
    }
    private static boolean bool(JsonNode node, String name) {
        JsonNode value = node == null ? null : node.get(name);
        return value != null && (value.isBoolean() ? value.asBoolean() : Boolean.parseBoolean(value.asText()));
    }
    private static String error(JsonNode root) {
        JsonNode value = root.path("error");
        return value.isMissingNode() || value.isNull() ? "" : value.toString();
    }

    public static final class WarframeMarketDataException extends RuntimeException {
        public WarframeMarketDataException(String message, Throwable cause) { super(message, cause); }
    }
}
