package com.nnm.bot.warframe.persistent.curated.pojo.vo.forum;

import java.util.List;

/**
 * 论坛文章管理端可选的分类和标签，以及首篇文章的元数据初始化权限。
 *
 * <p>选项来自现有文章数据，避免把标签写成无法维护的自由文本；数据库仍保留
 * 兼容历史数据的逗号分隔字符串。仅在文章表为空时允许创建分类和标签，用首篇文章
 * 初始化可选项。</p>
 */
public record ForumArticleOptionsVO(
        List<String> categories,
        List<String> tags,
        boolean metadataInitializationAllowed
) {
}
