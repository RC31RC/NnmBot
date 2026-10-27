package com.nnm.bot.warframe.persistent.curated.pojo.vo.forum;

import com.nnm.bot.warframe.persistent.curated.pojo.dto.forum.ForumPreparedContentDTO;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.forum.ForumArticleContentDTO;
import java.time.LocalDateTime;

/**
 * 未登录文章页面使用的公开字段。
 */
public record ForumArticlePublicVO(
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

    /**
     * 将输入对象转换为当前业务数据对象。
     *
     * @param article 文章。
     * @param content 内容。
     * @return 已发布文章的公开展示内容。
     */
    public static ForumArticlePublicVO from(
            ForumArticleContentDTO article,
            ForumPreparedContentDTO content) {
        return new ForumArticlePublicVO(
                article.getId(), article.getSlug(), article.getTitle(), article.getSummary(),
                article.getTags(), article.getCategory(), content.coverUrl(), content.contentHtml(),
                article.getPageUrl(), article.getCreatedAt(), article.getUpdatedAt());
    }
}
