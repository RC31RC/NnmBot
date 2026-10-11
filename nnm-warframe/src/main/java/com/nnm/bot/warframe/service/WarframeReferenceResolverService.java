package com.nnm.bot.warframe.service;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.application.WarframeReferenceResolution;
import com.nnm.bot.warframe.application.WarframeWorldStateCodebookService;
import com.nnm.bot.warframe.service.catalog.WarframeCommunityCatalogService;
import com.nnm.bot.warframe.service.catalog.WarframeOfficialCatalogService;
import com.nnm.bot.warframe.constant.WarframeCommunityRecordSource;
import com.nnm.bot.warframe.constant.WarframeCommunitySource;
import com.nnm.bot.warframe.constant.WarframeReferenceResolutionStatus;
import com.nnm.bot.warframe.constant.WarframeReferenceType;
import com.nnm.bot.warframe.pojo.WarframeCommunityDataRecord;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityNodeDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityStateTranslationDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityTranslationDetails;
import com.nnm.bot.warframe.pojo.WarframeOfficialDataRecord;
import com.nnm.bot.warframe.pojo.WarframeOfficialRegionData;
import com.nnm.bot.warframe.pojo.WarframeWorldStateCodebookEntry;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * 将 WorldState 的内部路径和代码解析为玩家可读名称。
 *
 * <p>解析优先使用人工与社区结构化数据，再使用官方导出，最后回退到稳定代码表。
 * 多个候选无法确定时保留原值，避免向玩家展示错误名称。</p>
 */
@Service
public class WarframeReferenceResolverService {

    /*
     * 节点名称是独立业务选择：官方 ExportRegions 提供稳定官方身份，
     * 社区 nodes.json 补充中文和任务字段，最后才使用 WorldState 代码簿。
     * 该顺序只属于引用解析 Service，不是全局来源优先级。
     */
    private static final List<String> NODE_SOURCE_PRIORITY = List.of(
            "SELF_MAINTAINED",
            "OFFICIAL_EXPORT_REGIONS",
            WarframeCommunitySource.KINGPRIMES_NODES.code(),
            "WARFRAME_WORLDSTATE_DATA");
    /*
     * 这些顺序只服务于引用解析。普通物品先尝试官方 Public Export，
     * 社区候选内部再按完整来源记录选择；状态翻译和节点不复用同一套顺序。
     */
    private static final List<String> TRANSLATION_SOURCE_PRIORITY = List.of(
            "SELF_MAINTAINED",
            WarframeCommunitySource.PLUS_TRANSLATION_ZH.code(),
            WarframeCommunitySource.PLUS_TRANSLATION_EN.code());
    private static final List<String> STATE_TRANSLATION_SOURCE_PRIORITY = List.of(
            "SELF_MAINTAINED",
            WarframeCommunitySource.KINGPRIMES_STATE_TRANSLATION.code());

    @Resource
    private WarframeOfficialCatalogService officialCatalogService;
    @Resource
    private WarframeCommunityCatalogService communityCatalogService;
    @Resource
    private WarframeWorldStateCodebookService worldStateCodebookService;
    @Resource
    private WarframeItemNameService itemNameService;

    public WarframeReferenceResolution resolve(String reference) {
        if (StrUtil.isBlank(reference)) {
            return unresolved(reference);
        }
        return resolveAll(Map.of(reference, WarframeReferenceType.ITEM)).get(reference);
    }

    /**
     * 同一次投影批量加载数据索引，避免每个字段分别访问数据库。
     */
    public Map<String, WarframeReferenceResolution> resolveAll(
            Map<String, WarframeReferenceType> references) {
        if (references == null || references.isEmpty()) {
            return Map.of();
        }
        Map<String, WarframeReferenceResolution> result = new LinkedHashMap<>();
        List<String> itemKeys = references.entrySet().stream()
                .filter(entry -> (entry.getValue() == null || entry.getValue() == WarframeReferenceType.ITEM)
                        && entry.getKey() != null && entry.getKey().startsWith("/"))
                .map(Map.Entry::getKey).toList();
        result.putAll(itemNameService.resolveAll(itemKeys));
        if (result.size() != references.size()) {
            ReferenceIndex index = loadIndex();
            references.forEach((reference, type) -> result.computeIfAbsent(reference,
                    key -> resolve(index, key, type == null ? WarframeReferenceType.ITEM : type)));
        }
        return Map.copyOf(result);
    }

    private ReferenceIndex loadIndex() {
        List<WarframeCommunityDataRecord<WarframeCommunityTranslationDetails>> translations =
                communityCatalogService.translations();
        List<WarframeOfficialDataRecord> official = officialCatalogService.records();
        Set<String> keys = new LinkedHashSet<>();
        translations.forEach(record -> keys.add(record.recordKey()));
        official.forEach(record -> keys.add(record.uniqueName()));
        return new ReferenceIndex(
                official,
                officialCatalogService.regions(),
                communityCatalogService.nodes(),
                communityCatalogService.stateTranslations(),
                indexTranslations(translations,
                        record -> "en".equals(record.details().locale()) ? record.details().displayName() : null),
                indexTranslations(translations,
                        record -> "zh".equals(record.details().locale()) ? record.details().displayName() : null),
                indexTranslations(translations, WarframeReferenceResolverService::translationId),
                itemNameService.resolveAll(keys));
    }

    private WarframeReferenceResolution resolve(
            ReferenceIndex index, String reference, WarframeReferenceType type) {
        if (StrUtil.isBlank(reference)) {
            return unresolved(reference);
        }
        String normalized = normalize(reference);
        return switch (type) {
            case NODE -> firstAvailable(
                    resolveOfficialNode(index, reference, normalized),
                    resolveCommunityNode(index, reference, normalized),
                    resolveBuiltInCode(reference, type),
                    unresolved(reference));
            case MISSION_TYPE, MODIFIER, FISSURE_TIER,
                    FACTION, SORTIE_BOSS, SYNDICATE, OPERATION_TYPE,
                    UPGRADE_TYPE, PERSISTENT_ENEMY -> firstAvailable(
                    resolveStateTranslation(index, reference, normalized),
                    resolveBuiltInCode(reference, type),
                    unresolved(reference));
            case ITEM -> resolveItem(index, reference, normalized);
        };
    }

    private WarframeReferenceResolution resolveItem(
            ReferenceIndex index, String reference, String normalized) {
        List<WarframeCommunityDataRecord<WarframeCommunityTranslationDetails>> translations =
                translationMatches(index, normalized);
        if (translations.size() == 1
                && translations.getFirst().recordSource() == WarframeCommunityRecordSource.SELF_MAINTAINED) {
            return translationResolution(index, reference, translations.getFirst());
        }
        WarframeReferenceResolution language = worldStateCodebookService.findLanguage(reference)
                .map(WarframeReferenceResolverService::toResolution)
                .orElse(null);
        if (language != null) {
            return language;
        }

        for (Function<WarframeOfficialDataRecord, String> extractor
                : List.<Function<WarframeOfficialDataRecord, String>>of(
                WarframeOfficialDataRecord::uniqueName,
                WarframeOfficialDataRecord::officialName,
                WarframeOfficialDataRecord::localizedName)) {
            WarframeReferenceResolution official = officialResolution(
                    index, reference, normalized, extractor);
            if (official != null) {
                return official;
            }
        }

        if (translations.size() == 1) {
            return translationResolution(index, reference, translations.getFirst());
        }
        if (translations.size() > 1) {
            return ambiguous(reference, translations.stream()
                    .map(WarframeReferenceResolverService::translationId)
                    .toList());
        }
        return firstAvailable(resolveStateTranslation(index, reference, normalized), unresolved(reference));
    }

    private static WarframeReferenceResolution translationResolution(
            ReferenceIndex index, String reference,
            WarframeCommunityDataRecord<WarframeCommunityTranslationDetails> translation) {
        WarframeReferenceResolution name = index.itemNames().get(translation.recordKey());
        return new WarframeReferenceResolution(reference, name.displayName(), translation.recordKey(),
                name.exportType(), name.status(), List.of(translation.recordKey()));
    }

    private static WarframeReferenceResolution resolveCommunityNode(
            ReferenceIndex index, String reference, String normalized) {
        List<WarframeCommunityDataRecord<WarframeCommunityNodeDetails>> matches =
                prioritizeCommunity(index.communityNodes().stream()
                .filter(record -> matchesCommunityNode(record, normalized))
                        .toList(),
                NODE_SOURCE_PRIORITY);
        if (matches.isEmpty()) {
            return null;
        }
        if (matches.size() > 1) {
            return ambiguous(reference, matches.stream()
                    .map(WarframeCommunityDataRecord::recordKey)
                    .toList());
        }
        var node = matches.getFirst();
        return new WarframeReferenceResolution(
                reference,
                nodeDisplayName(node.details().displayName(), node.details().systemName()),
                node.details().uniqueName(),
                sourceName(node, "NODE", NODE_SOURCE_PRIORITY),
                WarframeReferenceResolutionStatus.RESOLVED,
                List.of(node.recordKey()));
    }

    private static WarframeReferenceResolution resolveOfficialNode(
            ReferenceIndex index, String reference, String normalized) {
        List<WarframeOfficialRegionData> matches = index.officialRegions().stream()
                .filter(region -> matchesOfficialNode(region.record(), normalized))
                .toList();
        if (matches.isEmpty()) {
            return null;
        }
        if (matches.size() > 1) {
            return ambiguous(reference, matches.stream()
                    .map(region -> region.record().uniqueName())
                    .toList());
        }
        WarframeOfficialRegionData region = matches.getFirst();
        WarframeOfficialDataRecord record = region.record();
        String nodeName = StrUtil.blankToDefault(
                record.localizedName(),
                StrUtil.blankToDefault(record.officialName(), record.uniqueName()));
        return new WarframeReferenceResolution(
                reference,
                nodeDisplayName(nodeName, region.systemName()),
                record.uniqueName(),
                "OFFICIAL_EXPORT_REGIONS",
                WarframeReferenceResolutionStatus.RESOLVED,
                List.of(record.uniqueName()));
    }

    private static WarframeReferenceResolution resolveStateTranslation(
            ReferenceIndex index, String reference, String normalized) {
        List<WarframeCommunityDataRecord<WarframeCommunityStateTranslationDetails>> matches =
                prioritizeCommunity(index.stateTranslations().stream()
                .filter(record -> matchesStateTranslation(record, normalized))
                        .toList(),
                STATE_TRANSLATION_SOURCE_PRIORITY);
        if (matches.isEmpty()) {
            return null;
        }
        if (matches.size() > 1) {
            return ambiguous(reference, matches.stream()
                    .map(WarframeCommunityDataRecord::recordKey)
                    .toList());
        }
        var translation = matches.getFirst();
        return new WarframeReferenceResolution(
                reference,
                StrUtil.blankToDefault(
                        translation.details().displayName(),
                        translation.details().uniqueName()),
                translation.details().uniqueName(),
                sourceName(translation, "STATE_TRANSLATION", STATE_TRANSLATION_SOURCE_PRIORITY),
                WarframeReferenceResolutionStatus.RESOLVED,
                List.of(translation.recordKey()));
    }

    private WarframeReferenceResolution resolveBuiltInCode(
            String reference, WarframeReferenceType type) {
        return worldStateCodebookService.find(type, reference)
                .map(WarframeReferenceResolverService::toResolution)
                .orElse(null);
    }

    private static WarframeReferenceResolution toResolution(
            WarframeWorldStateCodebookEntry entry) {
        return new WarframeReferenceResolution(
                entry.reference(),
                entry.displayName(),
                entry.reference(),
                entry.source(),
                entry.status(),
                List.of(entry.reference()));
    }

    private static WarframeReferenceResolution officialResolution(
            ReferenceIndex index,
            String reference,
            String normalized,
            Function<WarframeOfficialDataRecord, String> extractor) {
        List<WarframeOfficialDataRecord> matches = index.officialItems().stream()
                .filter(item -> normalized.equals(normalize(extractor.apply(item))))
                .toList();
        if (matches.isEmpty()) {
            return null;
        }
        if (matches.size() > 1) {
            return ambiguous(reference, matches.stream()
                    .map(WarframeOfficialDataRecord::uniqueName)
                    .toList());
        }
        WarframeOfficialDataRecord item = matches.getFirst();
        return new WarframeReferenceResolution(
                reference,
                displayName(index, item),
                item.uniqueName(),
                item.exportType(),
                WarframeReferenceResolutionStatus.RESOLVED,
                List.of(item.uniqueName()));
    }

    private static String displayName(
            ReferenceIndex index, WarframeOfficialDataRecord item) {
        WarframeReferenceResolution name = index.itemNames().get(item.uniqueName());
        if (name != null && name.resolved()) {
            return name.displayName();
        }
        return StrUtil.blankToDefault(
                item.localizedName(),
                StrUtil.blankToDefault(item.officialName(), item.uniqueName()));
    }

    private static List<WarframeCommunityDataRecord<WarframeCommunityTranslationDetails>>
            translationMatches(ReferenceIndex index, String normalized) {
        Set<WarframeCommunityDataRecord<WarframeCommunityTranslationDetails>> result =
                new LinkedHashSet<>();
        result.addAll(index.translationsById().getOrDefault(normalized, List.of()));
        result.addAll(index.translationsByEnglishName().getOrDefault(normalized, List.of()));
        result.addAll(index.translationsByChineseName().getOrDefault(normalized, List.of()));
        Map<String, WarframeCommunityDataRecord<WarframeCommunityTranslationDetails>> byKey =
                new LinkedHashMap<>();
        result.stream().sorted(Comparator
                        .comparingInt((WarframeCommunityDataRecord<WarframeCommunityTranslationDetails> record) ->
                                sourceRank(record, TRANSLATION_SOURCE_PRIORITY))
                        .thenComparing(record -> "zh".equals(record.details().locale()) ? 0 : 1))
                .forEach(record -> byKey.putIfAbsent(record.recordKey(), record));
        return List.copyOf(byKey.values());
    }

    private static Map<String, List<WarframeCommunityDataRecord<
            WarframeCommunityTranslationDetails>>> indexTranslations(
            Collection<WarframeCommunityDataRecord<WarframeCommunityTranslationDetails>>
                    translations,
            Function<WarframeCommunityDataRecord<WarframeCommunityTranslationDetails>, String>
                    extractor) {
        Map<String, List<WarframeCommunityDataRecord<WarframeCommunityTranslationDetails>>>
                result = new LinkedHashMap<>();
        for (var translation : translations) {
            String key = normalize(extractor.apply(translation));
            if (StrUtil.isNotBlank(key)) {
                result.computeIfAbsent(key, ignored -> new ArrayList<>()).add(translation);
            }
        }
        return result;
    }

    private static <T extends WarframeCommunityDatasetDetails>
            List<WarframeCommunityDataRecord<T>> prioritizeCommunity(
                    List<WarframeCommunityDataRecord<T>> records,
                    List<String> sourcePriority) {
        if (records == null || records.isEmpty()) {
            return List.of();
        }
        int bestRank = records.stream()
                .mapToInt(record -> sourceRank(record, sourcePriority))
                .min()
                .orElse(Integer.MAX_VALUE);
        return records.stream()
                .filter(record -> sourceRank(record, sourcePriority) == bestRank)
                .sorted(Comparator.comparing(
                        WarframeCommunityDataRecord<T>::recordKey,
                        Comparator.nullsFirst(String::compareTo)))
                .toList();
    }

    private static int sourceRank(
            WarframeCommunityDataRecord<?> record,
            List<String> sourcePriority) {
        String source = record.recordSource() == WarframeCommunityRecordSource.SELF_MAINTAINED
                ? "SELF_MAINTAINED" : record.sourceCode();
        // 兼容尚未带 sourceCode 的旧调用；显式未知来源必须报错，不能偷偷排在末位。
        if (source == null) {
            return sourcePriority.size();
        }
        int rank = sourcePriority.indexOf(source);
        if (rank < 0) {
            throw new IllegalStateException("引用解析存在未登记来源：" + source);
        }
        return rank;
    }

    private static boolean matchesCommunityNode(
            WarframeCommunityDataRecord<WarframeCommunityNodeDetails> record,
            String normalized) {
        return normalized.equals(normalize(record.recordKey()))
                || normalized.equals(normalize(record.details().uniqueName()))
                || normalized.equals(normalize(record.details().displayName()));
    }

    private static boolean matchesOfficialNode(
            WarframeOfficialDataRecord record, String normalized) {
        return normalized.equals(normalize(record.uniqueName()))
                || normalized.equals(normalize(terminalName(record.uniqueName())))
                || normalized.equals(normalize(record.officialName()))
                || normalized.equals(normalize(record.localizedName()));
    }

    private static boolean matchesStateTranslation(
            WarframeCommunityDataRecord<WarframeCommunityStateTranslationDetails> record,
            String normalized) {
        return normalized.equals(normalize(record.recordKey()))
                || normalized.equals(normalize(record.details().uniqueName()))
                || normalized.equals(normalize(record.details().displayName()));
    }

    private static String sourceName(
            WarframeCommunityDataRecord<?> record,
            String dataset,
            List<String> sourcePriority) {
        String source = record.recordSource() == WarframeCommunityRecordSource.SELF_MAINTAINED
                ? "SELF_MAINTAINED" : record.sourceCode();
        if (source == null || !sourcePriority.contains(source)) {
            source = record.recordSource() == null ? "UNKNOWN" : record.recordSource().name();
        }
        return source + "_" + dataset;
    }

    private static String nodeDisplayName(String nodeName, String systemName) {
        String displayName = StrUtil.blankToDefault(nodeName, systemName);
        if (StrUtil.isBlank(systemName)
                || normalize(displayName).contains(normalize(systemName))) {
            return displayName;
        }
        return displayName + "（" + systemName + "）";
    }

    private static String translationId(
            WarframeCommunityDataRecord<WarframeCommunityTranslationDetails> record) {
        return record.recordKey();
    }

    private static WarframeReferenceResolution firstAvailable(
            WarframeReferenceResolution... resolutions) {
        for (WarframeReferenceResolution resolution : resolutions) {
            if (resolution != null) {
                return resolution;
            }
        }
        throw new IllegalStateException("引用解析必须提供兜底结果");
    }

    private static WarframeReferenceResolution ambiguous(
            String reference, List<String> candidates) {
        return new WarframeReferenceResolution(
                reference,
                reference,
                null,
                null,
                WarframeReferenceResolutionStatus.AMBIGUOUS,
                candidates);
    }

    private static WarframeReferenceResolution unresolved(String reference) {
        return new WarframeReferenceResolution(
                reference,
                reference,
                null,
                null,
                WarframeReferenceResolutionStatus.UNRESOLVED,
                List.of());
    }

    static String normalize(String value) {
        return WarframeItemResolverService.normalize(value);
    }

    private static String terminalName(String sourceKey) {
        int separator = sourceKey == null ? -1 : sourceKey.lastIndexOf('/');
        return separator >= 0 ? sourceKey.substring(separator + 1) : sourceKey;
    }

    private record ReferenceIndex(
            List<WarframeOfficialDataRecord> officialItems,
            List<WarframeOfficialRegionData> officialRegions,
            List<WarframeCommunityDataRecord<WarframeCommunityNodeDetails>> communityNodes,
            List<WarframeCommunityDataRecord<WarframeCommunityStateTranslationDetails>>
                    stateTranslations,
            Map<String, List<WarframeCommunityDataRecord<WarframeCommunityTranslationDetails>>>
                    translationsByEnglishName,
            Map<String, List<WarframeCommunityDataRecord<WarframeCommunityTranslationDetails>>>
                    translationsByChineseName,
            Map<String, List<WarframeCommunityDataRecord<WarframeCommunityTranslationDetails>>>
                    translationsById,
            Map<String, WarframeReferenceResolution> itemNames) {
    }
}
