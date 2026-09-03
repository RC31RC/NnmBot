package com.nnm.bot.warframe.dto;

import java.util.List;

/** 刷新或上传解析后的预览，不代表已写入数据库。 */
public record WarframeCommunityDatasetPreview(
        String dataset,
        String source,
        int recordCount,
        int addedCount,
        int changedCount,
        int conflictCount,
        List<WarframeCommunityFieldDiff> diffs,
        List<WarframeCommunityDatasetItem> records
) {
}
