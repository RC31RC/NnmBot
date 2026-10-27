package com.nnm.bot.warframe.persistent.curated.pojo.dto.forum;

import lombok.Getter;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ForumArticleDTO implements ForumArticleContentDTO {
    private final Long id;
    private final String slug;
    private final String title;
    private final String summary;
    private final String tags;
    private final String category;
    private final String coverObjectId;
    private final String contentHtml;
    private final String contentMarkdown;
    private final String pageUrl;
    private final boolean enabled;
    private final int sortOrder;
    private final Long createdByUserId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
