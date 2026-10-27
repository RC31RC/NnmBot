package com.nnm.bot.warframe.plugin.runtime.market;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.core.constant.CommandPresentationConstants.ParameterType;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketOrderPageDTO;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.CommandView;
import com.nnm.bot.core.plugin.model.RelatedParameter;
import java.util.Map;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeItemDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketItemDTO;
import com.nnm.bot.warframe.runtime.pojo.vo.market.WarframeMarketOrderVO;
import com.nnm.bot.warframe.runtime.constant.WarframeMarketConstants.OrderFilter;
import com.nnm.bot.warframe.runtime.pojo.query.market.WarframeMarketOrderQuery;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeItemResolutionDTO;
import com.nnm.bot.warframe.runtime.service.market.WarframeMarketService;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeItemResolverService;
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
@BotPluginCommand(code = WarframeCommandCodes.MARKET, matchType = MatchType.PREFIX, name = "市场", allowRelatedAction = true, gameCode = "warframe", order = 20)
// 订单来自 Warframe Market API；商品身份从官方目录与人工别名/术语白名单解析。
public class MarketPlugin extends BotPlugin {

    /**
     * 声明市场关联动作需要保留的查询上下文键。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    public Set<String> actionContextKeys() { return Set.of("item.name", "item.marketSlug"); }

    /**
     * 声明市场关联查询支持的参数及其展示信息。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    public List<RelatedParameter> relatedParameters() {
        return List.of(new RelatedParameter("item", ParameterType.TOKEN, 200));
    }

    /**
     * 将业务数据转换为命令或页面所需的展示字段。
     *
     * @return 当前命令或页面需要的展示字段。
     */
    @Override
    public List<CommandView> views() {
        return List.of(CommandView.textImageAndMarkdown("warframe.market.orders"));
    }

    private static final Set<String> PLATFORMS = Set.of("pc", "ps4", "xbox", "switch", "mobile");

    @Resource
    private WarframeItemResolverService itemResolver;

    @Resource
    private WarframeMarketService marketService;

    /**
     * 解析参数并选择图文共用的展示字段；订单过滤、排序及分页全部由 Service 完成。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation) {
        WarframeMarketOrderQuery query = parseQuery(invocation.arguments());
        if (StrUtil.isBlank(query.itemInput())) {
            return CommandResult.rejected("格式：wm [满级] [买家|卖家] [平台] 物品名称 [页码]");
        }
        WarframeItemResolutionDTO resolution = itemResolver.resolve(query.itemInput());
        // Market 有自己的可交易目录，不能因为官方物品多候选就提前拒绝匹配。
        String slug = resolveMarketSlug(query.itemInput(), resolution);
        if (StrUtil.isBlank(slug)) {
            if (resolution.ambiguous()) {
                return CommandResult.rejected("物品名称存在多个候选，请输入更完整的名称："
                        + resolution.candidates().stream().map(WarframeItemDTO::getLocalizedName)
                        .collect(Collectors.joining("、")));
            }
            return CommandResult.rejected("没有找到对应的 Warframe Market 物品，请输入物品名称或 Market URL 名称");
        }
        query = query.withMarketSlug(slug);
        Optional<WarframeMarketOrderPageDTO> result = marketService.queryOrders(query);
        if (result.isEmpty()) {
            return CommandResult.rejected("暂时无法获取 " + query.itemInput() + " 的市场订单，请稍后再试");
        }
        WarframeMarketOrderPageDTO data = result.get();
        // Service 保留完整查询能力；WM 不将百科详情和重复订单视图交给发送渠道。
        return CommandResult.view("warframe.market.orders", "市场订单", WarframeMarketOrderVO.from(data))
                .withActionContext(resolution.found() && !resolution.ambiguous()
                        ? Map.of("item.name", StrUtil.firstNonBlank(resolution.item().getLocalizedName(),
                                resolution.item().getOfficialName()), "item.marketSlug", slug) : Map.of());
    }

    /**
     * 将尾部页码、平台、买卖方向和满级开关从物品名称中分离，页码为 1-based。
     *
     * @param raw 原始。
     * @return 游戏资料市场订单查询对象。
     */
    static WarframeMarketOrderQuery parseQuery(String raw) {
        String[] tokens = StrUtil.emptyIfNull(raw).trim().split("\\s+");
        boolean maxLevel = false;
        OrderFilter filter = OrderFilter.ALL;
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
                filter = OrderFilter.BUY;
            } else if (Set.of("出售", "卖家", "sell").contains(normalized)) {
                filter = OrderFilter.SELL;
            } else if (PLATFORMS.contains(normalized)) {
                platform = normalized;
            } else if (StrUtil.isNotBlank(token)) {
                item.add(token);
            }
        }
        return new WarframeMarketOrderQuery(String.join(" ", item), "", platform, filter, maxLevel, page);
    }

    /**
     * 对照参考项目的销售词库：先展开黑话/部件，再匹配市场目录中的中文、英文和 slug。
     *   同分候选优先完整套装，但已明确输入的 slug 或全名始终优先。
     *
     * @param input 待解析或校验的输入。
     * @param resolution 解析状态。
     * @return 解析市场地址标识对应的文本。
     */
    private String resolveMarketSlug(String input, WarframeItemResolutionDTO resolution) {
        String direct = input.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", "_");
        List<WarframeMarketItemDTO> catalog;
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
        List<MarketCandidate> matched = catalog.stream().filter(item -> StrUtil.isNotBlank(item.slug()))
                .map(item -> new MarketCandidate(item, bestMarketScore(item, terms),
                        marketIntentPriority(item, input, exactTerms)))
                .filter(candidate -> candidate.score() >= 0.78D)
                .sorted(Comparator.comparingInt(MarketCandidate::intentPriority)
                        .thenComparing(Comparator.comparingDouble(MarketCandidate::score).reversed())
                        .thenComparing(candidate -> marketPriority(candidate.item()))
                        .thenComparing(candidate -> candidate.item().slug()))
                .toList();
        if (CollUtil.isNotEmpty(matched)) {
            MarketCandidate best = matched.getFirst();
            boolean ambiguous = matched.stream().skip(1).anyMatch(candidate ->
                    candidate.intentPriority() == best.intentPriority()
                            && Double.compare(candidate.score(), best.score()) == 0
                            && marketPriority(candidate.item()) == marketPriority(best.item())
                            && !candidate.item().slug().equals(best.item().slug()));
            // Slug sorting is deterministic display order, never evidence of a unique identity.
            return ambiguous ? "" : best.item().slug();
        }
        // 目录短暂不可用时允许明确的 URL slug；中文不得原样发送给上游。
        if (direct.matches("[a-z0-9]+(?:_[a-z0-9]+)+")) return direct;
        if (resolution.found() && StrUtil.isNotBlank(resolution.item().getMarketSlug())) {
            return resolution.item().getMarketSlug();
        }
        return "";
    }

    /**
     * 结合精确匹配词和物品类型计算当前查询意图的候选优先级。
     * @param item 当前物品或集合成员。
     * @param input 待解析或校验的输入。
     * @param exactTerms 精确术语集合。
     * @return 当前操作计算或读取的数值。
     */
    private static int marketIntentPriority(WarframeMarketItemDTO item, String input, Set<String> exactTerms) {
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

    /**
     * 规则分值而非统计置信度；业务意图先排序，分值仅比较同一意图等级。
     *
     * @param item 当前物品或集合成员。
     * @param terms 术语集合。
     * @return 当前操作计算或读取的数值。
     */
    private static double bestMarketScore(WarframeMarketItemDTO item, Set<String> terms) {
        List<String> names = List.of(item.slug(), item.localizedName("zh-hans"), item.localizedName("en"));
        double best = 0;
        for (String term : terms) {
            for (String name : names) {
                String candidate = compact(name);
                if (StrUtil.isBlank(candidate)) continue;
                if (candidate.equals(term)) best = Math.max(best, 1);
                else if (candidate.contains(term)) best = Math.max(best, 0.94);
                else if (isSubsequence(term, candidate)) best = Math.max(best, 0.82);
            }
        }
        return best;
    }

    /**
     * 按市场物品类别计算候选排序优先级。
     * @param item 当前物品或集合成员。
     * @return 当前操作计算或读取的数值。
     */
    private static int marketPriority(WarframeMarketItemDTO item) {
        String slug = item.slug().toLowerCase(Locale.ROOT);
        if (Boolean.TRUE.equals(item.setRoot()) || slug.endsWith("_set")) return 0;
        return slug.contains("_prime") ? 1 : 2;
    }

    /**
     * 判断候选词是否按顺序出现在目标文本中。
     *
     * @param input 待解析或校验的输入。
     * @param candidate 候选。
     * @return 满足候选词是否按顺序出现在目标文本中时为真，否则为假。
     */
    private static boolean isSubsequence(String input, String candidate) {
        int index = 0;
        for (int i = 0; i < candidate.length() && index < input.length(); i++) {
            if (candidate.charAt(i) == input.charAt(index)) index++;
        }
        return index == input.length();
    }

    /**
     * 去除名称中的比较分隔符，生成市场候选匹配键。
     * @param value 待校验或规范化的文本。
     * @return 已去除比较分隔符的名称键。
     */
    private static String compact(String value) {
        return StrUtil.emptyIfNull(value).trim().toLowerCase(Locale.ROOT)
                .replaceAll("[\\s_\\-·•/\\\\]+", "");
    }

    private record MarketCandidate(WarframeMarketItemDTO item, double score, int intentPriority) {}
}
