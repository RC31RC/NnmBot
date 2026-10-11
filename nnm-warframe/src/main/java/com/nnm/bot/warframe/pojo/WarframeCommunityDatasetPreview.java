package com.nnm.bot.warframe.pojo;

import java.util.List;
import java.time.Instant;

/** 刷新或上传解析后的预览，不代表已写入数据库。 */
public record WarframeCommunityDatasetPreview(
        String previewId,
        Instant createdAt,
        Instant expiresAt,
        String dataset,
        String source,
        int recordCount,
        int addedCount,
        int changedCount,
        int conflictCount,
        List<WarframeCommunityFieldDiff> diffs,
        List<WarframeCommunityDatasetItem> records,
        List<WarframeCommunitySourceSummary> sources
) {
    public WarframeCommunityDatasetPreview {
        sources = sources == null ? List.of() : List.copyOf(sources);
    }

    public WarframeCommunityDatasetPreview(
            String previewId, Instant createdAt, Instant expiresAt, String dataset, String source,
            int recordCount, int addedCount, int changedCount, int conflictCount,
            List<WarframeCommunityFieldDiff> diffs,
            List<WarframeCommunityDatasetItem> records) {
        this(previewId, createdAt, expiresAt, dataset, source, recordCount, addedCount, changedCount,
                conflictCount, diffs, records, List.of());
    }
}
