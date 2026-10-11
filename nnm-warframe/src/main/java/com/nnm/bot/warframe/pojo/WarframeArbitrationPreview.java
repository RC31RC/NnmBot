package com.nnm.bot.warframe.pojo;

import java.time.Instant;
import java.util.List;

public record WarframeArbitrationPreview(
        String previewId,
        Instant createdAt,
        Instant expiresAt,
        String source,
        int recordCount,
        int addedCount,
        int changedCount,
        int removedCount,
        List<WarframeArbitrationPreviewRecord> records
) {
    public WarframeArbitrationPreview(WarframeArbitrationPreviewSnapshot snapshot) {
        this(snapshot.previewId(), snapshot.createdAt(), snapshot.expiresAt(), snapshot.source(),
                snapshot.recordCount(), snapshot.addedCount(), snapshot.changedCount(),
                snapshot.removedCount(), snapshot.records());
    }
}
