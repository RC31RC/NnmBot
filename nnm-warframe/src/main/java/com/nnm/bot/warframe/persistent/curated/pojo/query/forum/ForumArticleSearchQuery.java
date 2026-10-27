package com.nnm.bot.warframe.persistent.curated.pojo.query.forum;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import java.util.List;

/**
 * wf配装命令的结构化查询条件。
 *
 * <p>带字段前缀的条件由插件解析；服务只接收已经分组好的标题、分类、标签和普通关键词，
 * 负责匹配、评分和限制候选数量。</p>
 */
public record ForumArticleSearchQuery(
        String title,
        String category,
        List<String> tags,
        List<String> keywords
) {

    /**
     * 构造论坛文章搜索查询，并保存初始化参数。
     *
     * @param title 标题。
     * @param category 分类。
     * @param tags 标签集合。
     * @param keywords 关键词集合。
     */
    public ForumArticleSearchQuery {
        title = normalize(title);
        category = normalize(category);
        tags = normalizeList(tags);
        keywords = normalizeList(keywords);
    }

    /**
     * 判断查询是否未指定任何有效筛选条件。
     *
     * @return 满足查询是否未指定任何有效筛选条件时为真，否则为假。
     */
    public boolean isBlank() {
        return StrUtil.isBlank(title)
                && StrUtil.isBlank(category)
                && CollUtil.isEmpty(tags)
                && CollUtil.isEmpty(keywords);
    }

    /**
     * 规范化输入，消除查询或比较时的格式差异。
     *
     * @param value 待校验或规范化的文本。
     * @return 规范化后的文本，供比较或查找使用。
     */
    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * 规范化列表。
     *
     * @param values 待处理的值集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<String> normalizeList(List<String> values) {
        return values == null
                ? List.of()
                : values.stream()
                        .map(ForumArticleSearchQuery::normalize)
                        .filter(value -> StrUtil.isNotBlank(value))
                        .toList();
    }
}
