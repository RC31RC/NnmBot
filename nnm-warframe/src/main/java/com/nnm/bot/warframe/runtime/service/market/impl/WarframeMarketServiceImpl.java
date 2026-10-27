package com.nnm.bot.warframe.runtime.service.market.impl;

import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketOrderPageDTO;
import com.nnm.bot.common.http.HttpClient.HttpResult;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketAuctionEntryDTO.AttributeDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.nnm.bot.warframe.runtime.rawdata.market.MarketResponseRawData;
import com.nnm.bot.warframe.runtime.rawdata.market.MarketResponseRawData.ItemRawData;
import com.nnm.bot.warframe.runtime.rawdata.market.MarketResponseRawData.OrderRawData;
import com.nnm.bot.warframe.runtime.rawdata.market.MarketResponseRawData.UserRawData;
import com.nnm.bot.warframe.runtime.rawdata.market.MarketResponseRawData.AuctionRawData;
import com.nnm.bot.warframe.runtime.rawdata.market.MarketResponseRawData.AuctionItemRawData;
import com.nnm.bot.warframe.runtime.rawdata.market.MarketResponseRawData.ItemSetRawData;
import com.nnm.bot.warframe.runtime.rawdata.market.MarketResponseRawData.StatisticsDataRawData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.warframe.constant.WarframeCacheNames;
import com.nnm.bot.warframe.config.cache.WarframeCacheReader;
import com.nnm.bot.warframe.runtime.client.market.WarframeMarketApiClient;
import com.nnm.bot.warframe.runtime.config.market.WarframeMarketApiProperties;
import com.nnm.bot.warframe.runtime.mapper.market.WarframeMarketItemMapper;
import com.nnm.bot.warframe.runtime.pojo.query.market.WarframeMarketAuctionQuery;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketAuctionPageDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketAuctionSnapshotDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketAuctionEntryDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketItemDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketItemSetDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketItemSummaryDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketOrderDTO;
import com.nnm.bot.warframe.runtime.pojo.query.market.WarframeMarketOrderQuery;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketOrderSnapshotDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketOrderEntryDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketStatisticsDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.market.MarketStatisticDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketUserDTO;
import com.nnm.bot.warframe.runtime.service.market.WarframeMarketService;
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
    private WarframeMarketItemMapper itemParser;
    @Resource
    private WarframeCacheReader cacheReader;
    @Resource
    private CacheManager cacheManager;
    @Resource
    private WarframeMarketApiProperties properties;

    /**
     * 订单 API 返回全量挂单，分页必须在可见性、在线状态及等级筛选之后执行。
     *
     * @param query 当前业务的查询条件。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    @Override
    public Optional<WarframeMarketOrderPageDTO> queryOrders(WarframeMarketOrderQuery query) {
        Objects.requireNonNull(query, "query");
        return fetchOrders(query.marketSlug(), query.platform()).map(snapshot -> {
            WarframeMarketItemDTO details = snapshot.item();
            try {
                details = fetchItem(query.marketSlug()).orElse(details);
            } catch (RuntimeException exception) {
                // 目录是增强数据，详情接口故障不应阻断已有订单。
                log.warn("Unable to load market details for [{}]", query.marketSlug(), exception);
            }
            WarframeMarketItemDTO item = details;
            List<WarframeMarketItemSummaryDTO> setItems = List.of();
            if (Boolean.TRUE.equals(item.setRoot()) || CollUtil.isNotEmpty(item.setParts())) {
                try {
                    setItems = fetchItemSet(query.marketSlug())
                            .map(set -> set.items().stream().map(WarframeMarketItemSummaryDTO::from).toList())
                            .orElseGet(List::of);
                } catch (RuntimeException exception) {
                    // 部件详情是增强数据；上游故障时仍返回已获得的有效订单。
                    log.warn("Unable to load market set for [{}]", query.marketSlug(), exception);
                }
            }
            Integer maxRank = item.maxRank() == null ? snapshot.maxRank() : item.maxRank();
            List<WarframeMarketOrderDTO> orders = snapshot.orders().stream()
                    .filter(order -> query.filter().accepts(order.orderType()))
                    .filter(WarframeMarketOrderDTO::visible)
                    .filter(order -> !"offline".equalsIgnoreCase(order.status()))
                    .filter(order -> matchesMaxRank(query.maxLevel(), maxRank, item, order))
                    .sorted(Comparator.comparing((WarframeMarketOrderDTO order) -> !"sell".equals(order.orderType()))
                            .thenComparingLong(order -> "sell".equals(order.orderType())
                                    ? order.platinum() : -(long) order.platinum())
                            .thenComparing(WarframeMarketOrderDTO::userName))
                    .toList();
            PageResult<WarframeMarketOrderEntryDTO> page = page(
                    orders.stream().map(WarframeMarketOrderEntryDTO::from).toList(), query.page(),
                    properties.getOrderPageSize());
            String name = item.localizedName("zh-hans");
            if (StrUtil.isBlank(name)) name = snapshot.itemName();
            if (StrUtil.isBlank(name)) name = query.itemInput();
            return new WarframeMarketOrderPageDTO(name, query.itemInput(), query.marketSlug(),
                    snapshot.platform(), query.filter().name(), query.maxLevel(), maxRank, page,
                    price(orders, "sell"), price(orders, "buy"),
                    WarframeMarketItemSummaryDTO.from(item), setItems);
        });
    }

    /**
     * 正负词条交给上游过滤，本地只处理展示排序与分页。
     *
     * @param query 当前业务的查询条件。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    @Override
    public Optional<WarframeMarketAuctionPageDTO> queryAuctions(WarframeMarketAuctionQuery query) {
        Objects.requireNonNull(query, "query");
        List<String> negative = query.requireNegative() && CollUtil.isEmpty(query.negativeStats())
                ? List.of("has") : query.negativeStats();
        return searchAuctions(query.type(), query.weaponSlug(), query.positiveStats(), negative)
                .map(snapshot -> {
                    List<WarframeMarketAuctionEntryDTO> auctions = snapshot.auctions().stream()
                            // “负任意”不等于忽略条件，须在分页前筛出确实带负词条的紫卡。
                            .filter(auction -> !query.requireNegative()
                                    || auction.item().attributes().stream().anyMatch(attribute -> !attribute.positive()))
                            .sorted(Comparator.comparing(
                                    WarframeMarketServiceImpl::auctionPrice,
                                    Comparator.nullsLast(Integer::compareTo))
                                    .thenComparing(WarframeMarketAuctionEntryDTO::id))
                            .toList();
                    return new WarframeMarketAuctionPageDTO(query.type(), query.weaponName(),
                            query.weaponSlug(), query.positiveStats(), query.negativeStats(),
                            page(auctions, query.page()), query.requireNegative());
                });
    }

    /**
     * 取得指定物品和平台的市场订单快照。
     *
     * @param slug 市场物品的地址标识。
     * @param platform 市场或官方接口使用的平台代码。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    @Override
    public Optional<WarframeMarketOrderSnapshotDTO> fetchOrders(String slug, String platform) {
        if (blank(slug)) return Optional.empty();
        String normalizedSlug = norm(slug);
        String normalizedPlatform = blank(platform) ? norm(apiClient.configuredMarketPlatform()) : norm(platform);
        return cached(WarframeCacheNames.MARKET_ORDERS,
                List.of("orders", normalizedSlug, normalizedPlatform),
                () -> loadOrders(normalizedSlug, normalizedPlatform));
    }

    /**
     * 从市场接口读取订单并转换为非受管订单快照。
     *
     * @param slug 市场物品的地址标识。
     * @param platform 市场或官方接口使用的平台代码。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    private Optional<WarframeMarketOrderSnapshotDTO> loadOrders(String slug, String platform) {
        HttpResult response = apiClient.fetchMarketOrdersResult(slug, platform);
        if (!success(response)) return Optional.empty();
        MarketResponseRawData<List<OrderRawData>> root = readResponse(response,
                objectMapper.getTypeFactory().constructCollectionType(List.class, OrderRawData.class), slug);
        var payload = root.getPayload();
        ItemRawData item = payload == null ? root.getItem() : payload.getItem();
        List<OrderRawData> values = payload == null ? root.getData() : payload.getOrders();
        if (values == null) values = root.getData();
        if (values == null) throw new WarframeMarketDataException("市场响应缺少订单数组：" + slug, null);
        List<WarframeMarketOrderDTO> orders = values.stream().map(this::parseOrder).toList();
        WarframeMarketItemDTO details = itemParser.parse(item);
        String name = details.localizedName("zh-hans");
        if (StrUtil.isBlank(name)) name = details.localizedName("en");
        return Optional.of(new WarframeMarketOrderSnapshotDTO(name, details.maxRank(),
                blank(platform) ? apiClient.configuredMarketPlatform() : platform,
                orders, response.body(), details, root.getApiVersion(), error(root.getError())));
    }

    /**
     * 取得市场物品的完整目录详情。
     *
     * @param slug 市场物品的地址标识。
     * @return 市场物品的非受管详情；没有匹配数据时返回空结果。
     */
    @Override
    public Optional<WarframeMarketItemDTO> fetchItem(String slug) {
        if (blank(slug)) return Optional.empty();
        return cached(WarframeCacheNames.METADATA, List.of("market:item", norm(slug)),
                () -> loadItem(norm(slug)));
    }

    /**
     * 从市场接口读取单个物品的目录详情。
     *
     * @param slug 市场物品的地址标识。
     * @return 市场物品的非受管详情；没有匹配数据时返回空结果。
     */
    private Optional<WarframeMarketItemDTO> loadItem(String slug) {
        HttpResult response = apiClient.fetchMarketItemResult(slug.trim());
        if (!success(response)) return Optional.empty();
        MarketResponseRawData<ItemRawData> root = readResponse(response, objectMapper.constructType(ItemRawData.class), slug);
        ItemRawData data = root.getData();
        if (data == null && root.getPayload() != null) data = root.getPayload().getItem();
        return data == null ? Optional.empty() : Optional.of(itemParser.parse(data));
    }

    /**
     * 取得市场套装及其部件详情。
     *
     * @param slug 市场物品的地址标识。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    @Override
    public Optional<WarframeMarketItemSetDTO> fetchItemSet(String slug) {
        if (blank(slug)) return Optional.empty();
        return cached(WarframeCacheNames.METADATA, List.of("market:set", norm(slug)),
                () -> loadItemSet(norm(slug)));
    }

    /**
     * 从市场接口读取套装及其部件列表。
     *
     * @param slug 市场物品的地址标识。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    private Optional<WarframeMarketItemSetDTO> loadItemSet(String slug) {
        HttpResult response = apiClient.fetchMarketItemSetResult(slug.trim());
        if (!success(response)) return Optional.empty();
        MarketResponseRawData<ItemSetRawData> root = readResponse(response, objectMapper.constructType(ItemSetRawData.class), slug);
        ItemSetRawData data = root.getData();
        if (data == null || data.getItems() == null) return Optional.empty();
        return Optional.of(new WarframeMarketItemSetDTO(data.getId(), data.getItems().stream().map(itemParser::parse).toList(),
                root.getApiVersion(), error(root.getError()), response.body()));
    }

    /**
     * 读取市场物品目录。
     *
     * @return 市场物品的非受管详情组成的列表。
     */
    @Override
    public List<WarframeMarketItemDTO> fetchItems() {
        return cached(WarframeCacheNames.METADATA, "market:items", () -> {
            List<WarframeMarketItemDTO> items = loadItems();
            return CollUtil.isEmpty(items) ? Optional.empty() : Optional.of(items);
        }).orElseGet(List::of);
    }

    /**
     * 从市场接口读取物品目录列表。
     *
     * @return 市场物品的非受管详情组成的列表。
     */
    private List<WarframeMarketItemDTO> loadItems() {
        HttpResult response = apiClient.fetchMarketItemsResult();
        return parseCatalog(response);
    }

    /**
     * 读取指定拍卖类别可选的武器目录。
     *
     * @param type 目标业务类型。
     * @return 市场物品的非受管详情组成的列表。
     */
    @Override
    public List<WarframeMarketItemDTO> fetchAuctionWeapons(String type) {
        String normalized = norm(type);
        if (!List.of("riven", "lich", "sister").contains(normalized)) {
            throw new IllegalArgumentException("Unsupported auction type: " + type);
        }
        return cached(WarframeCacheNames.METADATA, List.of("market:auction-weapons", normalized), () -> {
            List<WarframeMarketItemDTO> items = parseCatalog(apiClient.fetchAuctionWeaponsResult(normalized));
            return CollUtil.isEmpty(items) ? Optional.empty() : Optional.of(items);
        }).orElseGet(List::of);
    }

    /**
     * 目录与拍卖武器均使用 v2 的 slug/i18n 结构，但缓存按用途和类型隔离。
     *
     * @param response 上游接口返回的响应。
     * @return 市场物品的非受管详情组成的列表。
     */
    private List<WarframeMarketItemDTO> parseCatalog(HttpResult response) {
        if (!success(response)) return List.of();
        MarketResponseRawData<List<ItemRawData>> root = readResponse(response,
                objectMapper.getTypeFactory().constructCollectionType(List.class, ItemRawData.class), "物品目录");
        List<ItemRawData> values = root.getData();
        if (values == null && root.getPayload() != null) values = root.getPayload().getItems();
        return CollUtil.isEmpty(values) ? List.of() : values.stream().map(itemParser::parse).toList();
    }

    /**
     * 查询指定物品及词条条件的市场拍卖。
     *
     * @param type 目标业务类型。
     * @param weapon 武器。
     * @param positive 正面集合。
     * @param negative 负面集合。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    @Override
    public Optional<WarframeMarketAuctionSnapshotDTO> searchAuctions(
            String type, String weapon, List<String> positive, List<String> negative) {
        if (blank(weapon)) return Optional.empty();
        String t = norm(type), w = norm(weapon);
        List<String> p = normalize(positive), n = normalize(negative);
        return cached(WarframeCacheNames.MARKET_AUCTIONS, List.of("auction", t, w, p, n),
                () -> loadAuctions(t, w, p, n));
    }

    /**
     * 从市场接口读取拍卖并排除离线发布者。
     *
     * @param t 拍卖类别代码。
     * @param w 武器的市场标识。
     * @param p 正面词条筛选列表。
     * @param n 负面词条筛选列表。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    private Optional<WarframeMarketAuctionSnapshotDTO> loadAuctions(
            String t, String w, List<String> p, List<String> n) {
        HttpResult response = apiClient.fetchAuctionSearchResult(t, w, p, n);
        if (!success(response)) return Optional.empty();
        MarketResponseRawData<Void> root = readResponse(response, objectMapper.constructType(Void.class), w);
        if (root.getPayload() == null || root.getPayload().getAuctions() == null) {
            throw new WarframeMarketDataException("市场响应缺少拍卖数组：" + w, null);
        }
        var auctions = root.getPayload().getAuctions().stream()
                .filter(value -> value.getOwner() == null || !"offline".equalsIgnoreCase(value.getOwner().getStatus()))
                .map(this::parseAuction).toList();
        return Optional.of(new WarframeMarketAuctionSnapshotDTO(t, w, auctions, p, n, response.body()));
    }

    /**
     * 取得市场物品的成交统计。
     *
     * @param slug 市场物品的地址标识。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    @Override
    public Optional<WarframeMarketStatisticsDTO> fetchStatistics(String slug) {
        if (blank(slug)) return Optional.empty();
        return cached(WarframeCacheNames.MARKET_STATISTICS, List.of("statistics", norm(slug)),
                () -> loadStatistics(norm(slug)));
    }

    /**
     * 从市场接口读取物品的成交统计。
     *
     * @param slug 市场物品的地址标识。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    private Optional<WarframeMarketStatisticsDTO> loadStatistics(String slug) {
        HttpResult response = apiClient.fetchMarketStatisticsResult(slug.trim());
        if (!success(response)) return Optional.empty();
        MarketResponseRawData<StatisticsDataRawData> root = readResponse(response, objectMapper.constructType(StatisticsDataRawData.class), slug);
        var payload = root.getPayload();
        var values = payload == null || payload.getStatisticsLive() == null ? null : payload.getStatisticsLive().getDays90();
        if (values == null && root.getData() != null) values = root.getData().getStatistics();
        return Optional.of(new WarframeMarketStatisticsDTO(slug.trim(), CollUtil.isEmpty(values) ? List.of() : values.stream().map(value -> new MarketStatisticDTO(
                value.getDatetime(), value.getVolume(), value.getOrderType(), value.getMinimum(), value.getMaximum(),
                value.getAverage(), value.getWeightedAverage(), value.getMedian(), value.getModRank())).toList(), response.body()));
    }

    /**
     * 清除当前业务使用的缓存区域。
     */
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
     *   使用缓存自身的同键加载协调；请求失败不缓存，真实的空订单快照仍可缓存。
     *
     * @param cacheName 缓存名称。
     * @param key 当前记录或字段的查找键。
     * @param loader 加载器。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    private <T> Optional<T> cached(String cacheName, Object key, Supplier<Optional<T>> loader) {
        return cacheReader.get(cacheName, key, loader);
    }

    /**
     * 对已筛选数据执行分页。
     *
     * @param values 待处理的值集合。
     * @param page 从一开始的页码。
     * @return 包含当前页记录及总数的分页结果。
     */
    private static <T> PageResult<T> page(List<T> values, int page) {
        return page(values, page, AUCTION_PAGE_SIZE);
    }

    /**
     * 对已筛选数据执行分页。
     *
     * @param values 待处理的值集合。
     * @param page 从一开始的页码。
     * @param size 当前操作允许的数据数量。
     * @return 包含当前页记录及总数的分页结果。
     */
    private static <T> PageResult<T> page(List<T> values, int page, int size) {
        // 先以 long 运算，避免用户输入超大页码导致负数下标。
        int from = (int) Math.min(((long) page - 1) * size, values.size());
        return PageResult.of(values.subList(from, Math.min(from + size, values.size())), page, size, values.size());
    }

    /**
     * 按订单方向计算当前页面使用的价格摘要。
     *
     * @param orders 订单集合。
     * @param type 目标业务类型。
     * @return 按订单方向计算当前页面使用的价格摘要对应的文本。
     */
    private static String price(List<WarframeMarketOrderDTO> orders, String type) {
        return orders.stream().filter(order -> type.equals(order.orderType())).findFirst()
                .map(order -> order.platinum() + " 白金").orElse("");
    }

    /**
     * 判断订单等级是否满足当前最高等级筛选。
     *
     * @param requested 请求的。
     * @param maxRank 最大等级。
     * @param item 当前物品或集合成员。
     * @param order 成员在输入集合中的位置。
     * @return 满足订单等级是否满足当前最高等级筛选时为真，否则为假。
     */
    private static boolean matchesMaxRank(boolean requested, Integer maxRank,
                                          WarframeMarketItemDTO item, WarframeMarketOrderDTO order) {
        if (!requested) return true;
        if (maxRank != null && !maxRank.equals(order.rank())) return false;
        // 阿耶檀识塑像的满级是两种星星分别装满，不可将星星数量与 Mod 等级比较。
        return (item.maxAmberStars() == null || item.maxAmberStars().equals(order.amberStars()))
                && (item.maxCyanStars() == null || item.maxCyanStars().equals(order.cyanStars()));
    }

    /**
     * 将拍卖的固定字段映射到业务 DTO。
     *
     * @param value 已绑定的原始拍卖
     * @return 包含价格、发布者与物品词条的拍卖 DTO
     */
    private WarframeMarketAuctionEntryDTO parseAuction(AuctionRawData value) {
        Objects.requireNonNull(value, "拍卖记录不能为空");
        UserRawData owner = value.getOwner() == null ? new UserRawData() : value.getOwner();
        AuctionItemRawData item = value.getItem() == null ? new AuctionItemRawData() : value.getItem();
        List<AttributeDTO> attributes = CollUtil.isEmpty(item.getAttributes()) ? List.of()
                : item.getAttributes().stream().map(attribute -> new AttributeDTO(attribute.getUrlName(),
                        attribute.getValue() == null ? 0 : attribute.getValue().doubleValue(), Boolean.TRUE.equals(attribute.getPositive()))).toList();
        String status = StrUtil.emptyIfNull(owner.getStatus());
        String statusText = switch (status.toLowerCase(Locale.ROOT)) {
            case "online" -> "在线";
            case "ingame" -> "游戏中";
            case "offline" -> "离线";
            default -> status;
        };
        return new WarframeMarketAuctionEntryDTO(value.getId(), owner.getIngameName(), status, statusText,
                value.getStartingPrice(), value.getBuyoutPrice(), value.getTopBid(), Boolean.TRUE.equals(value.getDirectSell()),
                value.getCreated(), new WarframeMarketAuctionEntryDTO.AuctionItemDTO(item.getType(), item.getWeaponUrlName(),
                item.getName(), item.getModRank(), item.getRerolls(), item.getMasteryLevel(), item.getPolarity(), item.getElement(),
                item.getDamage(), item.getEphemera(), item.getQuirk(), attributes));
    }

    /**
     * 优先选择一口价，缺省时使用起拍价。
     *
     * @param auction 拍卖。
     * @return 当前操作计算或读取的数值。
     */
    private static Integer auctionPrice(WarframeMarketAuctionEntryDTO auction) {
        return auction.buyoutPrice() == null ? auction.startingPrice() : auction.buyoutPrice();
    }

    /**
     * 将订单及其发布者映射为非受管 DTO。
     *
     * @param order 已按市场协议绑定的订单
     * @return 规范化的订单，缺省可见性按公开处理
     */
    private WarframeMarketOrderDTO parseOrder(OrderRawData order) {
        Objects.requireNonNull(order, "订单不能为空");
        UserRawData user = order.getUser() == null ? new UserRawData() : order.getUser();
        return new WarframeMarketOrderDTO(order.getId(), StrUtil.emptyIfNull(order.getType()).toLowerCase(Locale.ROOT),
                order.getPlatinum() == null ? 0 : order.getPlatinum(), order.getQuantity() == null ? 0 : order.getQuantity(),
                order.getPerTrade(), order.getSubtype(), order.getRank(), order.getCharges(), order.getAmberStars(), order.getCyanStars(),
                !Boolean.FALSE.equals(order.getVisible()), order.getCreatedAt(), order.getUpdatedAt(), order.getItemId(), order.getGroupId(),
                new WarframeMarketUserDTO(user.getId(), user.getIngameName(), user.getSlug(), user.getReputation(), user.getPlatform(),
                        Boolean.TRUE.equals(user.getCrossplay()), user.getLocale(), user.getStatus(), user.getAvatar(), user.getLastSeen(),
                        user.getActivity(), objectMapper.convertValue(user, new TypeReference<>() {})),
                objectMapper.convertValue(order, new TypeReference<>() {}));
    }

    /**
     * 在外部协议边界执行一次具体类型绑定，业务方法不重复捕获同一解析异常。
     *
     * @param response 成功的市场 HTTP 响应
     * @param dataType 该端点数据区的具体类型
     * @return 已绑定的协议响应
     * @param identity 用于定位本次请求的业务身份。
     */
    private <T> MarketResponseRawData<T> readResponse(HttpResult response, JavaType dataType, String identity) {
        try {
            return objectMapper.readValue(response.body(), objectMapper.getTypeFactory()
                    .constructParametricType(MarketResponseRawData.class, dataType));
        } catch (JsonProcessingException exception) {
            throw new WarframeMarketDataException("市场响应不符合类型化字段合同：" + identity + "：" + exception.getOriginalMessage(), exception);
        }
    }

    /**
     * 规范化输入，消除查询或比较时的格式差异。
     *
     * @param values 待处理的值集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<String> normalize(List<String> values) {
        return values == null ? List.of() : values.stream().filter(v -> !blank(v)).map(WarframeMarketServiceImpl::norm)
                .distinct().sorted().toList();
    }
    /**
     * 规范化文本的首尾空白和大小写，供查找键比较使用。
     *
     * @param value 待校验或规范化的文本。
     * @return 规范化文本的首尾空白和大小写，供查找键比较使用对应的文本。
     */
    private static String norm(String value) { return value == null ? "" : value.trim().toLowerCase(Locale.ROOT); }
    /**
     * 判断 HTTP 响应是否成功且包含可用正文。
     *
     * @param r 当前 HTTP 响应。
     * @return 满足 HTTP 响应是否成功且包含可用正文时为真，否则为假。
     */
    private static boolean success(HttpResult r) { return r != null && r.isSuccess() && !blank(r.body()); }
    /**
     * 判断文本是否缺失或仅包含空白字符。
     *
     * @param value 待校验或规范化的文本。
     * @return 满足文本是否缺失或仅包含空白字符时为真，否则为假。
     */
    private static boolean blank(String value) { return StrUtil.isBlank(value); }
    /**
     * 保留上游错误的 JSON 表达，仅用于排障输出。
     *
     * @param value 上游错误原文，可为空
     * @return 错误的 JSON 文本；未提供错误时返回空字符串
     */
    private String error(Object value) {
        if (value == null) return "";
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new WarframeMarketDataException("市场错误原文无法序列化", exception);
        }
    }

    public static final class WarframeMarketDataException extends RuntimeException {
        /**
         * 构造市场数据异常，并保存初始化参数。
         *
         * @param message 待发送或处理的消息。
         * @param cause 失败原因。
         */
        public WarframeMarketDataException(String message, Throwable cause) { super(message, cause); }
    }
}
