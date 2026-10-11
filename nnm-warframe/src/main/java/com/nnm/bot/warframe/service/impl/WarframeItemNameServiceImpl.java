package com.nnm.bot.warframe.service.impl;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.application.WarframeReferenceResolution;
import com.nnm.bot.warframe.constant.WarframeCommunityRecordSource;
import com.nnm.bot.warframe.constant.WarframeCommunitySource;
import com.nnm.bot.warframe.constant.WarframeReferenceResolutionStatus;
import com.nnm.bot.warframe.model.entity.WarframeCommunityTranslationDataset;
import com.nnm.bot.warframe.pojo.WarframeOfficialDataRecord;
import com.nnm.bot.warframe.repository.WarframeCommunityTranslationDatasetRepository;
import com.nnm.bot.warframe.service.WarframeItemNameService;
import com.nnm.bot.warframe.service.catalog.WarframeOfficialCatalogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WarframeItemNameServiceImpl implements WarframeItemNameService {
    @Resource
    private WarframeCommunityTranslationDatasetRepository translationRepository;
    @Resource
    private WarframeOfficialCatalogService officialCatalogService;

    @Override
    public Map<String, WarframeReferenceResolution> resolveAll(Collection<String> itemKeys) {
        Set<String> keys = itemKeys.stream().filter(StrUtil::isNotBlank).collect(Collectors.toSet());
        if (keys.isEmpty()) {
            return Map.of();
        }
        Map<String, WarframeCommunityTranslationDataset> translations = new LinkedHashMap<>();
        translationRepository.findAllByEnabledTrueAndRecordKeyIn(keys).stream()
                .filter(record -> rank(record) < 4 && usable(record.getDisplayName(), record.getRecordKey()))
                .sorted(Comparator.comparingInt(WarframeItemNameServiceImpl::rank)
                        .thenComparing(record -> "zh".equals(record.getLocale()) ? 0 : 1))
                .forEach(record -> translations.putIfAbsent(record.getRecordKey(), record));
        Map<String, WarframeOfficialDataRecord> official = new LinkedHashMap<>();
        officialCatalogService.records().stream()
                .filter(record -> keys.contains(record.uniqueName())
                        && usable(record.localizedName(), record.uniqueName())
                        && !record.localizedName().equals(record.officialName()))
                .sorted(Comparator.comparing(WarframeOfficialDataRecord::exportType))
                .forEach(record -> official.putIfAbsent(record.uniqueName(), record));
        Map<String, WarframeReferenceResolution> result = new LinkedHashMap<>();
        for (String key : keys) {
            var translation = translations.get(key);
            var item = official.get(key);
            String name = key;
            String source = null;
            if (translation != null && rank(translation) == 0) {
                name = translation.getDisplayName();
                source = "SELF_MAINTAINED_TRANSLATION";
            } else if (item != null) {
                name = item.localizedName();
                source = item.exportType();
            } else if (translation != null) {
                name = translation.getDisplayName();
                source = translation.getSourceCode();
            }
            result.put(key, new WarframeReferenceResolution(key, name, key, source,
                    source == null ? WarframeReferenceResolutionStatus.UNRESOLVED
                            : WarframeReferenceResolutionStatus.RESOLVED,
                    source == null ? List.of() : List.of(key)));
        }
        return Map.copyOf(result);
    }

    private static boolean usable(String name, String key) {
        return StrUtil.isNotBlank(name) && !name.equals(key) && !name.startsWith("/");
    }

    private static int rank(WarframeCommunityTranslationDataset record) {
        if (record.getRecordSource() == WarframeCommunityRecordSource.SELF_MAINTAINED) {
            return 0;
        }
        if (WarframeCommunitySource.PLUS_TRANSLATION_ZH.code().equals(record.getSourceCode())) {
            return 2;
        }
        return WarframeCommunitySource.PLUS_TRANSLATION_EN.code().equals(record.getSourceCode()) ? 3 : 4;
    }
}
