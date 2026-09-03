package com.nnm.bot.warframe.application.plugin;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.application.WarframeForumArticleService;
import com.nnm.bot.warframe.config.properties.WarframeForumProperties;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.dto.WarframeForumArticleItem;
import jakarta.annotation.Resource;

import java.util.List;

/**
 * 模糊查询管理端维护的配装文章，只返回文章地址，不把正文塞进机器人消息。
 */
@BotPluginCommand(
        code = WarframeCommandCodes.FORUM_LOADOUT,
        matchType = MatchType.PREFIX,
        name = "wf配装",
        gameCode = "warframe",
        order = 21)
public class ForumLoadoutPlugin extends BotPlugin {

    @Resource
    private WarframeForumArticleService articleService;

    @Resource
    private WarframeForumProperties forumProperties;

    @Override
    public CommandResult handle(CommandInvocation invocation) {
        if (StrUtil.isBlank(invocation.arguments())) {
            return CommandResult.text("wf配装命令格式：wf配装 关键词");
        }
        List<WarframeForumArticleItem> articles =
                articleService.searchEnabled(invocation.arguments());
        if (articles.isEmpty()) {
            return CommandResult.text("没有找到匹配的 Warframe 配装文章："
                    + invocation.arguments());
        }
        StringBuilder result = new StringBuilder("找到 ")
                .append(articles.size())
                .append(" 篇相关配装文章：");
        for (WarframeForumArticleItem article : articles) {
            result.append('\n')
                    .append(article.title())
                    .append('\n')
                    .append(forumProperties.publicArticleUrl(article.id()));
        }
        return CommandResult.text(result.toString());
    }
}
