package com.nnm.bot.warframe.pojo;

import com.nnm.bot.warframe.constant.WarframeCommunityPreviewStatus;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 保存在共享 Redis 中的预览快照。
 *
 * <p>正式合并只信任此快照，不信任前端重新提交的上游 JSON。快照只是一次临时的待确认数据，
 * 不代表数据库版本，也不参与业务数据版本控制。</p>
 */
public record WarframeCommunityDatasetPreviewSnapshot(
        String previewId,
        String dataset,
        String source,
        Instant createdAt,
        Instant expiresAt,
        int recordCount,
        int addedCount,
        int changedCount,
        int conflictCount,
        List<WarframeCommunityFieldDiff> diffs,
        List<WarframeCommunityDatasetPreviewSnapshotRecord> records,
        WarframeCommunityPreviewStatus status,
        Set<String> sourceCodes,
        int chunkCount,
        int diffChunkCount,
        Map<String, List<WarframeCommunityDatasetPreviewGroup>> groupsByDataset,
        List<Integer> recordChunkSizes,
        List<WarframeCommunitySourceSummary> sources
) {
    public WarframeCommunityDatasetPreviewSnapshot {
        // 旧 Redis 预览没有来源摘要，仍可审核和合并。
        sources = sources == null ? List.of() : List.copyOf(sources);
    }

    public WarframeCommunityDatasetPreviewSnapshot(
            String previewId, String dataset, String source, Instant createdAt, Instant expiresAt,
            int recordCount, int addedCount, int changedCount, int conflictCount,
            List<WarframeCommunityFieldDiff> diffs,
            List<WarframeCommunityDatasetPreviewSnapshotRecord> records,
            WarframeCommunityPreviewStatus status) {
        this(previewId, dataset, source, createdAt, expiresAt, recordCount, addedCount,
                changedCount, conflictCount, diffs, records, status, Set.of(), 0, 0,
                Map.of(), List.of(), List.of());
    }

    public WarframeCommunityDatasetPreviewSnapshot(
            String previewId, String dataset, String source, Instant createdAt, Instant expiresAt,
            int recordCount, int addedCount, int changedCount, int conflictCount,
            List<WarframeCommunityFieldDiff> diffs,
            List<WarframeCommunityDatasetPreviewSnapshotRecord> records,
            WarframeCommunityPreviewStatus status,
            Set<String> sourceCodes) {
        this(previewId, dataset, source, createdAt, expiresAt, recordCount, addedCount,
                changedCount, conflictCount, diffs, records, status, sourceCodes, 0, 0,
                Map.of(), List.of(), List.of());
    }

    public WarframeCommunityDatasetPreviewSnapshot(
            String previewId,
            String dataset,
            String source,
            Instant createdAt,
            Instant expiresAt,
            int recordCount,
            int addedCount,
            int changedCount,
            int conflictCount,
            List<WarframeCommunityFieldDiff> diffs,
            List<WarframeCommunityDatasetPreviewSnapshotRecord> records,
            WarframeCommunityPreviewStatus status,
            Set<String> sourceCodes,
            int chunkCount,
            int diffChunkCount) {
        this(previewId, dataset, source, createdAt, expiresAt, recordCount, addedCount,
                changedCount, conflictCount, diffs, records, status, sourceCodes,
                chunkCount, diffChunkCount, Map.of(), List.of(), List.of());
    }

    public WarframeCommunityDatasetPreviewSnapshot(
            String previewId,
            String dataset,
            String source,
            Instant createdAt,
            Instant expiresAt,
            int recordCount,
            int addedCount,
            int changedCount,
            int conflictCount,
            List<WarframeCommunityFieldDiff> diffs,
            List<WarframeCommunityDatasetPreviewSnapshotRecord> records) {
        this(previewId, dataset, source, createdAt, expiresAt, recordCount, addedCount,
                changedCount, conflictCount, diffs, records,
                WarframeCommunityPreviewStatus.PENDING, Set.of(), 0, 0, Map.of(), List.of(),
                List.of());
    }
}
