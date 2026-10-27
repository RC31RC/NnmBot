package com.nnm.bot.warframe.persistent.curated.pojo.query.forum;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 论坛文章详情查询。
 */
public record ForumArticleIdQuery(
        @NotNull
        @Positive
        Long id
) {
}
