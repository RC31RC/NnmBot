package com.nnm.bot.warframe.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nnm.bot.warframe.constant.WarframeCommunityRecordSource;
import com.nnm.bot.warframe.constant.WarframeCommunityRecordChangeType;

import java.time.LocalDateTime;

/**
 * Redis 预览记录，不包含多态详情。currentJson 为 null 表示与 upstreamJson 相同，
 * manualFieldsJson 为 null 表示默认空对象；读取分片时恢复完整值。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record WarframeCommunityDatasetPreviewSnapshotRecord(
        Long id,
        String operationId,
        String targetDataset,
        String recordKey,
        String identityKey,
        String locale,
        String upstreamJson,
        boolean upstreamPresent,
        String currentJson,
        String originalJson,
        String manualFieldsJson,
        boolean manualModified,
        boolean enabled,
        WarframeCommunityRecordSource recordSource,
        String sourceCode,
        LocalDateTime lastImportedAt,
        WarframeCommunityRecordChangeType changeType,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
