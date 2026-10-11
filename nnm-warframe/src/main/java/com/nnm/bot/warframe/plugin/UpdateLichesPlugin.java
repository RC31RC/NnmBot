package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

/** 提示赤毒静态资料改由管理端上传预览并人工合并。 */
@BotPluginCommand(
        code = WarframeCommandCodes.UPDATE_LICHES,
        matchType = MatchType.EXACT,
        name = "更新赤毒",
        gameCode = "warframe",
        order = 61)
public final class UpdateLichesPlugin extends BotPlugin {

    @Override
    public CommandResult handle(CommandInvocation invocation) {
        return CommandResult.text("赤毒远程数据源已停用。请由管理端上传 term.json，"
                + "完成预览和差异选择后再人工合并；当前赤毒查询仍可用。");
    }
}
