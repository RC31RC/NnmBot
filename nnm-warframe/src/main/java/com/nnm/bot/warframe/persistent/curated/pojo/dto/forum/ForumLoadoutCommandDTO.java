package com.nnm.bot.warframe.persistent.curated.pojo.dto.forum;

import com.nnm.bot.warframe.persistent.curated.pojo.query.forum.ForumArticleSearchQuery;

/** 命令的原始检索词与按字段解析的查询参数，供机器人和未来的工具入口复用。 */
public record ForumLoadoutCommandDTO(String input, ForumArticleSearchQuery query) {
}
