package com.nnm.bot.warframe.application;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.constant.WarframeReferenceResolutionStatus;
import com.nnm.bot.warframe.domain.entity.WarframeManualItem;
import com.nnm.bot.warframe.domain.entity.WarframeOfficialExportRecord;
import com.nnm.bot.warframe.repository.WarframeManualItemRepository;
import com.nnm.bot.warframe.repository.WarframeOfficialExportRecordRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * 将 WorldState 的内部路径和代码解析为玩家可读名称。
 *
 * <p>该解析器只处理上游引用，不参与玩家命令输入的模糊搜索。批量解析时每类数据只查询一次，
 * 遇到多个候选会保留原值，避免把不确定结果直接展示给玩家。</p>
 */
@Service
public class WarframeReferenceResolver {

    @Resource
    private WarframeManualItemRepository manualItemRepository;
    @Resource
    private WarframeOfficialExportRecordRepository officialRepository;
    @Resource
    private WarframeCommunityCatalog communityCatalog;

    public WarframeReferenceResolution resolve(String reference) {
        if (StrUtil.isBlank(reference)) {
            return unresolved(reference);
        }
        return resolveAll(Map.of(reference, "")).get(reference);
    }

    /**
     * @param references 引用到类型提示的映射，类型提示可为 ITEM、NODE 或空字符串
     */
    public Map<String, WarframeReferenceResolution> resolveAll(
            Map<String, String> references) {
        if (references == null || references.isEmpty()) {
            return Map.of();
        }
        ReferenceIndex index = loadIndex();
        Map<String, WarframeReferenceResolution> result = new LinkedHashMap<>();
        references.forEach((reference, typeHint) ->
                result.put(reference, resolve(index, reference, typeHint)));
        return Map.copyOf(result);
    }

    private ReferenceIndex loadIndex() {
        List<WarframeCommunityCatalog.Translation> translations =
                communityCatalog.translations();
        return new ReferenceIndex(
                manualItemRepository.findAllByEnabledTrue(),
                officialRepository.findAllByEnabledTrue(),
                indexTranslations(
                        translations, WarframeCommunityCatalog.Translation::englishName),
                indexTranslations(
                        translations, WarframeCommunityCatalog.Translation::localizedName),
                indexTranslations(translations, WarframeCommunityCatalog.Translation::id));
    }

    private WarframeReferenceResolution resolve(
            ReferenceIndex index, String reference, String typeHint) {
        if (StrUtil.isBlank(reference)) {
            return unresolved(reference);
        }
        String normalized = normalize(reference);

        List<WarframeManualItem> manualMatches = index.manualItems().stream()
                .filter(item -> normalized.equals(normalize(item.getSourceKey())))
                .toList();
        WarframeReferenceResolution manual = manualResolution(reference, manualMatches);
        if (manual != null) {
            return manual;
        }

        WarframeReferenceResolution official = officialResolution(
                index, reference, typeHint, normalized,
                WarframeOfficialExportRecord::getUniqueName);
        if (official != null) {
            return official;
        }
        official = officialResolution(
                index, reference, typeHint, normalized,
                WarframeOfficialExportRecord::getOfficialName);
        if (official != null) {
            return official;
        }
        official = officialResolution(
                index, reference, typeHint, normalized,
                WarframeOfficialExportRecord::getLocalizedName);
        if (official != null) {
            return official;
        }
        official = officialResolution(
                index, reference, typeHint, normalized,
                item -> terminalName(item.getUniqueName()));
        if (official != null) {
            return official;
        }

        List<WarframeCommunityCatalog.Translation> translations =
                translationMatches(index, normalized);
        if (translations.size() == 1) {
            WarframeCommunityCatalog.Translation translation = translations.getFirst();
            return new WarframeReferenceResolution(
                    reference,
                    StrUtil.blankToDefault(
                            translation.localizedName(), translation.englishName()),
                    null,
                    "COMMUNITY_TRANSLATION",
                    WarframeReferenceResolutionStatus.RESOLVED,
                    List.of(translation.id()));
        }
        if (translations.size() > 1) {
            return ambiguous(
                    reference,
                    translations.stream()
                            .map(WarframeCommunityCatalog.Translation::id)
                            .toList());
        }
        return unresolved(reference);
    }

    private WarframeReferenceResolution officialResolution(
            ReferenceIndex index,
            String reference,
            String typeHint,
            String normalized,
            Function<WarframeOfficialExportRecord, String> extractor) {
        List<WarframeOfficialExportRecord> matches = index.officialItems().stream()
                .filter(item -> matchesTypeHint(item, typeHint))
                .filter(item -> normalized.equals(normalize(extractor.apply(item))))
                .toList();
        if (matches.isEmpty()) {
            return null;
        }
        if (matches.size() > 1) {
            return ambiguous(
                    reference,
                    matches.stream()
                            .map(WarframeOfficialExportRecord::getUniqueName)
                            .toList());
        }
        WarframeOfficialExportRecord item = matches.getFirst();
        return new WarframeReferenceResolution(
                reference,
                displayName(index, item),
                item.getUniqueName(),
                item.getExportType(),
                WarframeReferenceResolutionStatus.RESOLVED,
                List.of(item.getUniqueName()));
    }

    private static WarframeReferenceResolution manualResolution(
            String reference, List<WarframeManualItem> matches) {
        if (matches.isEmpty()) {
            return null;
        }
        if (matches.size() > 1) {
            return ambiguous(
                    reference,
                    matches.stream().map(WarframeManualItem::getSourceKey).toList());
        }
        WarframeManualItem item = matches.getFirst();
        return new WarframeReferenceResolution(
                reference,
                StrUtil.blankToDefault(item.getLocalizedName(), item.getOfficialName()),
                item.getSourceKey(),
                "MANUAL",
                WarframeReferenceResolutionStatus.RESOLVED,
                List.of(item.getSourceKey()));
    }

    private static String displayName(
            ReferenceIndex index, WarframeOfficialExportRecord item) {
        String normalizedOfficialName = normalize(item.getOfficialName());
        List<WarframeCommunityCatalog.Translation> translations =
                index.translationsByEnglishName()
                        .getOrDefault(normalizedOfficialName, List.of());
        if (translations.size() == 1
                && StrUtil.isNotBlank(translations.getFirst().localizedName())) {
            return translations.getFirst().localizedName();
        }
        return StrUtil.blankToDefault(
                item.getLocalizedName(),
                StrUtil.blankToDefault(item.getOfficialName(), item.getUniqueName()));
    }

    private static List<WarframeCommunityCatalog.Translation> translationMatches(
            ReferenceIndex index, String normalized) {
        Set<WarframeCommunityCatalog.Translation> result = new LinkedHashSet<>();
        result.addAll(index.translationsById().getOrDefault(normalized, List.of()));
        result.addAll(index.translationsByEnglishName().getOrDefault(normalized, List.of()));
        result.addAll(index.translationsByChineseName().getOrDefault(normalized, List.of()));
        return new ArrayList<>(result);
    }

    private static Map<String, List<WarframeCommunityCatalog.Translation>> indexTranslations(
            Collection<WarframeCommunityCatalog.Translation> translations,
            Function<WarframeCommunityCatalog.Translation, String> extractor) {
        Map<String, List<WarframeCommunityCatalog.Translation>> result =
                new LinkedHashMap<>();
        for (WarframeCommunityCatalog.Translation translation : translations) {
            String key = normalize(extractor.apply(translation));
            if (StrUtil.isNotBlank(key)) {
                result.computeIfAbsent(key, ignored -> new ArrayList<>()).add(translation);
            }
        }
        return result;
    }

    private static boolean matchesTypeHint(
            WarframeOfficialExportRecord item, String typeHint) {
        if (StrUtil.isBlank(typeHint) || "ITEM".equalsIgnoreCase(typeHint)) {
            return true;
        }
        if ("NODE".equalsIgnoreCase(typeHint)) {
            return contains(item.getExportType(), "region")
                    || contains(item.getExportFile(), "region")
                    || contains(item.getRecordType(), "node");
        }
        return true;
    }

    private static boolean contains(String value, String token) {
        return StrUtil.isNotBlank(value)
                && value.toLowerCase(Locale.ROOT).contains(token);
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
        return WarframeItemResolver.normalize(value);
    }

    private static String terminalName(String sourceKey) {
        int separator = sourceKey == null ? -1 : sourceKey.lastIndexOf('/');
        return separator >= 0 ? sourceKey.substring(separator + 1) : sourceKey;
    }

    private record ReferenceIndex(
            List<WarframeManualItem> manualItems,
            List<WarframeOfficialExportRecord> officialItems,
            Map<String, List<WarframeCommunityCatalog.Translation>> translationsByEnglishName,
            Map<String, List<WarframeCommunityCatalog.Translation>> translationsByChineseName,
            Map<String, List<WarframeCommunityCatalog.Translation>> translationsById) {
    }
}
