package com.nnm.bot.warframe.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/** 应用一次数据集预览，决策按记录和字段提交。 */
public record WarframeCommunityDatasetMergeRequest(
        @NotEmpty List<@Valid WarframeCommunityDatasetRecordMergeRequest> records
) {
}
