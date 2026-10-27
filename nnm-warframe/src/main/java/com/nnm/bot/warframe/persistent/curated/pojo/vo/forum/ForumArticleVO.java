package com.nnm.bot.warframe.persistent.curated.pojo.vo.forum;

import com.nnm.bot.warframe.persistent.curated.pojo.dto.forum.ForumOperatorDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.forum.ForumPreparedContentDTO;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.forum.ForumArticleContentDTO;
import java.time.LocalDateTime;

/**
 * 论坛文章管理端返回对象。
 */
public record ForumArticleVO(
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

    /**
     * 将输入对象转换为当前业务数据对象。
     *
     * @param article 文章。
     * @param operator 操作者。
     * @param ownerAuthorityLevel 所属人权限等级。
     * @param content 内容。
     * @return 文章详情及当前用户可用的操作。
     */
    public static ForumArticleVO from(
            ForumArticleContentDTO article,
            ForumOperatorDTO operator,
            int ownerAuthorityLevel,
            ForumPreparedContentDTO content) {
        long ownerUserId = article.getCreatedByUserId();
        return new ForumArticleVO(
                article.getId(), article.getSlug(), article.getTitle(), article.getSummary(),
                article.getTags(), article.getCategory(), content.coverUrl(), content.contentHtml(),
                content.contentMarkdown(), article.getPageUrl(), article.isEnabled(),
                article.getSortOrder(), article.getCreatedByUserId(),
                operator != null && operator.canUpdate(ownerUserId, ownerAuthorityLevel),
                operator != null && operator.canDelete(ownerUserId, ownerAuthorityLevel),
                article.getCreatedAt(), article.getUpdatedAt());
    }
}
