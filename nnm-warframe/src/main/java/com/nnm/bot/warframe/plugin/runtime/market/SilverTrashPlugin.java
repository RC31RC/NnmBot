package com.nnm.bot.warframe.plugin.runtime.market;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

/** 查询 45 至 99 杜卡德金币物品的市场成交性价比。 */
@BotPluginCommand(
        code = WarframeCommandCodes.SILVER_TRASH,
        matchType = MatchType.PREFIX,
        name = "银垃圾",
        gameCode = "warframe",
        order = 70)
public final class SilverTrashPlugin extends AbstractDucatsPlugin {

    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation) {
        return ranking(false, invocation.arguments());
    }
}
