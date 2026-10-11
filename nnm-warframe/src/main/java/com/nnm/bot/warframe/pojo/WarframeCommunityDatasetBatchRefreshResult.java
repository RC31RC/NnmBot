package com.nnm.bot.warframe.pojo;

import java.util.List;

/** 批量刷新各社区 JSON 后的轻量结果，详细冲突仍由单数据集刷新返回。 */
public record WarframeCommunityDatasetBatchRefreshResult(
        int total,
        int completed,
        List<WarframeCommunityDatasetRefreshSummary> datasets
) {
}
