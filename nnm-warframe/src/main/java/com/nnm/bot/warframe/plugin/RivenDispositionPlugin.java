package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.pojo.WarframeCommunityCommandDisplay;
import com.nnm.bot.warframe.service.catalog.WarframeCommunityCatalogService;
import jakarta.annotation.Resource;
import java.util.Map;

/** 查看紫卡倾向变化。 */
@BotPluginCommand(
        code = WarframeCommandCodes.RIVEN_DISPOSITION,
        matchType = MatchType.EXACT,
        name = "紫卡倾向变动",
        gameCode = "warframe",
        order = 62)
// wf_community_dataset_riven_trend：查询社区维护的紫卡倾向变动资料。
public final class RivenDispositionPlugin extends BotPlugin {

    @Resource
    private WarframeCommunityCatalogService catalog;

    @Override
    public CommandResult handle(CommandInvocation invocation) {
        var items = catalog.rivenTrends().stream()
                .filter(item -> invocation.arguments().isBlank()
                        || contains(item.details().trendName(), invocation.arguments())
                        || contains(item.details().weaponType(), invocation.arguments()))
                .limit(20).toList();
        return CommandResult.of(items.isEmpty() ? "没有找到紫卡倾向变动"
                        : "紫卡倾向变动：找到 " + items.size() + " 条",
                new WarframeCommunityCommandDisplay(invocation.arguments(), items.stream()
                        .map(item -> WarframeCommunityCommandDisplay.RivenTrend.from(item.details())).toList(),
                        items.size() == 20 ? "本次展示前 20 条，请补充关键词缩小范围" : null));
    }

    private static boolean contains(String value, String query) {
        return value != null && value.toLowerCase().contains(query.toLowerCase());
    }
}
