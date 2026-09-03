package com.nnm.bot.warframe.application;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.domain.entity.WarframeCommunityAliasDataset;
import com.nnm.bot.warframe.domain.entity.WarframeCommunityTranslationDataset;
import com.nnm.bot.warframe.repository.WarframeCommunityAliasDatasetRepository;
import com.nnm.bot.warframe.repository.WarframeCommunityTranslationDatasetRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 将社区翻译和别名的结构化字段提供给运行时物品解析。
 */
@Service
public class WarframeCommunityCatalog {

    @Resource
    private WarframeCommunityTranslationDatasetRepository translationRepository;
    @Resource
    private WarframeCommunityAliasDatasetRepository aliasRepository;

    public List<Translation> translations() {
        return translationRepository.findAllByEnabledTrue().stream()
                .filter(record -> !StrUtil.isAllBlank(
                        record.getEnglishName(), record.getChineseName()))
                .map(record -> new Translation(
                        record.getId(),
                        record.getSourceId() == null
                                ? record.getRecordKey() : record.getSourceId().toString(),
                        record.getEnglishName(),
                        record.getChineseName(),
                        record.getCurrentJson()))
                .toList();
    }

    public List<Alias> aliases() {
        return aliasRepository.findAllByEnabledTrue().stream()
                .filter(record -> StrUtil.isNotBlank(record.getAliasName())
                        && StrUtil.isNotBlank(record.getTargetName()))
                .map(record -> new Alias(
                        record.getId(),
                        record.getSourceId() == null
                                ? record.getRecordKey() : record.getSourceId().toString(),
                        record.getAliasName(),
                        record.getTargetName(),
                        record.getCurrentJson()))
                .toList();
    }

    public record Translation(
            Long databaseId,
            String id,
            String englishName,
            String localizedName,
            String rawJson) {
    }

    public record Alias(
            Long databaseId,
            String id,
            String aliasName,
            String targetName,
            String rawJson) {
    }
}
