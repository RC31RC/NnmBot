package com.nnm.bot.warframe.dto;

import jakarta.validation.constraints.NotBlank;

/** 字段冲突的处理方式，默认 KEEP_MANUAL。 */
public record WarframeCommunityFieldDecision(
        @NotBlank String field,
        @NotBlank String decision
) {
}
