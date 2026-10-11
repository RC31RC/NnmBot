package com.nnm.bot.warframe.pojo;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record WarframeArbitrationMergeRequest(
        @NotEmpty
        String previewId,
        List<@NotEmpty String> selectedOperationIds
) {
}
