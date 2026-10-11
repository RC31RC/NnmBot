package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.warframe.service.catalog.WarframeCommunityCatalogService;
import jakarta.annotation.Resource;
import java.util.Locale;
import com.nnm.bot.core.plugin.model.CommandInvocation;

import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.pojo.WarframeCommunityCommandDisplay;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 查询游戏文本翻译。 */
@BotPluginCommand(
        code = WarframeCommandCodes.TRANSLATION,
        matchType = MatchType.PREFIX,
        name = "翻译",
        gameCode = "warframe",
        order = 63)
// wf_community_dataset_translation：按中英文名称或记录键查询社区翻译资料。
public final class TranslationPlugin extends BotPlugin {

    @Resource
    private WarframeCommunityCatalogService catalog;

    @Override
    public CommandResult handle(
            CommandInvocation invocation) {
        String query = invocation.arguments().trim().toLowerCase(Locale.ROOT);
        var items = catalog.translations().stream()
                .filter(item -> query.isBlank()
                        || contains(item.details().displayName(), query)
                        || contains(item.recordKey(), query))
                .limit(20).toList();
        return CommandResult.of(
                items.isEmpty() ? "没有找到翻译数据" : "翻译：找到 " + items.size() + " 条",
                new WarframeCommunityCommandDisplay(query, items.stream()
                        .map(item -> WarframeCommunityCommandDisplay.Translation.from(
                                item.recordKey(), item.details()))
                        .toList(), items.size() == 20 ? "本次展示前 20 条，请补充关键词缩小范围" : null));
    }

    private static boolean contains(String value, String query) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(query);
    }
}
