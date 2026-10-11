package com.nnm.bot.warframe.pojo;

import com.nnm.bot.warframe.constant.WarframeCommunityRecordSource;
import com.nnm.bot.warframe.constant.WarframeCommunityRecordChangeType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;

/** 社区数据集中的一条记录。 */
public record WarframeCommunityDatasetItem(
        @JsonSerialize(using = ToStringSerializer.class)
        Long id,
        String operationId,
        String dataset,
        String recordKey,
        String identityKey,
        String locale,
        WarframeCommunityRecordSource recordSource,
        String sourceCode,
        LocalDateTime lastImportedAt,
        WarframeCommunityDatasetDetails details,
        String currentJson,
        String upstreamJson,
        String originalJson,
        String manualFieldsJson,
        boolean manualModified,
        boolean enabled,
        boolean upstreamPresent,
        WarframeCommunityRecordChangeType changeType,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public WarframeCommunityDatasetItem(
            Long id,
            String operationId,
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
            WarframeCommunityRecordChangeType changeType,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this(id, operationId, dataset, recordKey, recordKey, null, recordSource, null, null, details, currentJson,
                upstreamJson, originalJson, manualFieldsJson, manualModified, enabled,
                upstreamPresent, changeType, createdAt, updatedAt);
    }

    public WarframeCommunityDatasetItem(
            Long id,
            String operationId,
            String dataset,
            String recordKey,
            WarframeCommunityRecordSource recordSource,
            String sourceCode,
            LocalDateTime lastImportedAt,
            WarframeCommunityDatasetDetails details,
            String currentJson,
            String upstreamJson,
            String originalJson,
            String manualFieldsJson,
            boolean manualModified,
            boolean enabled,
            boolean upstreamPresent,
            WarframeCommunityRecordChangeType changeType,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this(id, operationId, dataset, recordKey, recordKey, null, recordSource, sourceCode,
                lastImportedAt, details, currentJson, upstreamJson, originalJson,
                manualFieldsJson, manualModified, enabled, upstreamPresent,
                changeType, createdAt, updatedAt);
    }
}
