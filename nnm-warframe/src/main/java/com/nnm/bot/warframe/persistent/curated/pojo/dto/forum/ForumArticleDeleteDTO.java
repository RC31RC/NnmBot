package com.nnm.bot.warframe.persistent.curated.pojo.dto.forum;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 论坛文章删除参数。
 */
public record ForumArticleDeleteDTO(
        @NotNull
        @Positive
        Long id
) {
}
