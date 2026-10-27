package com.nnm.bot.warframe.plugin.runtime.market;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.util.WarframeNameNormalizer;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.WarframeRivenAttributeDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketAuctionPageDTO;
import java.util.Optional;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.CommandView;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.pojo.vo.market.WarframeMarketAuctionVO;
import com.nnm.bot.warframe.runtime.pojo.query.market.WarframeMarketAuctionQuery;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketItemDTO;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeCommunityCatalogService;
import com.nnm.bot.warframe.persistent.official.service.upgrades.WarframeRivenAttributeService;
import com.nnm.bot.warframe.runtime.service.market.WarframeMarketService;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeItemResolverService;
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
// 拍卖目录来自 Warframe Market API；属性取官方定义，组合别名取人工紫卡别名白名单。
public final class MarketRivenPlugin extends BotPlugin {

    /**
     * 将业务数据转换为命令或页面所需的展示字段。
     *
     * @return 当前命令或页面需要的展示字段。
     */
    @Override
    public List<CommandView> views() {
        return List.of(CommandView.textImageAndMarkdown("warframe.market.auctions"));
    }

    /** 使用已采纳名称、术语和别名匹配实时拍卖武器目录。 */
    @Resource
    private WarframeItemResolverService itemResolver;
    /** 负责目录获取、拍卖筛选和分页，命令不直接调用 HTTP。 */
    @Resource
    private WarframeMarketService marketService;
    /** 已采纳属性定义和组合别名，是词条编码的唯一校验目录。 */
    @Resource
    private WarframeCommunityCatalogService catalog;
    @Resource
    private WarframeRivenAttributeService officialAttributes;

    /**
     * 解析武器及词条为查询对象；拍卖请求、价格排序与每页 10 条均由 Service 负责。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation) {
        String type = "riven";
        String input = invocation.arguments() == null ? "" : invocation.arguments().trim();
        if (StrUtil.isBlank(input)) {
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
        List<WarframeMarketItemDTO> weapons = marketService.fetchAuctionWeapons(type);
        if (CollUtil.isEmpty(weapons)) {
            return CommandResult.text("暂时无法获取拍卖武器目录，请稍后再试");
        }
        // 所有精确候选优先于包含候选；Prime 属于武器前缀，不是条件。
        List<WarframeMarketItemDTO> candidates = List.of();
        int weaponEnd = end;
        for (boolean exact : List.of(true, false)) {
            for (int boundary = end; boundary > 0; boundary--) {
                candidates = matchWeapons(String.join(" ", Arrays.copyOf(parts, boundary)), weapons, exact);
                if (CollUtil.isNotEmpty(candidates)) {
                    weaponEnd = boundary;
                    break;
                }
            }
            if (CollUtil.isNotEmpty(candidates)) break;
        }
        if (CollUtil.isEmpty(candidates)) {
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
            if (StrUtil.isEmpty(remaining)) {
                if (!isNegative || index + 1 >= end || parts[index + 1].startsWith("负")) {
                    return CommandResult.rejected("负面词条不能为空");
                }
                remaining = normalize(parts[++index]);
                if (StrUtil.isEmpty(remaining)) return CommandResult.rejected("负面词条不能为空");
            }
            while (StrUtil.isNotEmpty(remaining)) {
                // 完整名称可跨空格，但不能吞掉下一个负面标记；同一记录内的多编码仍表示组合。
                StringBuilder lookahead = new StringBuilder(remaining);
                for (int next = index + 1; next < end && !parts[next].startsWith("负"); next++) {
                    lookahead.append(normalize(parts[next]));
                }
                String rest = lookahead.toString();
                String keyword = keywords.stream().filter(rest::startsWith).findFirst().orElse(null);
                if (keyword == null) {
                    return CommandResult.rejected("无法识别词条，请检查或在管理端维护别名：" + token);
                }
                Set<List<String>> mappings = attributes.get(keyword);
                if (mappings.size() > 1) {
                    return CommandResult.rejected("词条存在多个候选，请使用完整词条名称：" + token);
                }
                (isNegative ? negative : positive).addAll(mappings.iterator().next());
                int consumed = keyword.length();
                while (consumed > remaining.length()) {
                    consumed -= remaining.length();
                    remaining = normalize(parts[++index]);
                }
                // 负面作用域延续到当前词段末尾；跨词名称解析完后，下个独立词段重新判定正负。
                remaining = remaining.substring(consumed);
            }
        }
        if (new LinkedHashSet<>(positive).size() != positive.size()
                || positive.size() > 3 || negative.size() > 1
                || positive.stream().anyMatch(negative::contains)) {
            return CommandResult.rejected("紫卡最多指定三个不同正面词条和一个负面词条，正负词条不能重复");
        }
        WarframeMarketItemDTO weapon = candidates.getFirst();
        WarframeMarketAuctionQuery query = new WarframeMarketAuctionQuery(
                type, weapon.localizedName("zh-hans"), weapon.slug(),
                positive, negative, page, requireNegative);
        Optional<WarframeMarketAuctionPageDTO> result = marketService.queryAuctions(query);
        if (result.isEmpty()) return CommandResult.text("暂时无法获取市场拍卖数据，请稍后再试");
        // Service 保留完整分页结果；命令出口选择交易字段，词条名称优先使用社区中文。
        Map<String, String> attributeNames = new LinkedHashMap<>();
        officialAttributes.definitions().forEach(record -> {
            WarframeRivenAttributeDTO details = record;
            if (details.code() != null && StrUtil.isNotBlank(details.effect())) {
                attributeNames.putIfAbsent(details.code(), details.effect());
            }
        });
        return CommandResult.view("warframe.market.auctions", "市场拍卖", WarframeMarketAuctionVO.from(result.get(),
                code -> attributeNames.getOrDefault(code, code)));
    }

    /**
     * 只在当前拍卖目录中匹配，优先整名/slug，再使用社区黑话和唯一的包含候选。
     *
     * @param input 待解析或校验的输入。
     * @param weapons 武器集合。
     * @param exactOnly 精确仅限。
     * @return 市场物品的非受管详情组成的列表。
     */
    private List<WarframeMarketItemDTO> matchWeapons(
            String input, List<WarframeMarketItemDTO> weapons, boolean exactOnly) {
        Set<String> terms = new LinkedHashSet<>();
        terms.add(normalize(input));
        List<WarframeMarketItemDTO> exact = matching(weapons, terms, true);
        if (CollUtil.isNotEmpty(exact)) return exact;
        itemResolver.auctionWeaponSearchTerms(input).stream().map(MarketRivenPlugin::normalize)
                .filter(value -> StrUtil.isNotBlank(value)).forEach(terms::add);
        exact = matching(weapons, terms, true);
        return exactOnly || CollUtil.isNotEmpty(exact) ? exact : matching(weapons, terms, false);
    }

    /**
     * 精确匹配完整名称或 slug；包含匹配至少需要两个字符。
     *
     * @param weapons 武器集合。
     * @param terms 术语集合。
     * @param exact 精确。
     * @return 市场物品的非受管详情组成的列表。
     */
    private static List<WarframeMarketItemDTO> matching(
            List<WarframeMarketItemDTO> weapons, Set<String> terms, boolean exact) {
        return weapons.stream().filter(item -> StrUtil.isNotBlank(item.slug()))
                .filter(item -> List.of(item.slug(), item.localizedName("en"), item.localizedName("zh-hans"))
                        .stream().map(MarketRivenPlugin::normalize)
                        .anyMatch(name -> terms.stream().anyMatch(term ->
                                exact ? name.equals(term) : term.length() >= 2 && name.contains(term))))
                .toList();
    }

    /**
     * 属性编码、完整名称和管理员维护的黑话共用一个索引，同名冲突不能取第一条。
     *
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private Map<String, Set<List<String>>> attributeIndex() {
        Map<String, Set<List<String>>> index = new LinkedHashMap<>();
        Set<String> validCodes = new LinkedHashSet<>();
        officialAttributes.definitions().forEach(record -> {
            String code = record.code();
            if (Boolean.TRUE.equals(record.searchOnly())
                    || StrUtil.isBlank(code) || Set.of("has", "none").contains(code)) return;
            validCodes.add(code);
            addAttribute(index, code, code);
            addAttribute(index, record.effect(), code);
        });
        catalog.rivenAliases().forEach(record -> {
            String code = record.details().attributeCode();
            List<String> codes = attributeCodes(code);
            // 组合必须整条有效，不能将错误编码滤除后变成另一种查询条件。
            if (CollUtil.isEmpty(codes) || !validCodes.containsAll(codes)) return;
            addAttribute(index, record.details().aliasName(), record.details().attributeCode());
        });
        return index;
    }

    /**
     * 同名不同编码保留为歧义；组合顺序不影响条件语义。
     *
     * @param index 本次解析或查找使用的业务索引；按已经确认的业务身份组织。
     * @param name 当前业务对象的名称。
     * @param code 当前数据集或业务对象的稳定代码。
     */
    private static void addAttribute(Map<String, Set<List<String>>> index, String name, String code) {
        if (StrUtil.isNotBlank(name) && StrUtil.isNotBlank(code)) {
            List<String> codes = attributeCodes(code);
            String keyword = normalize(name);
            if (CollUtil.isNotEmpty(codes) && StrUtil.isNotEmpty(keyword)) {
                index.computeIfAbsent(keyword, ignored -> new LinkedHashSet<>()).add(codes);
            }
        }
    }

    /**
     * 只接受来源明确提供的逗号分隔编码，不从别名文本推测组合。
     *
     * @param code 当前数据集或业务对象的稳定代码。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<String> attributeCodes(String code) {
        return code == null ? List.of() : Arrays.stream(code.split("[,+]")).map(String::trim)
                .filter(value -> StrUtil.isNotEmpty(value)).sorted().toList();
    }

    /**
     * 与物品名称解析使用相同的全角、大小写和空白归一规则。
     *
     * @param value 待校验或规范化的文本。
     * @return 规范化后的文本，供比较或查找使用。
     */
    private static String normalize(String value) {
        return WarframeNameNormalizer.item(value);
    }

}
