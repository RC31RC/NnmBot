package com.nnm.bot.warframe.dto;

import com.nnm.bot.warframe.constant.WarframeCommunityRecordSource;

import java.time.LocalDateTime;

/** 社区数据集中的一条记录。 */
public record WarframeCommunityDatasetItem(
        Long id,
        String dataset,
        String recordKey,
        WarframeCommunityRecordSource recordSource,
        WarframeCommunityDatasetDetails details,
        String currentJson,
        String upstreamJson,
        String originalJson,
        String manualFieldsJson,
        boolean manualModified,
        boolean enabled,
        boolean upstreamPresent,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
