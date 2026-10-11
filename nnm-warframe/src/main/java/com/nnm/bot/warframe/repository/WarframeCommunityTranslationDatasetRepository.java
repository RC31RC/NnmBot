package com.nnm.bot.warframe.repository;

import com.nnm.bot.warframe.model.entity.WarframeCommunityTranslationDataset;
import java.util.Collection;
import java.util.List;

/**
 * 社区物品及业务文本翻译仓储，主语言为简体中文，英文作为兜底。
 */
public interface WarframeCommunityTranslationDatasetRepository
        extends WarframeCommunityDatasetRepository<WarframeCommunityTranslationDataset> {
    List<WarframeCommunityTranslationDataset> findAllByEnabledTrueAndRecordKeyIn(Collection<String> keys);
}
