package com.nnm.bot.warframe.pojo;

import com.nnm.bot.warframe.constant.WarframeCommunityRecordChangeType;
import com.nnm.bot.warframe.model.ArbitrationScheduleEntry;

import java.time.Instant;

public record WarframeArbitrationPreviewRecord(
        String operationId,
        Instant activation,
        ArbitrationScheduleEntry current,
        ArbitrationScheduleEntry upstream,
        boolean upstreamPresent,
        WarframeCommunityRecordChangeType changeType
) {
}
