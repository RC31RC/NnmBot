package com.nnm.bot.warframe.pojo;

import java.util.List;

/**
 * 论坛文章分页查询条件。
 */
public record WarframeForumArticleQueryRequest(
        Integer current,
        Integer size,
        String keyword,
        String category,
        List<String> tags,
        Boolean enabled
) {

    public WarframeForumArticleQueryRequest {
        tags = tags == null
                ? List.of()
                : tags.stream()
                        .filter(value -> value != null && !value.isBlank())
                        .map(String::trim)
                        .distinct()
                        .toList();
    }
}
