package com.nnm.bot.warframe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * 论坛文章更新请求。文章标识与字段一起放在请求体中，保持管理端接口统一使用 POST。
 */
public record WarframeForumArticleUpdateRequest(
        @NotNull @Positive Long id,
        @NotBlank @Size(max = 160) String slug,
        @NotBlank @Size(max = 300) String title,
        @Size(max = 1000) String summary,
        @Size(max = 1000) String tags,
        @Size(max = 120) String category,
        @Size(max = 1000) String coverUrl,
        @NotBlank String contentHtml,
        String contentMarkdown,
        @NotNull Boolean enabled,
        int sortOrder
) {
}
