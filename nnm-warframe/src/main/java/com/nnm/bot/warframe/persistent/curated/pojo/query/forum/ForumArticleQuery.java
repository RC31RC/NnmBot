package com.nnm.bot.warframe.persistent.curated.pojo.query.forum;

import cn.hutool.core.util.StrUtil;
import java.util.List;

/**
 * 论坛文章分页查询条件。
 */
public record ForumArticleQuery(
        Integer current,
        Integer size,
        String keyword,
        String category,
        List<String> tags,
        Boolean enabled
) {

    /**
     * 构造论坛文章查询，并保存初始化参数。
     *
     * @param current 从一开始的当前页码。
     * @param size 当前操作允许的数据数量。
     * @param keyword 筛选使用的关键词。
     * @param category 分类。
     * @param tags 标签集合。
     * @param enabled 操作后的启用状态。
     */
    public ForumArticleQuery {
        tags = tags == null
                ? List.of()
                : tags.stream()
                        .filter(value -> StrUtil.isNotBlank(value))
                        .map(String::trim)
                        .distinct()
                        .toList();
    }
}
