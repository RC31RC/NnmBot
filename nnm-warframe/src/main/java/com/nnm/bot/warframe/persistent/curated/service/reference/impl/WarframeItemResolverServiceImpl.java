package com.nnm.bot.warframe.persistent.curated.service.reference.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO.AliasDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeItemDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeItemResolutionDTO;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeCommunityCatalogService;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeItemResolverService;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialNameDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialNameIndexDTO;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialLookupService;
import com.nnm.bot.warframe.util.WarframeNameNormalizer;
import jakarta.annotation.Resource;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

/**
 * 按“已选官方/自维护名称 -> 人工别名 -> 术语跳板 -> 高置信模糊候选”解析物品。
 *
 * <p>
 * 名称快照按官方独立主表保存，社区名称跳板分别保存；每次查询只组合只读视图。命中缓存不读取数据库，启动不强制加载全部数据。
 * </p>
 */
@Service
public class WarframeItemResolverServiceImpl implements WarframeItemResolverService {

    /** 只用于候选纠错，不参与精确身份确认。 */
    private static final LevenshteinDistance LEVENSHTEIN = new LevenshteinDistance();
    /** 与最佳匹配的分差在此范围内时保留歧义。 */
    private static final double FUZZY_AMBIGUITY_MARGIN = 0.04D;

    /** 已采纳的官方静态目录。 */
    @Resource
    private WarframeOfficialLookupService officialLookup;
    /** 已采纳的人工别名与术语白名单。 */
    @Resource
    private WarframeCommunityCatalogService communityCatalogService;

    /**
     * 通用物品搜索允许中文简称和高置信纠错；不代表 OCR 已确认原文身份。
     *
     * @param input 待解析或校验的输入。
     * @return 游戏资料物品解析状态对象。
     */
    public WarframeItemResolutionDTO resolve(String input) {
        String normalized = WarframeNameNormalizer.item(input);
        if (StrUtil.isBlank(normalized)) {
            return WarframeItemResolutionDTO.notFound();
        }

        SearchIndex index = searchIndex();
        WarframeItemResolutionDTO exact = exactResolution(index, input, normalized);
        if (input.trim().startsWith("/") || exact.found() || exact.ambiguous()) {
            return exact;
        }

        // 中文玩家经常省略物品前缀，例如“电池”。参考项目不会把这种输入
        // 直接判定为未找到，而是从词库返回候选，让命令层决定直达或提示补全。
        WarframeItemResolutionDTO localizedContains = resolveLocalizedContains(index, normalized);
        if (localizedContains.found() || localizedContains.ambiguous()) {
            return localizedContains;
        }

        return fuzzyResolve(index, normalized);
    }

    /**
     * 仅确认完整路径、名称或已采纳别名，不用子串或模糊纠错确认武器变体。
     *
     * @param input 待解析或校验的输入。
     * @return 游戏资料物品解析状态对象。
     */
    public WarframeItemResolutionDTO resolveExact(String input) {
        String normalized = WarframeNameNormalizer.item(input);
        return StrUtil.isBlank(normalized) ? WarframeItemResolutionDTO.notFound()
            : exactResolution(searchIndex(), input, normalized);
    }

    /**
     * 精确名称优先于黑话，歧义不得被后续别名或模糊匹配掩盖。
     *
     * @param index 本次解析或查找使用的业务索引；按已经确认的业务身份组织。
     * @param input 待解析或校验的输入。
     * @param normalized 规范化。
     * @return 游戏资料物品解析状态对象。
     */
    private static WarframeItemResolutionDTO exactResolution(SearchIndex index, String input, String normalized) {
        String raw = input.trim();
        if (raw.startsWith("/")) {
            return resolveKeys(index, index.names().getOrDefault(normalized, List.of()).stream()
                .filter(key -> raw.equals(index.items().get(key).bkey())).toList());
        }
        WarframeItemResolutionDTO names = resolveKeys(index, index.names().get(normalized));
        return names.found() || names.ambiguous() ? names : resolveKeys(index, index.aliases().get(normalized));
    }

    /**
     * 结合官方名称、译名与社区别名生成市场查询候选词。
     *
     * <p>
     * Warframe Market 的词库不是官方导出表的简单别名表。参考项目会先把 玩家黑话解析为标准英文，再把部件词作为第二个词参与商品词库匹配。 这里保留原始输入，并补充黑话跳板和常见部件英文词，供 WM 目录做候选评分。
     * </p>
     *
     * @param input 待解析或校验的输入。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    public List<String> marketSearchTerms(String input) {
        String raw = StrUtil.nullToEmpty(input).trim();
        if (StrUtil.isBlank(raw)) {
            return List.of();
        }
        LinkedHashMap<String, Boolean> terms = new LinkedHashMap<>();
        addSearchTerm(terms, raw);
        addResolvedTerms(terms, raw);
        // P 是版本后缀，不是物品名的一部分；只展开词尾，保留原输入的精确命中机会。
        if (raw.matches("(?i).+[^a-z]p$")) {
            String base = raw.substring(0, raw.length() - 1).trim();
            LinkedHashMap<String, Boolean> bases = new LinkedHashMap<>();
            addResolvedTerms(bases, base);
            addSearchTerm(terms, base + " Prime");
            addSearchTerm(terms, "Primed " + base);
            bases.keySet().forEach(value -> {
                addSearchTerm(terms, value + " Prime");
                // 战甲/武器使用 “X Prime”，Prime Mod 的 WM 英文名使用 “Primed X”。
                addSearchTerm(terms, "Primed " + value);
            });
        }
        boolean setRequested = raw.matches("(?s).+(?:一套|套装)$");
        if (setRequested) {
            String base = raw.replaceFirst("(?:一套|套装)$", "").trim();
            addSearchTerm(terms, base);
            addResolvedTerms(terms, base);
        }
        Set<String> queries = terms.keySet().stream().map(WarframeItemResolverServiceImpl::marketName)
            .filter(value -> value.length() >= 2).collect(Collectors.toSet());
        List<MarketName> matches = marketNames().filter(item -> !setRequested || item.slug().endsWith("_set"))
            .filter(item -> item.names().stream().map(WarframeItemResolverServiceImpl::marketName)
                .anyMatch(name -> queries.stream().anyMatch(query -> name.startsWith(query))))
            .toList();
        if (matches.size() == 1) {
            addSearchTerm(terms, matches.getFirst().slug());
            matches.getFirst().names().forEach(name -> addSearchTerm(terms, name));
        }
        return List.copyOf(terms.keySet());
    }

    /**
     * 将已确定的物品名称转换为市场匹配使用的名称形式。
     *
     * @param value 待校验或规范化的文本。
     * @return 用于匹配市场目录的名称。
     */
    private static String marketName(String value) {
        return WarframeNameNormalizer.item(value).replace("prime", "").replaceAll("[\\s_]+", "");
    }

    /**
     * 展开唯一黑话目标并保留中英文名称，原输入由调用者另行保留。
     *
     * @param terms 术语。
     * @param input 待解析或校验的输入。
     */
    private void addResolvedTerms(Map<String, Boolean> terms, String input) {
        String expanded = replaceAliases(input.replaceAll("(?i)(?<=\\p{IsHan})p(?=\\p{IsHan})", " Prime "));
        addSearchTerm(terms, expanded);
        WarframeItemResolutionDTO resolution = resolve(expanded);
        if (resolution.found()) {
            addSearchTerm(terms, resolution.item().getOfficialName());
            addSearchTerm(terms, resolution.item().getLocalizedName());
        }
    }

    /**
     * 结合官方紫卡武器定义及别名生成拍卖武器查询候选词。
     *
     * @param input 待解析或校验的输入。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    public List<String> auctionWeaponSearchTerms(String input) {
        String raw = StrUtil.nullToEmpty(input).trim();
        LinkedHashMap<String, Boolean> terms = new LinkedHashMap<>();
        addExactAuctionTerms(terms, raw);
        String base = raw.replaceFirst("(?i)(?:\\s+prime|(?<=[^a-z])p)$", "").trim();
        if (!base.equals(raw) && StrUtil.isNotBlank(base)) {
            LinkedHashMap<String, Boolean> bases = new LinkedHashMap<>();
            addExactAuctionTerms(bases, base);
            bases.keySet().forEach(value -> addSearchTerm(terms, value + " Prime"));
        }
        return List.copyOf(terms.keySet());
    }

    /**
     * 拍卖查询仅展开精确名称与已采纳别名，不做子串或拼写纠错。
     *
     * @param terms 术语。
     * @param input 待解析或校验的输入。
     */
    private void addExactAuctionTerms(Map<String, Boolean> terms, String input) {
        addSearchTerm(terms, input);
        String expanded = replaceAliases(input.replaceAll("(?i)(?<=\\p{IsHan})p(?=\\p{IsHan})", " Prime "));
        addSearchTerm(terms, expanded);
        SearchIndex index = searchIndex();
        for (String term : List.of(input, expanded)) {
            Set<String> keys =
                new LinkedHashSet<>(index.names().getOrDefault(WarframeNameNormalizer.item(term), List.of()));
            keys.addAll(index.aliases().getOrDefault(WarframeNameNormalizer.item(term), List.of()));
            for (String key : keys) {
                WarframeOfficialNameDTO item = index.items().get(key);
                if (item != null) {
                    addSearchTerm(terms, item.officialName());
                    addSearchTerm(terms, item.localizedName());
                }
            }
        }
    }

    /** 显式预热名称索引；启动时不强制装载全部表，按需调用。 */
    @Override
    public void warmUp() {
        searchIndex();
    }

    /**
     * 组合独立快照的只读视图；不复制全量官方记录，也不缓存跨来源解析结果。
     *
     * @return 仅在本次查询有效的名称关联视图。
     */
    private SearchIndex searchIndex() {
        List<WarframeOfficialNameIndexDTO> official = Arrays.stream(WarframeOfficialDataDomain.values())
            .filter(WarframeOfficialDataDomain::itemIndex).map(officialLookup::nameIndex).toList();
        Map<String, WarframeOfficialNameDTO> items =
            view(() -> official.stream().flatMap(index -> index.items().keySet().stream()), key -> official.stream()
                .map(index -> index.items().get(key)).filter(Objects::nonNull).findFirst().orElse(null));
        Map<String, List<String>> names =
            view(() -> official.stream().flatMap(index -> index.names().keySet().stream()), key -> official.stream()
                .flatMap(index -> index.names().getOrDefault(key, List.of()).stream()).distinct().toList());
        Map<String, List<String>> aliasTargets = communityCatalogService.aliasTargets();
        Map<String, List<String>> termTargets = communityCatalogService.termTargets();
        Map<String, List<String>> aliases =
            view(() -> Stream.concat(aliasTargets.keySet().stream(), termTargets.keySet().stream()),
                key -> Stream
                    .concat(aliasTargets.getOrDefault(key, List.of()).stream(),
                        termTargets.getOrDefault(key, List.of()).stream())
                    .flatMap(target -> names.getOrDefault(WarframeNameNormalizer.item(target), List.of()).stream())
                    .distinct().toList());
        return new SearchIndex(items, names, aliases);
    }

    /**
     * 为市场独立数据集生成本次请求的名称入口。
     *
     * @return 市场轻量名称列表，不读取市场详情树。
     */
    private Stream<MarketName> marketNames() {
        return officialLookup.nameIndex(WarframeOfficialDataDomain.MARKET_ITEM).items().values().stream()
            .map(row -> new MarketName(StrUtil.blankToDefault(row.marketSlug(), row.bkey()),
                Stream.of(row.officialName(), row.localizedName()).filter(StrUtil::isNotBlank).distinct().toList()));
    }

    /**
     * 将多个独立索引呈现为只读视图，不另外存储一份合并后的数据。
     *
     * @param keys 入口键的惰性遍历器。
     * @param lookup 具体索引上的查找函数。
     * @return 只在当前查询中使用的只读视图。
     * @param <V> 索引投影类型。
     */
    private static <V> Map<String, V> view(Supplier<Stream<String>> keys, Function<String, V> lookup) {
        return new AbstractMap<>() {
            @Override
            public V get(Object key) {
                return key instanceof String text ? lookup.apply(text) : null;
            }

            @Override
            public Set<Entry<String, V>> entrySet() {
                return new AbstractSet<>() {
                    @Override
                    public Iterator<Entry<String, V>> iterator() {
                        return keys.get().distinct().map(key -> Map.entry(key, lookup.apply(key))).iterator();
                    }

                    @Override
                    public int size() {
                        return Math.toIntExact(keys.get().distinct().count());
                    }
                };
            }
        };
    }

    /**
     * 多目标黑话不替换；最长的唯一别名优先，避免截断复合名称。
     *
     * @param input 待解析或校验的输入。
     * @return 替换别名对应的文本。
     */
    private String replaceAliases(String input) {
        String result = input;
        List<AliasDTO> aliases = communityCatalogService.aliases().stream().map(WarframeCommunityRecordDTO::details)
            .filter(alias -> StrUtil.isNotBlank(alias.aliasName()) && StrUtil.isNotBlank(alias.targetName()))
            .sorted(Comparator.comparingInt((AliasDTO alias) -> alias.aliasName().length()).reversed()).toList();
        Map<String, Set<String>> targets = new LinkedHashMap<>();
        for (AliasDTO alias : aliases) {
            targets.computeIfAbsent(WarframeNameNormalizer.item(alias.aliasName()), ignored -> new LinkedHashSet<>())
                .add(WarframeNameNormalizer.item(alias.targetName()));
        }
        // 逐个替换必须保持最长别名优先，避免短别名截断复合黑话。
        for (AliasDTO alias : aliases) {
            if (targets.get(WarframeNameNormalizer.item(alias.aliasName())).size() != 1) {
                continue;
            }
            result = replaceIgnoreCase(result, alias.aliasName(), alias.targetName());
        }
        return result;
    }

    /**
     * 按完整空白词边界替换，源与目标都作字面文本处理。
     *
     * @param value 待校验或规范化的文本。
     * @param source 当前数据来源标识。
     * @param target 本次操作的目标对象或路径。
     * @return 替换忽略大小写对应的文本。
     */
    private static String replaceIgnoreCase(String value, String source, String target) {
        boolean chinese =
            source.codePoints().anyMatch(code -> Character.UnicodeScript.of(code) == Character.UnicodeScript.HAN);
        String pattern = chinese ? Pattern.quote(source) : "(?<!\\S)" + Pattern.quote(source) + "(?!\\S)";
        return value.replaceAll("(?i)" + pattern, Matcher.quoteReplacement(target));
    }

    /**
     * 保留第一次出现的搜索顺序，跳过缺失文本。
     *
     * @param terms 术语。
     * @param value 待校验或规范化的文本。
     */
    private static void addSearchTerm(Map<String, Boolean> terms, String value) {
        if (StrUtil.isNotBlank(value)) {
            terms.putIfAbsent(value.trim(), Boolean.TRUE);
        }
    }

    /**
     * 中文省略前缀属于一般搜索便利能力，精确 OCR 确认不会调用此方法。
     *
     * @param index 本次解析或查找使用的业务索引；按已经确认的业务身份组织。
     * @param normalizedInput 规范化输入。
     * @return 游戏资料物品解析状态对象。
     */
    private static WarframeItemResolutionDTO resolveLocalizedContains(SearchIndex index, String normalizedInput) {
        List<String> candidates = index.items().entrySet().stream()
            .filter(entry -> StrUtil.isNotBlank(entry.getValue().localizedName()))
            .filter(entry -> WarframeNameNormalizer.item(entry.getValue().localizedName()).contains(normalizedInput))
            .map(Map.Entry::getKey).toList();
        return resolveKeys(index, candidates);
    }

    /**
     * 返回高分附近的全部候选，不因数据库返回顺序选中某个变体。
     *
     * @param index 本次解析或查找使用的业务索引；按已经确认的业务身份组织。
     * @param input 待解析或校验的输入。
     * @return 游戏资料物品解析状态对象。
     */
    private WarframeItemResolutionDTO fuzzyResolve(SearchIndex index, String input) {
        FuzzyThreshold threshold = FuzzyThreshold.forInput(input);
        if (threshold == null) {
            return WarframeItemResolutionDTO.notFound();
        }

        Map<String, Double> scores = new LinkedHashMap<>();
        collectFuzzyScores(index.names(), input, threshold, scores);
        collectFuzzyScores(index.aliases(), input, threshold, scores);
        if (CollUtil.isEmpty(scores)) {
            return WarframeItemResolutionDTO.notFound();
        }

        double bestScore = scores.values().stream().mapToDouble(Double::doubleValue).max().orElse(0D);
        List<String> candidateKeys =
            scores.entrySet().stream().filter(entry -> bestScore - entry.getValue() <= FUZZY_AMBIGUITY_MARGIN)
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed().thenComparing(Map.Entry::getKey))
                .map(Map.Entry::getKey).toList();
        return resolveKeys(index, candidateKeys);
    }

    /**
     * 对每个身份保留最佳文本分数；分数不是统计置信度。
     *
     * @param terms 术语集合。
     * @param input 待解析或校验的输入。
     * @param threshold 阈值。
     * @param scores 评分。
     */
    private static void collectFuzzyScores(Map<String, List<String>> terms, String input, FuzzyThreshold threshold,
        Map<String, Double> scores) {
        for (Entry<String, List<String>> entry : terms.entrySet()) {
            String candidate = entry.getKey();
            int distance = LEVENSHTEIN.apply(input, candidate);
            int longest = Math.max(input.length(), candidate.length());
            double score = longest == 0 ? 1D : 1D - ((double)distance / longest);
            if (distance > threshold.maxDistance() || score < threshold.minScore()) {
                continue;
            }
            for (String key : entry.getValue()) {
                scores.merge(key, score, Math::max);
            }
        }
    }

    /**
     * 将索引身份投影为完整候选，并在来源选择后保留同名歧义。
     *
     * @param index 本次解析或查找使用的业务索引；按已经确认的业务身份组织。
     * @param keys 待处理的查找键集合。
     * @return 游戏资料物品解析状态对象。
     */
    private static WarframeItemResolutionDTO resolveKeys(SearchIndex index, List<String> keys) {
        if (CollUtil.isEmpty(keys)) {
            return WarframeItemResolutionDTO.notFound();
        }
        List<WarframeItemDTO> matches = keys.stream().map(index.items()::get).filter(Objects::nonNull).distinct()
            .map(WarframeItemResolverServiceImpl::toItem).sorted(itemComparator()).toList();
        List<WarframeItemDTO> prioritized = prioritize(matches);
        if (prioritized.size() == 1) {
            return WarframeItemResolutionDTO.resolved(prioritized.getFirst());
        }
        return CollUtil.isEmpty(prioritized) ? WarframeItemResolutionDTO.notFound()
            : WarframeItemResolutionDTO.ambiguous(prioritized);
    }

    /**
     * 维护同身份择优在官方目录读取时完成；名称碰撞不通过遗留来源等级消歧。
     *
     * @param matches 匹配集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<WarframeItemDTO> prioritize(List<WarframeItemDTO> matches) {
        return matches;
    }

    /**
     * 稳定候选顺序只用于展示，不代表来源优先级。
     *
     * @return 比较器对象。
     */
    private static Comparator<WarframeItemDTO> itemComparator() {
        return Comparator.comparing((WarframeItemDTO item) -> StrUtil.blankToDefault(item.getLocalizedName(), ""))
            .thenComparing(item -> StrUtil.blankToDefault(item.getOfficialName(), ""))
            .thenComparing(item -> StrUtil.blankToDefault(item.getSourceKey(), ""));
    }

    /**
     * 仅为命中候选构造可交付 DTO，调用方不能修改缓存中的不可变名称投影。
     *
     * @param row 轻量名称投影。
     * @return 当前查询独有的物品结果。
     */
    private static WarframeItemDTO toItem(WarframeOfficialNameDTO row) {
        WarframeItemDTO item = new WarframeItemDTO();
        item.setId(row.databaseId());
        item.setSourceKey(row.bkey());
        item.setDomainCode(row.domainCode());
        item.setOfficialName(row.officialName());
        item.setLocalizedName(row.localizedName());
        item.setParentName(row.parentName());
        item.setProductCategory(row.productCategory());
        item.setWikiSlug(row.wikiSlug());
        item.setMarketSlug(row.marketSlug());
        return item;
    }

    /** 市场名称仅在当前请求中筛选，不持有详情树。 */
    private record MarketName(String slug, List<String> names) {
    }

    /** 本次查询的来源隔离索引视图，不另外复制或缓存合并目录。 */
    private record SearchIndex(Map<String, WarframeOfficialNameDTO> items, Map<String, List<String>> names,
        Map<String, List<String>> aliases) {
    }

    /**
     * @param maxDistance 当前输入长度允许的最大编辑距离
     * @param minScore 必须同时满足的归一化相似度下限
     */
    private record FuzzyThreshold(int maxDistance, double minScore) {

        /**
         * 过短输入不纠错，长名称才允许更大的编辑距离。
         *
         * @param input 待解析或校验的输入。
         * @return 模糊匹配阈值对象。
         */
        private static FuzzyThreshold forInput(String input) {
            int length = input.codePointCount(0, input.length());
            if (length <= 2) {
                return null;
            }
            if (length <= 4) {
                return new FuzzyThreshold(1, 0.70D);
            }
            if (length <= 8) {
                return new FuzzyThreshold(2, 0.72D);
            }
            return new FuzzyThreshold(3, 0.75D);
        }
    }
}
