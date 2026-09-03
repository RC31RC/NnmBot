package com.nnm.bot.warframe.dto;

/**
 * 论坛文章分页查询条件。
 */
public record WarframeForumArticleQueryRequest(
        Integer current,
        Integer size,
        String keyword,
        String category,
        Boolean enabled
) {
}
