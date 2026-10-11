package com.nnm.bot.warframe.pojo;

import com.nnm.bot.common.response.PageResult;

import java.time.Instant;
import java.util.List;

/** 管理端仅返回本页操作和对应差异，Redis 中的完整批次不受分页影响。 */
public record WarframeCommunityDatasetPreviewPage(
        String previewId,
        Instant createdAt,
        Instant expiresAt,
        String dataset,
        String source,
        int recordCount,
        int addedCount,
        int changedCount,
        int removedCount,
        PageResult<WarframeCommunityDatasetItem> operations,
        List<WarframeCommunityDatasetPreviewGroup> groups,
        List<WarframeCommunitySourceSummary> sources
) {
}
