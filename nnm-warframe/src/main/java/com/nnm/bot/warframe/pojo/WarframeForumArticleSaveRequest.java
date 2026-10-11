package com.nnm.bot.warframe.pojo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 论坛文章新增请求。
 */
public record WarframeForumArticleSaveRequest(
        @NotBlank
        @Size(max = 160)
        String slug,
        @NotBlank
        @Size(max = 300)
        String title,
        @Size(max = 1000)
        String summary,
        @Size(max = 1000)
        String tags,
        @Size(max = 120)
        String category,
        @Size(max = 1000)
        String coverUrl,
        @NotBlank
        String contentHtml,
        String contentMarkdown,
        @NotNull
        Boolean enabled,
        int sortOrder
) {
}
