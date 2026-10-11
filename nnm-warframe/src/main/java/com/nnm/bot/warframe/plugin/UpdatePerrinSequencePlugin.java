package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

/** 提示终幕静态资料改由管理端上传预览并人工合并。 */
@BotPluginCommand(
        code = WarframeCommandCodes.UPDATE_PERRIN_SEQUENCE,
        matchType = MatchType.EXACT,
        name = "更新终幕",
        gameCode = "warframe",
        order = 62)
public final class UpdatePerrinSequencePlugin extends BotPlugin {

    @Override
    public CommandResult handle(CommandInvocation invocation) {
        return CommandResult.text("终幕远程数据源已停用。请由管理端上传 term.json，"
                + "完成预览和差异选择后再人工合并；当前终幕查询仍可用。");
    }
}
