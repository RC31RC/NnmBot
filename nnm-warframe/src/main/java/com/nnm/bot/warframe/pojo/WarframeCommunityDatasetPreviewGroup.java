package com.nnm.bot.warframe.pojo;

import java.util.List;

/** 预览中按来源聚合的操作分组。 */
public record WarframeCommunityDatasetPreviewGroup(
        String sourceCode,
        List<WarframeCommunityDatasetPreviewChangeGroup> changeGroups
) {
}
