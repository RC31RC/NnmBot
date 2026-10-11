package com.nnm.bot.warframe.repository;

import java.util.List;
import com.nnm.bot.warframe.model.entity.WarframeCommunityAliasDataset;

/**
 * 统一物品别名社区数据仓储。
 *
 * <p>仓储同时承载社区同步别名和自维护别名；解析器通过该仓储建立
 * 用户输入到标准物品记录的候选集合。</p>
 */
public interface WarframeCommunityAliasDatasetRepository
        extends WarframeCommunityDatasetRepository<WarframeCommunityAliasDataset> {

    List<WarframeCommunityAliasDataset> findAllByEnabledTrueAndManualItemIdIsNotNull();

    List<WarframeCommunityAliasDataset> findAllByEnabledTrueAndManualItemId(Long manualItemId);

    boolean existsByManualItemIdAndNormalizedAlias(Long manualItemId, String normalizedAlias);

    boolean existsByManualItemIdAndNormalizedAliasAndIdNot(
            Long manualItemId, String normalizedAlias, Long id);
}
