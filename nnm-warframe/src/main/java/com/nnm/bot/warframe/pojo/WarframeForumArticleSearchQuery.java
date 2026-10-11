package com.nnm.bot.warframe.pojo;

import java.util.List;

/**
 * wf配装命令的结构化查询条件。
 *
 * <p>带字段前缀的条件由插件解析；服务只接收已经分组好的标题、分类、标签和普通关键词，
 * 负责匹配、评分和限制候选数量。</p>
 */
public record WarframeForumArticleSearchQuery(
        String title,
        String category,
        List<String> tags,
        List<String> keywords
) {

    public WarframeForumArticleSearchQuery {
        title = normalize(title);
        category = normalize(category);
        tags = normalizeList(tags);
        keywords = normalizeList(keywords);
    }

    public boolean isBlank() {
        return title.isBlank()
                && category.isBlank()
                && tags.isEmpty()
                && keywords.isEmpty();
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private static List<String> normalizeList(List<String> values) {
        return values == null
                ? List.of()
                : values.stream()
                        .map(WarframeForumArticleSearchQuery::normalize)
                        .filter(value -> !value.isBlank())
                        .toList();
    }
}
