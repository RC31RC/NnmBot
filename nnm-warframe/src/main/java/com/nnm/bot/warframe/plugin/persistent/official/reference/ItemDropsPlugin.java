package com.nnm.bot.warframe.plugin.persistent.official.reference;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

@BotPluginCommand(code = WarframeCommandCodes.ITEM_DROPS, matchType = MatchType.PREFIX,
        name = "物品掉落", gameCode = "warframe", order = 84)
public final class ItemDropsPlugin extends AbstractItemRelationPlugin {
    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation) {
        return relations(invocation.arguments(), false);
    }
}
