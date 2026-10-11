package com.nnm.bot.warframe.pojo;

import com.nnm.bot.warframe.constant.WarframeCommunityPreviewStatus;

import java.time.Instant;
import java.util.List;

public record WarframeArbitrationPreviewSnapshot(
        String previewId,
        Instant createdAt,
        Instant expiresAt,
        String source,
        int recordCount,
        int addedCount,
        int changedCount,
        int removedCount,
        List<WarframeArbitrationPreviewRecord> records,
        WarframeCommunityPreviewStatus status
) {
    public WarframeArbitrationPreviewSnapshot(
            String previewId,
            Instant createdAt,
            Instant expiresAt,
            String source,
            int recordCount,
            int addedCount,
            int changedCount,
            int removedCount,
            List<WarframeArbitrationPreviewRecord> records) {
        this(previewId, createdAt, expiresAt, source, recordCount, addedCount,
                changedCount, removedCount, records, WarframeCommunityPreviewStatus.PENDING);
    }

    public WarframeArbitrationPreviewSnapshot merged() {
        return new WarframeArbitrationPreviewSnapshot(
                previewId, createdAt, expiresAt, source, recordCount, addedCount,
                changedCount, removedCount, records, WarframeCommunityPreviewStatus.MERGED);
    }
}
