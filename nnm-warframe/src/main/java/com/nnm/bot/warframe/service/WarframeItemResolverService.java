package com.nnm.bot.warframe.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.application.WarframeItemResolution;
import com.nnm.bot.warframe.service.catalog.WarframeCommunityCatalogService;
import com.nnm.bot.warframe.service.catalog.WarframeOfficialCatalogService;
import com.nnm.bot.warframe.cache.WarframeCacheNames;
import com.nnm.bot.warframe.model.entity.WarframeItem;
import com.nnm.bot.warframe.pojo.WarframeCommunityAliasDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityDataRecord;
import com.nnm.bot.warframe.pojo.WarframeCommunityTranslationDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityTermDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityMarketComponentDetails;
import com.nnm.bot.warframe.pojo.WarframeOfficialDataRecord;
import jakarta.annotation.Resource;
import jakarta.annotation.PostConstruct;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 按“官方名称/中文名 -> 社区别名 -> 黑话跳板 -> 高置信模糊候选”解析物品。
 *
 * <p>该索引是所有需要物品入参的命令共用的能力，不在每次 wm、wiki 或紫卡查询时
 * 重复访问数据库。数据维护完成后由管理服务清理缓存，启动时也会主动预热。</p>
 */
@Service
public class WarframeItemResolverService {

    private static final LevenshteinDistance LEVENSHTEIN = new LevenshteinDistance();
    private static final double FUZZY_AMBIGUITY_MARGIN = 0.04D;

    @Resource
    private WarframeOfficialCatalogService officialCatalogService;
    @Resource
    private WarframeCommunityCatalogService communityCatalogService;
    @Resource
    private CacheManager cacheManager;
    @Resource
    private WarframeItemNameService itemNameService;

    public WarframeItemResolution resolve(String input) {
        if (input != null && input.startsWith("/")) {
            SearchIndex index = cachedIndex();
            return resolveKeys(index, index.items().entrySet().stream()
                    .filter(entry -> input.equals(entry.getValue().getSourceKey()))
                    .map(Map.Entry::getKey).toList());
        }
        String normalized = normalize(input);
        if (StrUtil.isBlank(normalized)) {
            return WarframeItemResolution.notFound();
        }

        SearchIndex index = cachedIndex();
        WarframeItemResolution exactName = resolveKeys(index, index.names().get(normalized));
        if (exactName.found() || exactName.ambiguous()) {
            return exactName;
        }

        WarframeItemResolution exactAlias = resolveKeys(index, index.aliases().get(normalized));
        if (exactAlias.found() || exactAlias.ambiguous()) {
            return exactAlias;
        }

        // 中文玩家经常省略物品前缀，例如“电池”。参考项目不会把这种输入
        // 直接判定为未找到，而是从词库返回候选，让命令层决定直达或提示补全。
        WarframeItemResolution localizedContains = resolveLocalizedContains(index, normalized);
        if (localizedContains.found() || localizedContains.ambiguous()) {
            return localizedContains;
        }

        return fuzzyResolve(index, normalized);
    }

    /**
     * 返回用于社区商品目录搜索的标准化候选词。
     *
     * <p>Warframe Market 的词库不是官方导出表的简单别名表。参考项目会先把
     * 玩家黑话解析为标准英文，再把部件词作为第二个词参与商品词库匹配。
     * 这里保留原始输入，并补充黑话跳板和常见部件英文词，供 WM 目录做候选评分。</p>
     */
    public List<String> marketSearchTerms(String input) {
        String raw = StrUtil.nullToEmpty(input).trim();
        if (raw.isBlank()) {
            return List.of();
        }
        SearchIndex index = cachedIndex();
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
        // 最长部件后缀优先；“机体蓝图”不能再拆成“母牛机体”加“蓝图”。
        String normalizedRaw = normalize(raw);
        index.marketComponents().entrySet().stream()
                .filter(component -> normalizedRaw.endsWith(component.getKey()))
                .max(Comparator.<Map.Entry<String, String>>comparingInt(
                        component -> component.getKey().length()))
                .ifPresent(component -> {
                    String base = normalizedRaw.substring(
                            0, normalizedRaw.length() - component.getKey().length()).trim();
                    if (base.isBlank()) return;
                    LinkedHashMap<String, Boolean> bases = new LinkedHashMap<>();
                    addResolvedTerms(bases, base);
                    bases.keySet().forEach(value ->
                            addSearchTerm(terms, value + " " + component.getValue()));
                });
        return List.copyOf(terms.keySet());
    }

    private void addResolvedTerms(Map<String, Boolean> terms, String input) {
        String expanded = replaceAliases(input);
        addSearchTerm(terms, expanded);
        WarframeItemResolution resolution = resolve(expanded);
        if (resolution.found()) {
            addSearchTerm(terms, resolution.item().getOfficialName());
            addSearchTerm(terms, resolution.item().getLocalizedName());
        }
    }

    /** 拍卖命令需要先确定武器边界，不能用模糊纠错把尾部条件吞进武器名称。 */
    public List<String> auctionWeaponSearchTerms(String input) {
        String raw = StrUtil.nullToEmpty(input).trim();
        LinkedHashMap<String, Boolean> terms = new LinkedHashMap<>();
        addExactAuctionTerms(terms, raw);
        String base = raw.replaceFirst("(?i)(?:\\s+prime|(?<=[^a-z])p)$", "").trim();
        if (!base.equals(raw) && !base.isBlank()) {
            LinkedHashMap<String, Boolean> bases = new LinkedHashMap<>();
            addExactAuctionTerms(bases, base);
            bases.keySet().forEach(value -> addSearchTerm(terms, value + " Prime"));
        }
        return List.copyOf(terms.keySet());
    }

    private void addExactAuctionTerms(Map<String, Boolean> terms, String input) {
        addSearchTerm(terms, input);
        String expanded = replaceAliases(input);
        addSearchTerm(terms, expanded);
        SearchIndex index = cachedIndex();
        for (String term : List.of(input, expanded)) {
            Set<String> keys = new LinkedHashSet<>(
                    index.names().getOrDefault(normalize(term), List.of()));
            keys.addAll(index.aliases().getOrDefault(normalize(term), List.of()));
            for (String key : keys) {
                WarframeItem item = index.items().get(key);
                if (item != null) {
                    addSearchTerm(terms, item.getOfficialName());
                    addSearchTerm(terms, item.getLocalizedName());
                }
            }
        }
    }

    @PostConstruct
    public void warmUp() {
        cachedIndex();
    }

    /**
     * 解析索引使用配置中的八小时缓存，避免每条命令都重新查询全部官方和社区数据。
     * 数据管理写入后应清理该缓存；TTL 也保证外部同步遗漏清理时最终可见。
     */
    private SearchIndex cachedIndex() {
        if (cacheManager == null) {
            return loadIndex();
        }
        Cache cache = cacheManager.getCache(WarframeCacheNames.ITEM_RESOLUTION_INDEX);
        // 同键加载由缓存协调，过期时并发消息不会各自全表扫描。
        return cache == null ? loadIndex() : cache.get("current", this::loadIndex);
    }

    private SearchIndex loadIndex() {
        SearchIndex index = new SearchIndex(
                new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashMap<>(),
                new LinkedHashMap<>(),
                safeAliases().stream().map(WarframeCommunityDataRecord::details)
                        .filter(alias -> StrUtil.isNotBlank(alias.aliasName())
                                && StrUtil.isNotBlank(alias.targetName()))
                        .sorted(Comparator.comparingInt(
                                (WarframeCommunityAliasDetails alias) -> alias.aliasName().length())
                                .reversed())
                        .toList());
        loadOfficial(index);
        loadCommunity(index);
        loadMarketComponents(index);
        return index;
    }

    private void loadOfficial(SearchIndex index) {
        for (WarframeOfficialDataRecord source : officialCatalogService.records()) {
            String key = officialKey(source.exportType(), source.uniqueName());
            WarframeItem item = new WarframeItem();
            item.setId(source.databaseId());
            item.setSourceKey(source.uniqueName());
            item.setExportType(source.exportType());
            item.setRecordType(source.recordType());
            item.setOfficialName(source.officialName());
            item.setLocalizedName(source.localizedName());
            item.setParentName(source.parentName());
            item.setProductCategory(source.productCategory());
            item.setDescription(source.description());
            item.setLongDescription(source.longDescription());
            item.setWikiSlug(source.wikiSlug());
            item.setMarketSlug(source.marketSlug());
            item.setRawJson(source.rawJson());
            item.setLocalizedRawJson(source.localizedRawJson());
            item.setEnabled(true);
            item.setSourceType("OFFICIAL_EXPORT");
            index.items().put(key, item);
            add(index.names(), normalize(source.uniqueName()), key);
            add(index.names(), normalize(source.officialName()), key);
            add(index.names(), normalize(source.localizedName()), key);
            add(index.names(), normalize(terminalName(source.uniqueName())), key);
        }
    }

    private void loadCommunity(SearchIndex index) {
        Map<String, List<String>> translationKeysByEnglishName = new HashMap<>();
        Map<String, List<WarframeCommunityDataRecord<WarframeCommunityTranslationDetails>>> byKey =
                new LinkedHashMap<>();
        for (WarframeCommunityDataRecord<WarframeCommunityTranslationDetails> source
                : communityCatalogService.translations()) {
            byKey.computeIfAbsent(source.recordKey(), ignored -> new ArrayList<>()).add(source);
        }
        var names = itemNameService.resolveAll(byKey.keySet());
        for (var entry : byKey.entrySet()) {
            String recordKey = entry.getKey();
            var records = entry.getValue();
            List<String> itemKeys = index.items().entrySet().stream()
                    .filter(item -> recordKey.equals(item.getValue().getSourceKey()))
                    .map(Map.Entry::getKey).toList();
            if (itemKeys.isEmpty()) {
                var source = records.getFirst();
                String key = "community:" + recordKey;
                WarframeItem item = new WarframeItem();
                item.setId(source.databaseId());
                item.setSourceKey(recordKey);
                item.setOfficialName(records.stream()
                        .filter(record -> "en".equals(record.details().locale()))
                        .map(record -> record.details().displayName()).findFirst().orElse(recordKey));
                item.setRawJson(source.rawJson());
                item.setEnabled(true);
                item.setSourceType(source.recordSource().name());
                index.items().put(key, item);
                itemKeys = List.of(key);
            }
            for (String key : itemKeys) {
                WarframeItem item = index.items().get(key);
                var name = names.get(recordKey);
                if (name != null && name.resolved()) {
                    item.setLocalizedName(name.displayName());
                }
                add(index.names(), normalize(recordKey), key);
                for (var record : records) {
                    String displayName = record.details().displayName();
                    add(index.names(), normalize(displayName), key);
                    if ("en".equals(record.details().locale())) {
                        add(translationKeysByEnglishName, normalize(displayName), key);
                    }
                }
            }
        }
        // term 是参考项目中的完整黑话/补充词库。它的中文词只负责入口，
        // englishText 继续走同一套官方名称解析，避免为每个词库复制一份物品实体。
        for (WarframeCommunityDataRecord<WarframeCommunityTermDetails> term
                : safeTerms()) {
            WarframeCommunityTermDetails details = term.details();
            String target = normalize(details.englishText());
            String key = uniqueKey(index.names().get(target));
            if (key == null) {
                key = resolveOfficialKey(details.englishText(), index.items());
            }
            if (key == null) {
                key = uniqueKey(index.names().get(normalize(details.chineseText())));
            }
            if (key == null) {
                continue;
            }
            add(index.aliases(), normalize(details.chineseText()), key);
            add(index.aliases(), normalize(details.englishText()), key);
        }
        for (WarframeCommunityAliasDetails alias : index.expansionAliases()) {
            String normalizedTarget = normalize(alias.targetName());
            List<String> translationKeys = new ArrayList<>(
                    translationKeysByEnglishName.getOrDefault(normalizedTarget, List.of()));
            if (translationKeys.isEmpty()) {
                translationKeys.addAll(index.names().getOrDefault(normalizedTarget, List.of()));
            }
            String key = CollUtil.size(translationKeys) == 1
                    ? translationKeys.getFirst() : null;
            if (key == null) {
                key = resolveOfficialKey(alias.targetName(), index.items());
            }
            if (key == null || !index.items().containsKey(key)) {
                continue;
            }
            add(index.aliases(), normalize(alias.aliasName()), key);

            // 参考项目的黑话表允许“黑话 -> 中文名/英文名”两种目标。
            // 这里把目标本身也作为跳板继续加入名称索引，避免只命中一半链路。
            add(index.names(), normalizedTarget, key);
        }
    }

    private void loadMarketComponents(SearchIndex index) {
        for (WarframeCommunityDataRecord<WarframeCommunityMarketComponentDetails> source
                : safeMarketComponents()) {
            WarframeCommunityMarketComponentDetails details = source.details();
            String alias = normalize(details.inputAlias());
            if (StrUtil.isBlank(alias) || StrUtil.isBlank(details.marketSuffix())) {
                continue;
            }
            index.marketComponents().putIfAbsent(alias, details.marketSuffix().trim());
        }
    }

    private List<WarframeCommunityDataRecord<WarframeCommunityMarketComponentDetails>>
            safeMarketComponents() {
        List<WarframeCommunityDataRecord<WarframeCommunityMarketComponentDetails>> components =
                communityCatalogService.marketComponents();
        return components == null ? List.of() : components;
    }

    private List<WarframeCommunityDataRecord<WarframeCommunityTermDetails>> safeTerms() {
        List<WarframeCommunityDataRecord<WarframeCommunityTermDetails>> terms =
                communityCatalogService.terms();
        return terms == null ? List.of() : terms;
    }

    private String replaceAliases(String input) {
        String result = input;
        // 逐个替换必须保持最长别名优先，避免短别名截断复合黑话。
        for (WarframeCommunityAliasDetails alias : cachedIndex().expansionAliases()) {
            result = replaceIgnoreCase(result, alias.aliasName(), alias.targetName());
        }
        return result;
    }

    private static String replaceIgnoreCase(String value, String source, String target) {
        return value.replaceAll("(?i)(?<!\\S)" + Pattern.quote(source)
                        + "(?!\\S)", Matcher.quoteReplacement(target));
    }

    private static void addSearchTerm(Map<String, Boolean> terms, String value) {
        if (StrUtil.isNotBlank(value)) {
            terms.putIfAbsent(value.trim(), Boolean.TRUE);
        }
    }

    private List<WarframeCommunityDataRecord<WarframeCommunityAliasDetails>> safeAliases() {
        List<WarframeCommunityDataRecord<WarframeCommunityAliasDetails>> aliases =
                communityCatalogService.aliases();
        return aliases == null ? List.of() : aliases;
    }

    private static String uniqueKey(List<String> keys) {
        return keys != null && keys.size() == 1 ? keys.getFirst() : null;
    }

    private static WarframeItemResolution resolveLocalizedContains(
            SearchIndex index, String normalizedInput) {
        List<String> candidates = index.items().entrySet().stream()
                .filter(entry -> "OFFICIAL_EXPORT".equals(entry.getValue().getSourceType()))
                .filter(entry -> StrUtil.isNotBlank(entry.getValue().getLocalizedName()))
                .filter(entry -> normalize(entry.getValue().getLocalizedName()).contains(normalizedInput))
                .map(Map.Entry::getKey)
                .toList();
        return resolveKeys(index, candidates);
    }

    private WarframeItemResolution fuzzyResolve(SearchIndex index, String input) {
        FuzzyThreshold threshold = FuzzyThreshold.forInput(input);
        if (threshold == null) {
            return WarframeItemResolution.notFound();
        }

        Map<String, Double> scores = new LinkedHashMap<>();
        collectFuzzyScores(index.names(), input, threshold, scores);
        collectFuzzyScores(index.aliases(), input, threshold, scores);
        if (scores.isEmpty()) {
            return WarframeItemResolution.notFound();
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

    private static void collectFuzzyScores(
            Map<String, List<String>> terms,
            String input,
            FuzzyThreshold threshold,
            Map<String, Double> scores) {
        for (Map.Entry<String, List<String>> entry : terms.entrySet()) {
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

    private static WarframeItemResolution resolveKeys(
            SearchIndex index, List<String> keys) {
        if (CollUtil.isEmpty(keys)) {
            return WarframeItemResolution.notFound();
        }
        List<WarframeItem> matches = keys.stream()
                .map(index.items()::get)
                .filter(Objects::nonNull)
                .distinct()
                .sorted(itemComparator())
                .toList();
        List<WarframeItem> prioritized = prioritize(matches);
        if (prioritized.size() == 1) {
            return WarframeItemResolution.resolved(prioritized.getFirst());
        }
        return CollUtil.isEmpty(prioritized)
                ? WarframeItemResolution.notFound()
                : WarframeItemResolution.ambiguous(prioritized);
    }

    private static List<WarframeItem> prioritize(List<WarframeItem> matches) {
        for (String sourceType : List.of(
                "SELF_MAINTAINED", "OFFICIAL_EXPORT", "COMMUNITY")) {
            List<WarframeItem> preferred = matches.stream()
                    .filter(item -> sourceType.equals(item.getSourceType()))
                    .toList();
            if (!preferred.isEmpty()) {
                return preferred;
            }
        }
        return matches;
    }

    private static Comparator<WarframeItem> itemComparator() {
        return Comparator.comparing(
                        (WarframeItem item) -> StrUtil.blankToDefault(
                                item.getLocalizedName(), ""))
                .thenComparing(item -> StrUtil.blankToDefault(item.getOfficialName(), ""))
                .thenComparing(item -> StrUtil.blankToDefault(item.getSourceKey(), ""));
    }

    private String resolveOfficialKey(
            String target,
            Map<String, WarframeItem> items) {
        String normalized = normalize(target);
        if (StrUtil.isBlank(normalized)) {
            return null;
        }
        String matched = null;
        for (Map.Entry<String, WarframeItem> entry : items.entrySet()) {
            WarframeItem item = entry.getValue();
            if (!"OFFICIAL_EXPORT".equals(item.getSourceType())
                    || (!normalized.equals(normalize(item.getSourceKey()))
                    && !normalized.equals(normalize(item.getOfficialName()))
                    && !normalized.equals(normalize(item.getLocalizedName()))
                    && !normalized.equals(normalize(terminalName(item.getSourceKey()))))) {
                continue;
            }
            if (matched != null && !matched.equals(entry.getKey())) {
                return null;
            }
            matched = entry.getKey();
        }
        return matched;
    }

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

    private static String terminalName(String sourceKey) {
        int separator = sourceKey == null ? -1 : sourceKey.lastIndexOf('/');
        return separator >= 0 ? sourceKey.substring(separator + 1) : sourceKey;
    }

    private static String officialKey(String exportType, String uniqueName) {
        return "official:" + StrUtil.blankToDefault(exportType, "")
                + ":" + StrUtil.blankToDefault(uniqueName, "");
    }

    public static String normalize(String input) {
        if (StrUtil.isBlank(input)) {
            return "";
        }
        String normalized = Normalizer.normalize(input.trim(), Normalizer.Form.NFKC)
                .toLowerCase(Locale.ROOT);
        return normalized.replaceAll("[\\s_\\-·•/\\\\]+", "");
    }

    private record SearchIndex(
            Map<String, WarframeItem> items,
            Map<String, List<String>> names,
            Map<String, List<String>> aliases,
            Map<String, String> marketComponents,
            List<WarframeCommunityAliasDetails> expansionAliases
    ) {
    }

    private record FuzzyThreshold(int maxDistance, double minScore) {

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
