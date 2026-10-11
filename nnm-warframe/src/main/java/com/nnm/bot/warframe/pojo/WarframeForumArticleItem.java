package com.nnm.bot.warframe.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.nnm.bot.warframe.model.entity.WarframeForumArticle;
import com.nnm.bot.warframe.application.WarframeForumOperator;

import java.time.LocalDateTime;

/**
 * 论坛文章管理端返回对象。
 */
public record WarframeForumArticleItem(
        @JsonSerialize(using = ToStringSerializer.class)
        Long id,
        String slug,
        String title,
        String summary,
        String tags,
        String category,
        String coverUrl,
        String contentHtml,
        String contentMarkdown,
        String pageUrl,
        boolean enabled,
        int sortOrder,
        Long createdByUserId,
        boolean editable,
        boolean deletable,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static WarframeForumArticleItem from(
            WarframeForumArticle article,
            WarframeForumOperator operator,
            int ownerAuthorityLevel) {
        long ownerUserId = article.getCreatedByUserId();
        return new WarframeForumArticleItem(
                article.getId(),
                article.getSlug(),
                article.getTitle(),
                article.getSummary(),
                article.getTags(),
                article.getCategory(),
                article.getCoverUrl(),
                article.getContentHtml(),
                article.getContentMarkdown(),
                article.getPageUrl(),
                article.isEnabled(),
                article.getSortOrder(),
                article.getCreatedByUserId(),
                operator != null && operator.canUpdate(ownerUserId, ownerAuthorityLevel),
                operator != null && operator.canDelete(ownerUserId, ownerAuthorityLevel),
                article.getCreatedAt(),
                article.getUpdatedAt()
        );
    }

    public static WarframeForumArticleItem from(WarframeForumArticle article) {
        return new WarframeForumArticleItem(
                article.getId(),
                article.getSlug(),
                article.getTitle(),
                article.getSummary(),
                article.getTags(),
                article.getCategory(),
                article.getCoverUrl(),
                article.getContentHtml(),
                article.getContentMarkdown(),
                article.getPageUrl(),
                article.isEnabled(),
                article.getSortOrder(),
                article.getCreatedByUserId(),
                false,
                false,
                article.getCreatedAt(),
                article.getUpdatedAt()
        );
    }
}
