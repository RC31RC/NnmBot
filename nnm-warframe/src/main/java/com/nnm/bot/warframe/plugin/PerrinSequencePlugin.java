package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.api.BotPlugin;
import java.util.Locale;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.pojo.WarframeCommunityCommandDisplay;
import com.nnm.bot.warframe.service.catalog.WarframeCommunityCatalogService;
import jakarta.annotation.Resource;
import java.util.Map;

/** 查询阵营轮换与信条武器信息。 */
@BotPluginCommand(
        code = WarframeCommandCodes.PERRIN_SEQUENCE,
        matchType = MatchType.EXACT,
        name = "佩兰数列",
        gameCode = "warframe",
        order = 68)
// wf_community_term：读取 perrin、佩兰和信条相关静态术语，不表示本轮库存或元素加成。
public final class PerrinSequencePlugin extends BotPlugin {

    @Resource
    private WarframeCommunityCatalogService catalog;

    @Override
    public CommandResult handle(CommandInvocation invocation) {
        var items = catalog.terms().stream()
                .filter(item -> "perrin".equalsIgnoreCase(item.details().termGroup())
                        || contains(item.details().englishText(), "perrin")
                        || contains(item.details().chineseText(), "佩兰")
                        || contains(item.details().englishText(), "tenet")
                        || contains(item.details().chineseText(), "信条"))
                .limit(20).toList();
        return CommandResult.of(items.isEmpty() ? "没有找到佩兰数列奖励数据"
                        : "佩兰数列奖励：找到 " + items.size() + " 条",
                new WarframeCommunityCommandDisplay(null, items.stream()
                        .map(item -> WarframeCommunityCommandDisplay.Term.from(item.details())).toList(),
                        "当前为佩兰及信条静态资料，不代表本轮库存与元素加成"));
    }

    private static boolean contains(String value, String query) {
        return value != null && query != null
                && value.toLowerCase(Locale.ROOT)
                .contains(query.toLowerCase(Locale.ROOT));
    }
}
