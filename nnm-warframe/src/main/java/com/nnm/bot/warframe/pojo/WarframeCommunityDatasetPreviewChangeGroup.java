package com.nnm.bot.warframe.pojo;

import com.nnm.bot.warframe.constant.WarframeCommunityRecordChangeType;

import java.util.List;

/** 预览中同一来源、同一变化类型下的全部操作 ID。 */
public record WarframeCommunityDatasetPreviewChangeGroup(
        WarframeCommunityRecordChangeType changeType,
        List<String> operationIds
) {
}
