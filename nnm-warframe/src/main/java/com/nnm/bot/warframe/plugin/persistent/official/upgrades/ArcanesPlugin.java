package com.nnm.bot.warframe.plugin.persistent.official.upgrades;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;

@BotPluginCommand(code = WarframeCommandCodes.ARCANES, matchType = MatchType.PREFIX,
        name = "奥术资料", gameCode = "warframe", order = 69)
// Items Arcanes / Plus ExportArcanes -> arcane + arcane_rank_stat，同文件联查。
public final class ArcanesPlugin extends AbstractUpgradePlugin {
    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation) {
        return upgrade(Category.ARCANE, "奥术资料", invocation.arguments());
    }
}
