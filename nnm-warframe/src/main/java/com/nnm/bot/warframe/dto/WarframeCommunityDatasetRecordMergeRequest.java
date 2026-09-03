package com.nnm.bot.warframe.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record WarframeCommunityDatasetRecordMergeRequest(
        @NotBlank String recordKey,
        @NotBlank String upstreamJson,
        Boolean upstreamPresent,
        List<@Valid WarframeCommunityFieldDecision> decisions
) {
}
