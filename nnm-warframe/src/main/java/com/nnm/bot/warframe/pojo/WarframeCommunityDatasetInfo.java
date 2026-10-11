package com.nnm.bot.warframe.pojo;

import java.time.LocalDateTime;

/** 社区数据集菜单项及其运行时审计元信息。 */
public record WarframeCommunityDatasetInfo(
        String code,
        String fileName,
        String label,
        long recordCount,
        long enabledCount,
        String tableName,
        String sourceType,
        String sourceCode,
        LocalDateTime lastImportedAt,
        String sourceDescription,
        String description,
        String consumerCommands,
        boolean refreshable,
        boolean manageable,
        boolean readOnly
) {
}
