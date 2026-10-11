package com.nnm.bot.warframe.repository;

import com.nnm.bot.warframe.model.entity.WarframeCommunityRivenAnalyseTrendDataset;

/**
 * 紫卡分析趋势社区数据仓储。
 *
 * <p>数据由社区紫卡分析趋势数据集同步，也允许通过统一社区数据管理能力
 * 进行人工维护；服务层使用它查询趋势原始记录，不负责命令参数解析或展示排版。</p>
 */
public interface WarframeCommunityRivenAnalyseTrendDatasetRepository
        extends WarframeCommunityDatasetRepository<WarframeCommunityRivenAnalyseTrendDataset> {
}
