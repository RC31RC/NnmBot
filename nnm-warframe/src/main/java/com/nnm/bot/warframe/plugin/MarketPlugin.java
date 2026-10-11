package com.nnm.bot.warframe.plugin;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.application.WarframeItemResolution;
import com.nnm.bot.warframe.application.WarframeMarketService;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.model.entity.WarframeItem;
import com.nnm.bot.warframe.pojo.WarframeMarketItem;
import com.nnm.bot.warframe.pojo.WarframeMarketOrderQuery;
import com.nnm.bot.warframe.pojo.WarframeMarketOrderQuery.Filter;
import com.nnm.bot.warframe.pojo.WarframeMarketOrderResult;
import com.nnm.bot.warframe.pojo.WarframeMarketOrderDisplay;
import com.nnm.bot.warframe.service.WarframeItemResolverService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/** 市场命令入口：解析语法和物品别名，将查询对象交给统一 Market Service。 */
@Slf4j
@BotPluginCommand(code = WarframeCommandCodes.MARKET, matchType = MatchType.PREFIX, name = "市场", gameCode = "warframe", order = 20)
// 订单来自外部 Warframe Market API（cache2k 缓存）；wf_official_export_record、wf_community_dataset_translation、
// wf_community_term、wf_community_dataset_alias 仅用于把用户输入解析为物品名和 WM slug。
public class MarketPlugin extends BotPlugin {

    private static final Set<String> PLATFORMS = Set.of("pc", "ps4", "xbox", "switch", "mobile");

    @Resource
    private WarframeItemResolverService itemResolver;

    @Resource
    private WarframeMarketService marketService;

    /** 解析参数并选择图文共用的展示字段；订单过滤、排序及分页全部由 Service 完成。 */
    @Override
    public CommandResult handle(CommandInvocation invocation) {
        WarframeMarketOrderQuery query = parseQuery(invocation.arguments());
        if (query.itemInput().isBlank()) {
            return CommandResult.rejected("格式：wm [满级] [买家|卖家] [平台] 物品名称 [页码]");
        }
        WarframeItemResolution resolution = itemResolver.resolve(query.itemInput());
        // Market 有自己的可交易目录，不能因为官方物品多候选就提前拒绝匹配。
        String slug = resolveMarketSlug(query.itemInput(), resolution);
        if (slug.isBlank()) {
            if (resolution.ambiguous()) {
                return CommandResult.text("物品名称存在多个候选，请输入更完整的名称："
                        + resolution.candidates().stream().map(WarframeItem::getLocalizedName)
                        .collect(Collectors.joining("、")));
            }
            return CommandResult.text("没有找到对应的 Warframe Market 物品，请输入物品名称或 Market URL 名称");
        }
        query = query.withMarketSlug(slug);
        Optional<WarframeMarketOrderResult> result = marketService.queryOrders(query);
        if (result.isEmpty()) {
            return CommandResult.text("暂时无法获取 " + query.itemInput() + " 的市场订单，请稍后再试");
        }
        WarframeMarketOrderResult data = result.get();
        // Service 保留完整查询能力；WM 不将百科详情和重复订单视图交给发送渠道。
        return CommandResult.of("市场订单", WarframeMarketOrderDisplay.from(data));
    }

    /** 将尾部页码、平台、买卖方向和满级开关从物品名称中分离，页码为 1-based。 */
    static WarframeMarketOrderQuery parseQuery(String raw) {
        String[] tokens = StrUtil.emptyIfNull(raw).trim().split("\\s+");
        boolean maxLevel = false;
        Filter filter = Filter.ALL;
        String platform = "";
        List<String> item = new ArrayList<>();
        int page = 1;
        int end = tokens.length;
        if (end > 0 && tokens[end - 1].matches("\\d+")) {
            try {
                page = Math.max(1, Integer.parseInt(tokens[end - 1]));
            } catch (NumberFormatException ignored) {
                page = Integer.MAX_VALUE;
            }
            end--;
        }
        for (int index = 0; index < end; index++) {
            String token = tokens[index];
            // 中文开关允许连写；英文 max 仍要求独立参数，避免破坏物品名称。
            if (token.startsWith("满级") || token.endsWith("满级")) {
                maxLevel = true;
                token = token.replaceAll("^满级|满级$", "");
            }
            String normalized = token.toLowerCase(Locale.ROOT);
            if ("满级".equals(token) || "max".equals(normalized)) {
                maxLevel = true;
            } else if (Set.of("购买", "买家", "buy").contains(normalized)) {
                filter = Filter.BUY;
            } else if (Set.of("出售", "卖家", "sell").contains(normalized)) {
                filter = Filter.SELL;
            } else if (PLATFORMS.contains(normalized)) {
                platform = normalized;
            } else if (!token.isBlank()) {
                item.add(token);
            }
        }
        return new WarframeMarketOrderQuery(String.join(" ", item), "", platform, filter, maxLevel, page);
    }

    /**
     * 对照参考项目的销售词库：先展开黑话/部件，再匹配市场目录中的中文、英文和 slug。
     * 同分候选优先完整套装，但已明确输入的 slug 或全名始终优先。
     */
    private String resolveMarketSlug(String input, WarframeItemResolution resolution) {
        String direct = input.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", "_");
        List<WarframeMarketItem> catalog;
        try {
            catalog = marketService.fetchItems();
        } catch (RuntimeException exception) {
            log.warn("Market catalog unavailable, attempting explicit slug", exception);
            catalog = List.of();
        }
        List<String> searchTerms = new ArrayList<>();
        searchTerms.add(input);
        searchTerms.addAll(itemResolver.marketSearchTerms(input));
        if (resolution.found() && resolution.item() != null) {
            searchTerms.add(resolution.item().getOfficialName());
            searchTerms.add(resolution.item().getLocalizedName());
            searchTerms.add(resolution.item().getMarketSlug());
        }
        Set<String> terms = searchTerms.stream().filter(StrUtil::isNotBlank)
                .map(MarketPlugin::compact).collect(Collectors.toCollection(LinkedHashSet::new));
        // 裸名称的 Prime 偏好只使用精确名称/别名，不能把模糊纠错结果当成用户意图。
        Set<String> exactTerms = new LinkedHashSet<>();
        exactTerms.add(compact(input));
        itemResolver.auctionWeaponSearchTerms(input).stream()
                .filter(StrUtil::isNotBlank).map(MarketPlugin::compact).forEach(exactTerms::add);
        Optional<String> matched = catalog.stream().filter(item -> StrUtil.isNotBlank(item.slug()))
                .map(item -> new MarketCandidate(item, bestMarketScore(item, terms),
                        marketIntentPriority(item, input, exactTerms)))
                .filter(candidate -> candidate.score() >= 0.78D)
                .sorted(Comparator.comparingInt(MarketCandidate::intentPriority)
                        .thenComparing(Comparator.comparingDouble(MarketCandidate::score).reversed())
                        .thenComparing(candidate -> marketPriority(candidate.item()))
                        .thenComparing(candidate -> candidate.item().slug()))
                .map(candidate -> candidate.item().slug()).findFirst();
        if (matched.isPresent()) return matched.get();
        // 目录短暂不可用时允许明确的 URL slug；中文不得原样发送给上游。
        if (direct.matches("[a-z0-9]+(?:_[a-z0-9]+)+")) return direct;
        if (resolution.found() && StrUtil.isNotBlank(resolution.item().getMarketSlug())) {
            return resolution.item().getMarketSlug();
        }
        return "";
    }

    private static int marketIntentPriority(WarframeMarketItem item, String input, Set<String> exactTerms) {
        String slug = item.slug().toLowerCase(Locale.ROOT);
        // 带下划线的完整 slug 是显式商品选择，不受裸名称偏好影响。
        if (input.contains("_") && slug.equalsIgnoreCase(input.trim())) return 0;
        if (slug.endsWith("_prime_set") || slug.endsWith("_prime")) {
            String base = slug.replaceFirst("_prime(?:_set)?$", "");
            if (exactTerms.contains(compact(base))) return 1;
        }
        if (List.of(slug, item.localizedName("zh-hans"), item.localizedName("en"))
                .stream().map(MarketPlugin::compact).anyMatch(compact(input)::equals)) return 2;
        return 3;
    }

    /** 规则分值而非统计置信度；业务意图先排序，分值仅比较同一意图等级。 */
    private static double bestMarketScore(WarframeMarketItem item, Set<String> terms) {
        List<String> names = List.of(item.slug(), item.localizedName("zh-hans"), item.localizedName("en"));
        double best = 0;
        for (String term : terms) {
            for (String name : names) {
                String candidate = compact(name);
                if (candidate.isBlank()) continue;
                if (candidate.equals(term)) best = Math.max(best, 1);
                else if (candidate.contains(term)) best = Math.max(best, 0.94);
                else if (isSubsequence(term, candidate)) best = Math.max(best, 0.82);
            }
        }
        return best;
    }

    private static int marketPriority(WarframeMarketItem item) {
        String slug = item.slug().toLowerCase(Locale.ROOT);
        if (Boolean.TRUE.equals(item.setRoot()) || slug.endsWith("_set")) return 0;
        return slug.contains("_prime") ? 1 : 2;
    }

    private static boolean isSubsequence(String input, String candidate) {
        int index = 0;
        for (int i = 0; i < candidate.length() && index < input.length(); i++) {
            if (candidate.charAt(i) == input.charAt(index)) index++;
        }
        return index == input.length();
    }

    private static String compact(String value) {
        return StrUtil.emptyIfNull(value).trim().toLowerCase(Locale.ROOT)
                .replaceAll("[\\s_\\-·•/\\\\]+", "");
    }

    private record MarketCandidate(WarframeMarketItem item, double score, int intentPriority) {}
}
