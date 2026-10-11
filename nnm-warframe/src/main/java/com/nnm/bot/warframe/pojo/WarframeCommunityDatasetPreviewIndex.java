package com.nnm.bot.warframe.pojo;

import java.util.List;
import java.util.Map;

/** 共享预览的完整分组及操作在记录分片中的位置，不向前端暴露存储索引。 */
public record WarframeCommunityDatasetPreviewIndex(
        Map<String, List<WarframeCommunityDatasetPreviewGroup>> groupsByDataset,
        Map<String, Integer> recordPositions
) {
    public WarframeCommunityDatasetPreviewIndex {
        groupsByDataset = groupsByDataset == null ? Map.of() : Map.copyOf(groupsByDataset);
        recordPositions = recordPositions == null ? Map.of() : Map.copyOf(recordPositions);
    }
}
