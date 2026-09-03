package com.nnm.bot.warframe.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.nnm.bot.warframe.domain.entity.WarframeForumArticle;

import java.time.LocalDateTime;

/**
 * 未登录文章页面使用的公开字段。
 */
public record WarframeForumArticlePublicItem(
        @JsonSerialize(using = ToStringSerializer.class)
        Long id,
        String slug,
        String title,
        String summary,
        String tags,
        String category,
        String coverUrl,
        String contentHtml,
        String pageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static WarframeForumArticlePublicItem from(WarframeForumArticle article) {
        return new WarframeForumArticlePublicItem(
                article.getId(),
                article.getSlug(),
                article.getTitle(),
                article.getSummary(),
                article.getTags(),
                article.getCategory(),
                article.getCoverUrl(),
                article.getContentHtml(),
                article.getPageUrl(),
                article.getCreatedAt(),
                article.getUpdatedAt());
    }
}
