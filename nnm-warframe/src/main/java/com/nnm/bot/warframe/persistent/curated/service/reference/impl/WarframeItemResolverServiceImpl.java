package com.nnm.bot.warframe.persistent.curated.service.reference.impl;

import java.util.stream.Stream;
import java.util.stream.Collectors;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeItemResolverService;
import java.util.Map.Entry;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.constant.WarframeCacheNames;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeItemDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO.AliasDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO.TermDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeItemResolutionDTO;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeCommunityCatalogService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import com.nnm.bot.warframe.util.WarframeNameNormalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 按“已选官方/自维护名称 -> 人工别名 -> 术语跳板 -> 高置信模糊候选”解析物品。
 *
 * <p>该索引是所有需要物品入参的命令共用的能力，不在每次 wm、wiki 或紫卡查询时
 * 重复访问数据库。数据维护完成后由管理服务清理缓存，启动时也会主动预热。</p>
 */
@Service
public class WarframeItemResolverServiceImpl implements WarframeItemResolverService {

    /** 只用于候选纠错，不参与精确身份确认。 */
    private static final LevenshteinDistance LEVENSHTEIN = new LevenshteinDistance();
    /** 与最佳匹配的分差在此范围内时保留歧义。 */
    private static final double FUZZY_AMBIGUITY_MARGIN = 0.04D;

    /** 已采纳的官方静态目录。 */
    @Resource
    private WarframeOfficialRecordReadService officialCatalogService;
    /** 已采纳的人工别名与术语白名单。 */
    @Resource
    private WarframeCommunityCatalogService communityCatalogService;
    /** 有界本地索引缓存，由采纳流程触发失效。 */
    @Resource
    private CacheManager cacheManager;

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

        SearchIndex index = cachedIndex();
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
                : exactResolution(cachedIndex(), input, normalized);
    }

    /**
     * 精确名称优先于黑话，歧义不得被后续别名或模糊匹配掩盖。
     *
     * @param index 本次解析或查找使用的业务索引；按已经确认的业务身份组织。
     * @param input 待解析或校验的输入。
     * @param normalized 规范化。
     * @return 游戏资料物品解析状态对象。
     */
    private static WarframeItemResolutionDTO exactResolution(
            SearchIndex index, String input, String normalized) {
        String raw = input.trim();
        if (raw.startsWith("/")) {
            return resolveKeys(index, index.items().entrySet().stream()
                    .filter(entry -> raw.equals(entry.getValue().getSourceKey()))
                    .map(Map.Entry::getKey).toList());
        }
        WarframeItemResolutionDTO names = resolveKeys(index, index.names().get(normalized));
        return names.found() || names.ambiguous()
                ? names : resolveKeys(index, index.aliases().get(normalized));
    }

    /**
     * 结合官方名称、译名与社区别名生成市场查询候选词。
     *
     *    <p>Warframe Market 的词库不是官方导出表的简单别名表。参考项目会先把
     *    玩家黑话解析为标准英文，再把部件词作为第二个词参与商品词库匹配。
     *    这里保留原始输入，并补充黑话跳板和常见部件英文词，供 WM 目录做候选评分。</p>
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
        List<MarketName> matches = cachedIndex().marketNames().stream()
                .filter(item -> !setRequested || item.slug().endsWith("_set"))
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
        SearchIndex index = cachedIndex();
        for (String term : List.of(input, expanded)) {
            Set<String> keys = new LinkedHashSet<>(
                    index.names().getOrDefault(WarframeNameNormalizer.item(term), List.of()));
            keys.addAll(index.aliases().getOrDefault(WarframeNameNormalizer.item(term), List.of()));
            for (String key : keys) {
                WarframeItemDTO item = index.items().get(key);
                if (item != null) {
                    addSearchTerm(terms, item.getOfficialName());
                    addSearchTerm(terms, item.getLocalizedName());
                }
            }
        }
    }

    /**
     * 提前建立本实例物品解析索引，减少首个命令的加载等待。
     */
    @PostConstruct
    public void warmUp() {
        cachedIndex();
    }

    /**
     * 解析索引使用配置中的八小时缓存，避免每条命令都重新查询官方目录和人工白名单。
     *   数据管理写入后应清理该缓存；TTL 也保证外部同步遗漏清理时最终可见。
     *
     * @return 搜索索引对象。
     */
    private SearchIndex cachedIndex() {
        if (cacheManager == null) {
            return loadIndex();
        }
        Cache cache = cacheManager.getCache(WarframeCacheNames.ITEM_RESOLUTION_INDEX);
        // 同键加载由缓存协调，过期时并发消息不会各自全表扫描。
        return cache == null ? loadIndex() : cache.get("current", this::loadIndex);
    }

    /**
     * 构建单次完整索引；来源解析不读取本地参考目录或审计 JSON 字段。
     *
     * @return 搜索索引对象。
     */
    private SearchIndex loadIndex() {
        SearchIndex index = new SearchIndex(
                new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashMap<>(),
                safeAliases().stream().map(WarframeCommunityRecordDTO::details)
                        .filter(alias -> StrUtil.isNotBlank(alias.aliasName())
                                && StrUtil.isNotBlank(alias.targetName()))
                        .sorted(Comparator.comparingInt(
                                (AliasDTO alias) -> alias.aliasName().length())
                                .reversed())
                        .toList(), new ArrayList<>());
        officialCatalogService.records(WarframeOfficialDataDomain.MARKET_ITEM).forEach(record -> {
            String slug = StrUtil.blankToDefault(record.marketSlug(), record.bkey());
            List<String> names = Stream.of(record.officialName(), record.localizedName())
                    .filter(StrUtil::isNotBlank).distinct().toList();
            index.marketNames().add(new MarketName(slug, names));
        });
        loadOfficial(index);
        loadTerms(index);
        loadAliases(index);
        return index;
    }

    /**
     * 官方类型与完整路径共同组成索引身份，允许同名不同物品保留歧义。
     *
     * @param index 本次解析或查找使用的业务索引；按已经确认的业务身份组织。
     */
    private void loadOfficial(SearchIndex index) {
        for (WarframeOfficialRecordReadDTO source : officialCatalogService.records()) {
            String key = officialKey(source.domainCode(), source.bkey());
            WarframeItemDTO item = new WarframeItemDTO();
            item.setId(source.databaseId());
            item.setSourceKey(source.bkey());
            item.setDomainCode(source.domainCode());
            item.setOfficialName(source.officialName());
            item.setLocalizedName(source.localizedName());
            item.setParentName(source.parentName());
            item.setProductCategory(source.productCategory());
            item.setDescription(source.description());
            item.setLongDescription(source.longDescription());
            item.setWikiSlug(source.wikiSlug());
            item.setMarketSlug(source.marketSlug());
            item.setRawJson(source.data() == null ? null : source.data().toString());
            item.setLocalizedRawJson(source.data() == null ? null : source.data().toString());
            item.setEnabled(true);

            index.items().put(key, item);
            add(index.names(), WarframeNameNormalizer.item(source.bkey()), key);
            add(index.names(), WarframeNameNormalizer.item(source.officialName()), key);
            add(index.names(), WarframeNameNormalizer.item(source.localizedName()), key);
            add(index.names(), WarframeNameNormalizer.item(terminalName(source.bkey())), key);
        }
    }

    /**
     * 加载术语。
     *
     * @param index 本次解析或查找使用的业务索引；按已经确认的业务身份组织。
     */
    private void loadTerms(SearchIndex index) {
        // term 是参考项目中的完整黑话/补充词库。它的中文词只负责入口，
        // englishText 继续走同一套官方名称解析，避免为每个词库复制一份物品实体。
        for (WarframeCommunityRecordDTO<TermDTO> term
                : safeTerms()) {
            TermDTO details = term.details();
            String target = WarframeNameNormalizer.item(details.englishText());
            String key = uniqueKey(index.names().get(target));
            if (key == null) {
                key = resolveOfficialKey(details.englishText(), index.items());
            }
            if (key == null) {
                key = uniqueKey(index.names().get(WarframeNameNormalizer.item(details.chineseText())));
            }
            if (key == null) {
                continue;
            }
            add(index.aliases(), WarframeNameNormalizer.item(details.chineseText()), key);
            add(index.aliases(), WarframeNameNormalizer.item(details.englishText()), key);
        }
    }

    /**
     * 加载别名。
     *
     * @param index 本次解析或查找使用的业务索引；按已经确认的业务身份组织。
     */
    private void loadAliases(SearchIndex index) {
        for (AliasDTO alias : index.expansionAliases()) {
            String normalizedTarget = WarframeNameNormalizer.item(alias.targetName());
            List<String> translationKeys = index.names().getOrDefault(normalizedTarget, List.of());
            String key = CollUtil.size(translationKeys) == 1
                    ? translationKeys.getFirst() : null;
            if (key == null) {
                key = resolveOfficialKey(alias.targetName(), index.items());
            }
            if (key == null || !index.items().containsKey(key)) {
                continue;
            }
            add(index.aliases(), WarframeNameNormalizer.item(alias.aliasName()), key);

            // 参考项目的黑话表允许“黑话 -> 中文名/英文名”两种目标。
            // 这里把目标本身也作为跳板继续加入名称索引，避免只命中一半链路。
            add(index.names(), normalizedTarget, key);
        }
    }

    /**
     * 术语空目录按没有补充入口处理。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WarframeCommunityRecordDTO<TermDTO>> safeTerms() {
        List<WarframeCommunityRecordDTO<TermDTO>> terms =
                communityCatalogService.terms();
        return terms == null ? List.of() : terms;
    }

    /**
     * 多目标黑话不替换；最长的唯一别名优先，避免截断复合名称。
     *
     * @param input 待解析或校验的输入。
     * @return 替换别名对应的文本。
     */
    private String replaceAliases(String input) {
        String result = input;
        Map<String, Set<String>> targets = new LinkedHashMap<>();
        for (AliasDTO alias : cachedIndex().expansionAliases()) {
            targets.computeIfAbsent(WarframeNameNormalizer.item(alias.aliasName()), ignored -> new LinkedHashSet<>())
                    .add(WarframeNameNormalizer.item(alias.targetName()));
        }
        // 逐个替换必须保持最长别名优先，避免短别名截断复合黑话。
        for (AliasDTO alias : cachedIndex().expansionAliases()) {
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
        boolean chinese = source.codePoints().anyMatch(code -> Character.UnicodeScript.of(code)
                == Character.UnicodeScript.HAN);
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
     * 黑话只来自已采纳来源，不读取本地参考文件。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WarframeCommunityRecordDTO<AliasDTO>> safeAliases() {
        List<WarframeCommunityRecordDTO<AliasDTO>> aliases =
                communityCatalogService.aliases();
        return aliases == null ? List.of() : aliases;
    }

    /**
     * 只有唯一身份可以作为术语跳板，多目标保留未确认。
     *
     * @param keys 待处理的查找键集合。
     * @return 唯一键对应的文本。
     */
    private static String uniqueKey(List<String> keys) {
        return keys != null && keys.size() == 1 ? keys.getFirst() : null;
    }

    /**
     * 中文省略前缀属于一般搜索便利能力，精确 OCR 确认不会调用此方法。
     *
     * @param index 本次解析或查找使用的业务索引；按已经确认的业务身份组织。
     * @param normalizedInput 规范化输入。
     * @return 游戏资料物品解析状态对象。
     */
    private static WarframeItemResolutionDTO resolveLocalizedContains(
            SearchIndex index, String normalizedInput) {
        List<String> candidates = index.items().entrySet().stream()
                .filter(entry -> StrUtil.isNotBlank(entry.getValue().getLocalizedName()))
                .filter(entry -> WarframeNameNormalizer.item(entry.getValue().getLocalizedName()).contains(normalizedInput))
                .map(Map.Entry::getKey)
                .toList();
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

        double bestScore = scores.values().stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0D);
        List<String> candidateKeys = scores.entrySet().stream()
                .filter(entry -> bestScore - entry.getValue() <= FUZZY_AMBIGUITY_MARGIN)
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed()
                        .thenComparing(Map.Entry::getKey))
                .map(Map.Entry::getKey)
                .toList();
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
    private static void collectFuzzyScores(
            Map<String, List<String>> terms,
            String input,
            FuzzyThreshold threshold,
            Map<String, Double> scores) {
        for (Entry<String,List<String>> entry : terms.entrySet()) {
            String candidate = entry.getKey();
            int distance = LEVENSHTEIN.apply(input, candidate);
            int longest = Math.max(input.length(), candidate.length());
            double score = longest == 0 ? 1D : 1D - ((double) distance / longest);
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
    private static WarframeItemResolutionDTO resolveKeys(
            SearchIndex index, List<String> keys) {
        if (CollUtil.isEmpty(keys)) {
            return WarframeItemResolutionDTO.notFound();
        }
        List<WarframeItemDTO> matches = keys.stream()
                .map(index.items()::get)
                .filter(Objects::nonNull)
                .distinct()
                .sorted(itemComparator())
                .toList();
        List<WarframeItemDTO> prioritized = prioritize(matches);
        if (prioritized.size() == 1) {
            return WarframeItemResolutionDTO.resolved(prioritized.getFirst());
        }
        return CollUtil.isEmpty(prioritized)
                ? WarframeItemResolutionDTO.notFound()
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
        return Comparator.comparing(
                        (WarframeItemDTO item) -> StrUtil.blankToDefault(
                                item.getLocalizedName(), ""))
                .thenComparing(item -> StrUtil.blankToDefault(item.getOfficialName(), ""))
                .thenComparing(item -> StrUtil.blankToDefault(item.getSourceKey(), ""));
    }

    /**
     * 旧黑话目标只允许唯一的官方名称或路径身份。
     *
     * @param target 本次操作的目标对象或路径。
     * @param items 当前物品或成员列表。
     * @return 解析官方键对应的文本。
     */
    private String resolveOfficialKey(
            String target,
            Map<String, WarframeItemDTO> items) {
        String normalized = WarframeNameNormalizer.item(target);
        if (StrUtil.isBlank(normalized)) {
            return null;
        }
        String matched = null;
        for (Entry<String,WarframeItemDTO> entry : items.entrySet()) {
            WarframeItemDTO item = entry.getValue();
            if ((!normalized.equals(WarframeNameNormalizer.item(item.getSourceKey()))
                    && !normalized.equals(WarframeNameNormalizer.item(item.getOfficialName()))
                    && !normalized.equals(WarframeNameNormalizer.item(item.getLocalizedName()))
                    && !normalized.equals(WarframeNameNormalizer.item(terminalName(item.getSourceKey()))))) {
                continue;
            }
            if (matched != null && !matched.equals(entry.getKey())) {
                return null;
            }
            matched = entry.getKey();
        }
        return matched;
    }

    /**
     * 一段输入可以关联多个身份，索引不能用后写候选覆盖前者。
     *
     * @param index 本次解析或查找使用的业务索引；按已经确认的业务身份组织。
     * @param normalized 规范化。
     * @param itemKey 物品键。
     */
    private static void add(
            Map<String, List<String>> index, String normalized, String itemKey) {
        if (StrUtil.isBlank(normalized)) {
            return;
        }
        List<String> keys = index.computeIfAbsent(normalized, ignored -> new ArrayList<>());
        if (!keys.contains(itemKey)) {
            keys.add(itemKey);
        }
    }

    /**
     * 资源末段仅作为名称入口，完整路径仍保留为唯一来源身份。
     *
     * @param sourceKey 来源键。
     * @return 终止状态名称对应的文本。
     */
    private static String terminalName(String sourceKey) {
        int separator = sourceKey == null ? -1 : sourceKey.lastIndexOf('/');
        return separator >= 0 ? sourceKey.substring(separator + 1) : sourceKey;
    }

    /**
     * 不同官方导出类型即使路径相同也保留各自的索引身份。
     *
     * @param domainCode 业务领域代码。
     * @param uniqueName 唯一名称。
     * @return 官方键对应的文本。
     */
    private static String officialKey(String domainCode, String uniqueName) {
        return "official:" + StrUtil.blankToDefault(domainCode, "")
                + ":" + StrUtil.blankToDefault(uniqueName, "");
    }

    /** 名称匹配统一全角、大小写和常见分隔符；完整路径另做原文精确比较。 */

    /**
     * @param items 索引身份对应的完整物品投影
     * @param names 名称与资源路径入口，允许一名多目标
     * @param aliases 已采纳黑话和术语的目标身份
     * @param marketComponents 中文部件后缀到 Market 英文后缀
     * @param expansionAliases 按长度排序的文本展开规则
     */
    private record MarketName(String slug, List<String> names) {
    }

    private record SearchIndex(
            Map<String, WarframeItemDTO> items,
            Map<String, List<String>> names,
            Map<String, List<String>> aliases,
            List<AliasDTO> expansionAliases,
            List<MarketName> marketNames
    ) {
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
