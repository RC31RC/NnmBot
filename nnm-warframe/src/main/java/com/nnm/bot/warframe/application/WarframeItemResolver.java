package com.nnm.bot.warframe.application;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.domain.entity.WarframeItem;
import com.nnm.bot.warframe.domain.entity.WarframeManualAlias;
import com.nnm.bot.warframe.domain.entity.WarframeManualItem;
import com.nnm.bot.warframe.domain.entity.WarframeOfficialExportRecord;
import com.nnm.bot.warframe.repository.WarframeManualAliasRepository;
import com.nnm.bot.warframe.repository.WarframeManualItemRepository;
import com.nnm.bot.warframe.repository.WarframeOfficialExportRecordRepository;
import jakarta.annotation.Resource;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/** 按精确名称、精确别名和高置信模糊候选依次解析 Warframe 物品。 */
@Service
public class WarframeItemResolver {

    private static final LevenshteinDistance LEVENSHTEIN = new LevenshteinDistance();
    private static final double FUZZY_AMBIGUITY_MARGIN = 0.04D;

    @Resource
    private WarframeOfficialExportRecordRepository officialRepository;
    @Resource
    private WarframeCommunityCatalog communityCatalog;
    @Resource
    private WarframeManualItemRepository manualItemRepository;
    @Resource
    private WarframeManualAliasRepository manualAliasRepository;

    public WarframeItemResolution resolve(String input) {
        String normalized = normalize(input);
        if (StrUtil.isBlank(normalized)) {
            return WarframeItemResolution.notFound();
        }

        SearchIndex index = loadIndex();
        WarframeItemResolution exactName = resolveKeys(index, index.names().get(normalized));
        if (exactName.found() || exactName.ambiguous()) {
            return exactName;
        }

        WarframeItemResolution exactAlias = resolveKeys(index, index.aliases().get(normalized));
        if (exactAlias.found() || exactAlias.ambiguous()) {
            return exactAlias;
        }

        return fuzzyResolve(index, normalized);
    }

    private SearchIndex loadIndex() {
        SearchIndex index = new SearchIndex(
                new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashMap<>());
        loadManual(index);
        loadOfficial(index);
        loadCommunity(index);
        return index;
    }

    private void loadManual(SearchIndex index) {
        Map<Long, String> keysById = new HashMap<>();
        for (WarframeManualItem source : manualItemRepository.findAllByEnabledTrue()) {
            String key = "manual:" + source.getSourceKey();
            WarframeItem item = new WarframeItem();
            item.setId(source.getId() == null ? null : -source.getId());
            item.setSourceKey(source.getSourceKey());
            item.setOfficialName(source.getOfficialName());
            item.setLocalizedName(source.getLocalizedName());
            item.setWikiSlug(source.getWikiSlug());
            item.setMarketSlug(source.getMarketSlug());
            item.setRawJson(source.getRawJson());
            item.setEnabled(true);
            item.setSourceType("MANUAL");
            index.items().put(key, item);
            if (source.getId() != null) {
                keysById.put(source.getId(), key);
            }
            add(index.names(), normalize(source.getSourceKey()), key);
            add(index.names(), normalize(source.getOfficialName()), key);
            add(index.names(), normalize(source.getLocalizedName()), key);
        }
        for (WarframeManualAlias alias : manualAliasRepository.findAllByEnabledTrue()) {
            String key = keysById.get(alias.getManualItemId());
            if (key != null) {
                add(index.aliases(), normalize(alias.getNormalizedAlias()), key);
                add(index.aliases(), normalize(alias.getAliasName()), key);
            }
        }
    }

    private void loadOfficial(SearchIndex index) {
        for (WarframeOfficialExportRecord source : officialRepository.findAllByEnabledTrue()) {
            String key = officialKey(source.getExportType(), source.getUniqueName());
            WarframeItem item = new WarframeItem();
            item.setId(source.getId());
            item.setSourceKey(source.getUniqueName());
            item.setExportType(source.getExportType());
            item.setRecordType(source.getRecordType());
            item.setOfficialName(source.getOfficialName());
            item.setLocalizedName(source.getLocalizedName());
            item.setParentName(source.getParentName());
            item.setProductCategory(source.getProductCategory());
            item.setDescription(source.getDescription());
            item.setLongDescription(source.getLongDescription());
            item.setWikiSlug(source.getWikiSlug());
            item.setMarketSlug(source.getMarketSlug());
            item.setRawJson(source.getRawJson());
            item.setLocalizedRawJson(source.getLocalizedRawJson());
            item.setEnabled(true);
            item.setSourceType("OFFICIAL_EXPORT");
            index.items().put(key, item);
            add(index.names(), normalize(source.getUniqueName()), key);
            add(index.names(), normalize(source.getOfficialName()), key);
            add(index.names(), normalize(source.getLocalizedName()), key);
            add(index.names(), normalize(terminalName(source.getUniqueName())), key);
        }
    }

    private void loadCommunity(SearchIndex index) {
        Map<String, List<String>> translationKeysByEnglishName = new HashMap<>();
        for (WarframeCommunityCatalog.Translation source : communityCatalog.translations()) {
            String key = "community:" + StrUtil.blankToDefault(
                    source.databaseId() == null ? null : source.databaseId().toString(),
                    source.id());
            WarframeItem item = new WarframeItem();
            item.setId(source.databaseId());
            item.setSourceKey(source.id());
            item.setOfficialName(source.englishName());
            item.setLocalizedName(source.localizedName());
            item.setRawJson(source.rawJson());
            item.setEnabled(true);
            item.setSourceType("COMMUNITY");
            index.items().put(key, item);
            add(translationKeysByEnglishName, normalize(source.englishName()), key);
            add(index.names(), normalize(source.id()), key);
            add(index.names(), normalize(source.englishName()), key);
            add(index.names(), normalize(source.localizedName()), key);
        }
        for (WarframeCommunityCatalog.Alias alias : communityCatalog.aliases()) {
            String normalizedTarget = normalize(alias.targetName());
            List<String> translationKeys = translationKeysByEnglishName.get(normalizedTarget);
            String key = CollUtil.size(translationKeys) == 1
                    ? translationKeys.getFirst() : null;
            if (key == null) {
                key = resolveOfficialKey(alias.targetName(), index.items());
            }
            if (key == null || !index.items().containsKey(key)) {
                continue;
            }
            add(index.aliases(), normalize(alias.aliasName()), key);
        }
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
        for (String sourceType : List.of("MANUAL", "OFFICIAL_EXPORT", "COMMUNITY")) {
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
            Map<String, List<String>> aliases
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
