package com.nnm.bot.warframe.application.plugin;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.warframe.application.WarframeItemResolution;
import com.nnm.bot.warframe.application.WarframeItemResolver;
import com.nnm.bot.warframe.domain.entity.WarframeItem;
import com.nnm.bot.warframe.infrastructure.client.WarframeApiClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Warframe Market 订单查询。
 *
 * <p>市场数据随时变化，因此这里只缓存和展示单次查询结果，不把订单写入
 * WorldState。物品名称优先通过本地别名表解析，未命中时兼容直接输入 Market slug。</p>
 */
@Slf4j
@BotPluginCommand(
        code = WarframeCommandCodes.MARKET,
        matchType = MatchType.PREFIX,
        name = "市场",
        gameCode = "warframe",
        order = 20)
public class MarketPlugin extends BotPlugin {

    private static final Set<String> PLATFORMS = Set.of("pc", "ps4", "xbox", "switch");

    @Resource
    private WarframeApiClient apiClient;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private WarframeItemResolver itemResolver;

    @Override
    public CommandResult handle(CommandInvocation invocation) {
        if (invocation.arguments().isBlank()) {
            return CommandResult.text("市场命令格式：市场 物品URL名称，例如：市场 broken-war");
        }
        MarketQuery query = parseQuery(invocation.arguments());
        if (query.itemInput().isBlank()) {
            return CommandResult.text("市场命令格式：市场 [满级] [买家|卖家] [平台] 物品名称");
        }
        WarframeItemResolution resolution = this.itemResolver.resolve(query.itemInput());
        if (resolution.ambiguous()) {
            return CommandResult.text("物品名称存在多个候选，请输入更完整的名称："
                    + resolution.candidates().stream()
                            .map(WarframeItem::getLocalizedName)
                            .collect(java.util.stream.Collectors.joining("、")));
        }
        String marketSlug = resolution.found()
                && StrUtil.isNotBlank(resolution.item().getMarketSlug())
                ? resolution.item().getMarketSlug()
                : directMarketSlug(query.itemInput());
        query = query.withMarketSlug(marketSlug);

        String response = ObjectUtil.isNull(query.platform())
                ? apiClient.fetchMarketOrders(query.marketSlug())
                : apiClient.fetchMarketOrders(query.marketSlug(), query.platform());
        if (StrUtil.isBlank(response)) {
            return CommandResult.text("暂时无法获取 " + query.itemInput() + " 的市场订单，请稍后再试");
        }
        try {
            Map<String, Object> data = parseOrders(query, response);
            return CommandResult.of(summary(data), data);
        } catch (Exception exception) {
            log.warn("Unable to parse Warframe Market response for [{}]",
                    query.marketSlug(), exception);
            return CommandResult.text("市场返回数据无法解析，请稍后再试");
        }
    }

    private Map<String, Object> parseOrders(MarketQuery query, String response) throws Exception {
        JsonNode root = objectMapper.readTree(response);
        JsonNode orders = root.path("payload").path("orders");
        Integer maxRank = nullableInt(root.path("payload").path("item").path("max_rank"));
        ArrayList<Map<String, Object>> sellers = new ArrayList<>();
        ArrayList<Map<String, Object>> buyers = new ArrayList<>();
        if (orders.isArray()) {
            for (JsonNode order : orders) {
                String orderType = order.path("order_type").asText("").toLowerCase(Locale.ROOT);
                if (!query.accepts(orderType)) {
                    continue;
                }
                String status = order.path("user").path("status").asText("");
                if ("offline".equalsIgnoreCase(status)) {
                    continue;
                }
                if (!matchesMaxRank(query.maxLevel(), maxRank, order)) {
                    continue;
                }
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("userName", order.path("user").path("ingame_name").asText(""));
                item.put("reputation", order.path("user").path("reputation").asText(""));
                item.put("platinum", order.path("platinum").asInt(0));
                item.put("quantity", order.path("quantity").asInt(0));
                item.put("rank", nullableInt(order.path("mod_rank")));
                item.put("amberStars", nullableInt(order.path("amber_stars")));
                item.put("cyanStars", nullableInt(order.path("cyan_stars")));
                item.put("status", status);
                item.put("statusText", statusText(status));
                if ("sell".equals(orderType)) {
                    sellers.add(item);
                } else if ("buy".equals(orderType)) {
                    buyers.add(item);
                }
            }
        }
        sellers.sort(Comparator.comparingInt(item -> (Integer) item.get("platinum")));
        buyers.sort(Comparator.comparingInt(item -> -((Integer) item.get("platinum"))));

        Map<String, Object> data = new LinkedHashMap<>();
        String itemName = root.path("payload").path("item").path("en").asText("");
        data.put("itemName", itemName.isBlank() ? query.itemInput() : itemName);
        data.put("query", query.itemInput());
        data.put("marketSlug", query.marketSlug());
        data.put("platform", ObjectUtil.isNull(query.platform())
                ? apiClient.configuredMarketPlatform()
                : query.platform());
        data.put("filter", query.filterName());
        data.put("maxRankRequested", query.maxLevel());
        data.put("maxRank", ObjectUtil.isNull(maxRank) ? "" : maxRank);
        data.put("itemDescription", "Warframe Market 实时订单");
        data.put("sellerCount", sellers.size());
        data.put("buyerCount", buyers.size());
        data.put("sellers", sellers);
        data.put("buyers", buyers);
        data.put("lowestSellPrice", sellers.isEmpty() ? "" : sellers.getFirst().get("platinum") + " 白金");
        data.put("highestBuyPrice", buyers.isEmpty() ? "" : buyers.getFirst().get("platinum") + " 白金");
        data.put("updatedAt", OffsetDateTime.now().toString());
        return data;
    }

    private static String statusText(String status) {
        return switch (status.toLowerCase(Locale.ROOT)) {
            case "online" -> "在线";
            case "ingame" -> "游戏中";
            case "offline" -> "离线";
            default -> status;
        };
    }

    private static String summary(Map<String, Object> data) {
        return "市场：" + data.get("itemName")
                + "\n最低卖价：" + value(data.get("lowestSellPrice"))
                + "\n最高买价：" + value(data.get("highestBuyPrice"))
                + "\n平台：" + value(data.get("platform"));
    }

    private static String value(Object value) {
        return ObjectUtil.isNull(value) || StrUtil.isBlank(value.toString()) ? "暂无" : value.toString();
    }

    private static MarketQuery parseQuery(String raw) {
        String input = ObjectUtil.isNull(raw) ? "" : raw.trim();
        boolean maxLevel = false;
        Filter filter = Filter.ALL;
        String platform = null;

        StringBuilder item = new StringBuilder();
        for (String token : input.split("\\s+")) {
            if (token.isBlank()) {
                continue;
            }
            String normalized = token.toLowerCase(Locale.ROOT);
            if ("满级".equals(token) || "max".equals(normalized)) {
                maxLevel = true;
            } else if (Set.of("购买", "买家", "buy").contains(normalized)
                    || "购买".equals(token) || "买家".equals(token)) {
                filter = Filter.BUY;
            } else if (Set.of("出售", "卖家", "sell").contains(normalized)
                    || "出售".equals(token) || "卖家".equals(token)) {
                filter = Filter.SELL;
            } else if (PLATFORMS.contains(normalized)) {
                platform = normalized;
            } else {
                if (!item.isEmpty()) {
                    item.append(' ');
                }
                item.append(token);
            }
        }
        return new MarketQuery(item.toString().trim(), "", platform, filter, maxLevel);
    }

    private static String directMarketSlug(String itemInput) {
        return itemInput.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", "_");
    }

    private static boolean matchesMaxRank(boolean requested, Integer maxRank, JsonNode order) {
        if (!requested || ObjectUtil.isNull(maxRank)) {
            return true;
        }
        Integer rank = nullableInt(order.path("mod_rank"));
        if (ObjectUtil.isNotNull(rank)) {
            return rank.equals(maxRank);
        }
        Integer amberStars = nullableInt(order.path("amber_stars"));
        Integer cyanStars = nullableInt(order.path("cyan_stars"));
        return ObjectUtil.isNull(amberStars) || ObjectUtil.isNull(cyanStars)
                || (amberStars.equals(maxRank) && cyanStars.equals(maxRank));
    }

    private static Integer nullableInt(JsonNode node) {
        return ObjectUtil.isNotNull(node) && node.isIntegralNumber() ? node.asInt() : null;
    }

    private enum Filter {
        ALL, BUY, SELL
    }

    private record MarketQuery(String itemInput, String marketSlug, String platform,
                               Filter filter, boolean maxLevel) {

        private boolean accepts(String orderType) {
            return filter == Filter.ALL
                    || filter == Filter.BUY && "buy".equals(orderType)
                    || filter == Filter.SELL && "sell".equals(orderType);
        }

        public String filterName() {
            return filter.name();
        }

        private MarketQuery withMarketSlug(String marketSlug) {
            return new MarketQuery(this.itemInput, marketSlug, this.platform,
                    this.filter, this.maxLevel);
        }
    }
}
