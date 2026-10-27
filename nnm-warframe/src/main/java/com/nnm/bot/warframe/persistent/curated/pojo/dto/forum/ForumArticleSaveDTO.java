package com.nnm.bot.warframe.persistent.curated.pojo.dto.forum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;

/**
 * 论坛文章新增和修改共用字段；新增不传 id，修改必须传有效 id。
 */
public record ForumArticleSaveDTO(
        @Positive
        Long id,
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
