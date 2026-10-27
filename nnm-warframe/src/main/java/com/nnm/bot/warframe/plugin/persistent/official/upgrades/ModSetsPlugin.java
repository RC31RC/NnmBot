package com.nnm.bot.warframe.plugin.persistent.official.upgrades;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;

@BotPluginCommand(code = WarframeCommandCodes.MOD_SETS, matchType = MatchType.PREFIX,
        name = "MOD 套装", gameCode = "warframe", order = 70)
// Plus ExportModSet -> mod_set + mod_set_plus_rank_stat，等级表示装备件数。
public final class ModSetsPlugin extends AbstractUpgradePlugin {
    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation) {
        return upgrade(Category.MOD_SET, "MOD 套装", invocation.arguments());
    }
}
