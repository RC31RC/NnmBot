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
import java.util.Locale;

/** 查询信条相关数据。 */
@BotPluginCommand(
        code = WarframeCommandCodes.SISTERS,
        matchType = MatchType.PREFIX,
        name = "信条",
        gameCode = "warframe",
        order = 67)
// wf_community_term：读取 term_group=sisters 的中英文静态资料，不表示实时武器库存。
public final class SistersPlugin extends BotPlugin {

    @Resource
    private WarframeCommunityCatalogService catalog;

    @Override
    public CommandResult handle(CommandInvocation invocation) {
        var items = catalog.terms().stream()
                .filter(item -> "sisters".equalsIgnoreCase(item.details().termGroup()))
                .filter(item -> contains(item.details().englishText(), invocation.arguments())
                        || contains(item.details().chineseText(), invocation.arguments()))
                .limit(20).toList();
        return CommandResult.of(items.isEmpty() ? "没有找到信条数据" : "信条：找到 " + items.size() + " 条",
                new WarframeCommunityCommandDisplay(invocation.arguments(), items.stream()
                        .map(item -> WarframeCommunityCommandDisplay.Term.from(item.details())).toList(),
                        "当前为信条静态资料，不是实时武器出售清单"));
    }

    private static boolean contains(String value, String query) {
        return query == null || query.isBlank() || (value != null
                && value.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)));
    }
}
