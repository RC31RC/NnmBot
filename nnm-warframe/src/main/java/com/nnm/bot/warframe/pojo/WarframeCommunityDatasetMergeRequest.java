package com.nnm.bot.warframe.pojo;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

/** 应用一次数据集预览，只提交服务端预览中已勾选的操作 ID。 */
public record WarframeCommunityDatasetMergeRequest(
        @NotBlank
        String previewId,
        Set<@NotBlank String> selectedOperationIds
) {
}
