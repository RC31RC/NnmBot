package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.application.WarframeMarketService;
import com.nnm.bot.warframe.pojo.WarframeMarketAuctionQuery;
import com.nnm.bot.warframe.pojo.WarframeMarketAuctionDisplay;
import com.nnm.bot.warframe.pojo.WarframeMarketItem;
import com.nnm.bot.warframe.service.catalog.WarframeCommunityCatalogService;
import com.nnm.bot.warframe.service.WarframeItemResolverService;
import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 拍卖命令公共入口：按拍卖目录识别武器，将武器、词条和页码封装为查询对象。 */
@BotPluginCommand(code = WarframeCommandCodes.MARKET_RIVEN, matchType = MatchType.PREFIX,
        name = "紫卡市场", gameCode = "warframe", order = 64)
// 拍卖和武器目录来自外部 Warframe Market API；wf_community_dataset_market_riven_tion 及其 _alias
// 解析词条及自维护组合别名（alias.attribute_code 为逗号分隔的属性编码）；
// wf_official_export_record、wf_community_dataset_translation、wf_community_term、
// wf_community_dataset_alias 用于武器名称匹配。
public final class MarketRivenPlugin extends BotPlugin {

    @Resource
    private WarframeItemResolverService itemResolver;
    @Resource
    private WarframeMarketService marketService;
    @Resource
    private WarframeCommunityCatalogService catalog;

    /** 解析武器及词条为查询对象；拍卖请求、价格排序与每页 10 条均由 Service 负责。 */
    @Override
    public CommandResult handle(CommandInvocation invocation) {
        String type = "riven";
        String input = invocation.arguments() == null ? "" : invocation.arguments().trim();
        if (input.isBlank()) {
            return CommandResult.rejected("格式：命令 武器名称 [词条] [负词条]");
        }
        if (input.length() > 500) return CommandResult.rejected("紫卡查询条件过长");
        String[] parts = input.split("\\s+");
        int end = parts.length;
        int page = 1;
        if (end > 1 && parts[end - 1].matches("\\d+")) {
            try {
                page = Math.max(1, Integer.parseInt(parts[end - 1]));
            } catch (NumberFormatException ignored) {
                page = Integer.MAX_VALUE;
            }
            end--;
        }
        List<WarframeMarketItem> weapons = marketService.fetchAuctionWeapons(type);
        if (weapons.isEmpty()) {
            return CommandResult.text("暂时无法获取拍卖武器目录，请稍后再试");
        }
        // 所有精确候选优先于包含候选；Prime 属于武器前缀，不是条件。
        List<WarframeMarketItem> candidates = List.of();
        int weaponEnd = end;
        for (boolean exact : List.of(true, false)) {
            for (int boundary = end; boundary > 0; boundary--) {
                candidates = matchWeapons(String.join(" ", Arrays.copyOf(parts, boundary)), weapons, exact);
                if (!candidates.isEmpty()) {
                    weaponEnd = boundary;
                    break;
                }
            }
            if (!candidates.isEmpty()) break;
        }
        if (candidates.isEmpty()) {
            return CommandResult.rejected("没有找到对应的拍卖武器，请检查武器名称");
        }
        if (candidates.size() > 1) {
            return CommandResult.rejected("武器名称存在多个候选："
                    + String.join("、", candidates.stream().limit(10)
                    .map(item -> item.localizedName("zh-hans")).toList()));
        }
        Map<String, Set<List<String>>> attributes = attributeIndex();
        List<String> keywords = attributes.keySet().stream()
                .sorted(Comparator.comparingInt(String::length).reversed()).toList();
        List<String> positive = new ArrayList<>();
        List<String> negative = new ArrayList<>();
        boolean requireNegative = false;
        for (int index = weaponEnd; index < end; index++) {
            String token = parts[index];
            if (Set.of("负任意", "负面任意").contains(token)) {
                requireNegative = true;
                continue;
            }
            boolean isNegative = token.startsWith("负");
            String remaining = normalize(token.replaceFirst("^负面?", ""));
            if (remaining.isEmpty()) return CommandResult.rejected("负面词条不能为空");
            // 字典最长匹配支持相连词条；单条记录内的多属性表示组合，不同记录表示歧义。
            while (!remaining.isEmpty()) {
                String rest = remaining;
                String keyword = keywords.stream().filter(rest::startsWith).findFirst().orElse(null);
                if (keyword == null) {
                    return CommandResult.rejected("无法识别词条，请检查或在管理端维护别名：" + token);
                }
                Set<List<String>> mappings = attributes.get(keyword);
                if (mappings.size() > 1) {
                    return CommandResult.rejected("词条存在多个候选，请使用完整词条名称：" + token);
                }
                (isNegative ? negative : positive).addAll(mappings.iterator().next());
                remaining = remaining.substring(keyword.length());
            }
        }
        if (new LinkedHashSet<>(positive).size() != positive.size()
                || positive.size() > 3 || negative.size() > 1
                || positive.stream().anyMatch(negative::contains)) {
            return CommandResult.rejected("紫卡最多指定三个不同正面词条和一个负面词条，正负词条不能重复");
        }
        WarframeMarketItem weapon = candidates.getFirst();
        WarframeMarketAuctionQuery query = new WarframeMarketAuctionQuery(
                type, weapon.localizedName("zh-hans"), weapon.slug(),
                positive, negative, page, requireNegative);
        var result = marketService.queryAuctions(query);
        if (result.isEmpty()) return CommandResult.text("暂时无法获取市场拍卖数据，请稍后再试");
        // Service 保留完整分页结果；命令出口选择交易字段，词条名称优先使用社区中文。
        Map<String, String> attributeNames = new LinkedHashMap<>();
        catalog.marketRivenTions().forEach(record -> {
            var details = record.details();
            if (details.urlName() != null && details.effect() != null && !details.effect().isBlank()) {
                attributeNames.putIfAbsent(details.urlName(), details.effect());
            }
        });
        return CommandResult.of("市场拍卖", WarframeMarketAuctionDisplay.from(result.get(),
                code -> attributeNames.getOrDefault(code, code)));
    }

    /** 只在当前拍卖目录中匹配，优先整名/slug，再使用社区黑话和唯一的包含候选。 */
    private List<WarframeMarketItem> matchWeapons(
            String input, List<WarframeMarketItem> weapons, boolean exactOnly) {
        Set<String> terms = new LinkedHashSet<>();
        terms.add(normalize(input));
        List<WarframeMarketItem> exact = matching(weapons, terms, true);
        if (!exact.isEmpty()) return exact;
        itemResolver.auctionWeaponSearchTerms(input).stream().map(MarketRivenPlugin::normalize)
                .filter(value -> !value.isBlank()).forEach(terms::add);
        exact = matching(weapons, terms, true);
        return exactOnly || !exact.isEmpty() ? exact : matching(weapons, terms, false);
    }

    private static List<WarframeMarketItem> matching(
            List<WarframeMarketItem> weapons, Set<String> terms, boolean exact) {
        return weapons.stream().filter(item -> !item.slug().isBlank())
                .filter(item -> List.of(item.slug(), item.localizedName("en"), item.localizedName("zh-hans"))
                        .stream().map(MarketRivenPlugin::normalize)
                        .anyMatch(name -> terms.stream().anyMatch(term ->
                                exact ? name.equals(term) : term.length() >= 2 && name.contains(term))))
                .toList();
    }

    /** 属性编码、完整名称和管理员维护的黑话共用一个索引，同名冲突不能取第一条。 */
    private Map<String, Set<List<String>>> attributeIndex() {
        Map<String, Set<List<String>>> index = new LinkedHashMap<>();
        catalog.marketRivenTions().forEach(record -> {
            if (Boolean.TRUE.equals(record.details().searchOnly())
                    || "has".equals(record.details().urlName()) || "none".equals(record.details().urlName())) return;
            addAttribute(index, record.details().urlName(), record.details().urlName());
            addAttribute(index, record.details().effect(), record.details().urlName());
        });
        catalog.marketRivenTionAliases().forEach(record -> {
            String code = record.details().attributeCode();
            if (code != null) Arrays.stream(code.split(",")).forEach(value -> addAttribute(index, value, value));
            addAttribute(index, record.details().aliasName(), record.details().attributeCode());
        });
        return index;
    }

    private static void addAttribute(Map<String, Set<List<String>>> index, String name, String code) {
        if (name != null && !name.isBlank() && code != null && !code.isBlank()) {
            List<String> codes = Arrays.stream(code.split(",")).map(String::trim)
                    .filter(value -> !value.isEmpty()).sorted().toList();
            if (!codes.isEmpty()) {
                index.computeIfAbsent(normalize(name), ignored -> new LinkedHashSet<>()).add(codes);
            }
        }
    }

    private static String normalize(String value) {
        return WarframeItemResolverService.normalize(value);
    }

}
