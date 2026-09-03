package com.nnm.bot.warframe.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 论坛文章详情和删除请求。
 */
public record WarframeForumArticleIdRequest(
        @NotNull @Positive Long id
) {
}
