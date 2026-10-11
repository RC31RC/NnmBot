package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.warframe.service.WarframeWorldStateRuleService;
import jakarta.annotation.Resource;
import java.util.Locale;
import com.nnm.bot.core.plugin.model.CommandInvocation;

import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.pojo.WarframeCommunityCommandDisplay;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 查看游戏 1999 相关日历数据。 */
@BotPluginCommand(
        code = WarframeCommandCodes.CALENDAR_1999,
        name = "1999日历",
        gameCode = "warframe",
        order = 74)
// wf_community_worldstate_rule：查询 calendar1999 静态任务名称和说明，不含实时日期排期。
public final class Calendar1999Plugin extends BotPlugin {

    @Resource
    private WarframeWorldStateRuleService worldStateRuleService;

    @Override
    public CommandResult handle(
            CommandInvocation invocation) {
        String query = invocation.arguments().trim().toLowerCase(Locale.ROOT);
        var items = worldStateRuleService.stateTranslations().stream()
                // 当前目录是日历任务词典，不是官方 KnownCalendarSeasons 的实时日期排期。
                .filter(item -> contains(item.uniqueName(), "calendar1999"))
                .filter(item -> query.isBlank()
                        || contains(item.uniqueName(), query)
                        || contains(item.displayName(), query)
                        || contains(item.description(), query))
                .toList();
        return CommandResult.of(
                items.isEmpty() ? "没有找到 1999 日历任务资料" : "1999 日历任务资料",
                new WarframeCommunityCommandDisplay(query, items.stream()
                        .map(WarframeCommunityCommandDisplay.CalendarEntry::from).toList(),
                        "当前为静态任务资料，不包含实时日历日期排期"));
    }

    private static boolean contains(String value, String query) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(query);
    }
}
