package com.nnm.bot.warframe.pojo;

import com.nnm.bot.warframe.constant.WarframeCommunityRecordSource;

import java.time.LocalDateTime;

/**
 * 命令侧可读取的社区数据记录，保留结构化字段和完整 JSON 追溯信息。
 */
public record WarframeCommunityDataRecord<T extends WarframeCommunityDatasetDetails>(
        Long databaseId,
        String recordKey,
        WarframeCommunityRecordSource recordSource,
        String sourceCode,
        LocalDateTime lastImportedAt,
        T details,
        String rawJson,
        String upstreamJson,
        String originalJson,
        String manualFieldsJson,
        boolean manualModified
) {

    /**
     * 保留旧调用方的构造签名。来源字段加入后，未提供来源的历史记录按空来源处理。
     */
    public WarframeCommunityDataRecord(
            Long databaseId,
            String recordKey,
            WarframeCommunityRecordSource recordSource,
            T details,
            String rawJson,
            String upstreamJson,
            String originalJson,
            String manualFieldsJson,
            boolean manualModified) {
        this(databaseId, recordKey, recordSource, null, null, details, rawJson,
                upstreamJson, originalJson, manualFieldsJson, manualModified);
    }
}
