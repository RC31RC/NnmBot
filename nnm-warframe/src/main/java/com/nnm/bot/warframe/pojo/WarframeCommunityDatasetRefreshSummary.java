package com.nnm.bot.warframe.pojo;

/** 单个社区 JSON 刷新结果摘要。 */
public record WarframeCommunityDatasetRefreshSummary(
        String previewId,
        String dataset,
        String label,
        int recordCount,
        int addedCount,
        int changedCount,
        int conflictCount
) {
}
