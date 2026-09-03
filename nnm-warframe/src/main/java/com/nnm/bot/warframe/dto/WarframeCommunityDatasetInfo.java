package com.nnm.bot.warframe.dto;

/** 社区数据集菜单项。 */
public record WarframeCommunityDatasetInfo(
        String code,
        String fileName,
        String label,
        long recordCount,
        long enabledCount
) {
}
