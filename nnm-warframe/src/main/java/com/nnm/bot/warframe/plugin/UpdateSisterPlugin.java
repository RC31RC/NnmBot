package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;

import com.nnm.bot.core.plugin.model.CommandResult;

import com.nnm.bot.warframe.constant.WarframeCommandCodes;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;

/** 提示信条静态资料改由管理端上传预览并人工合并。 */
@BotPluginCommand(
        code = WarframeCommandCodes.UPDATE_SISTER,
        matchType = MatchType.EXACT,
        name = "更新信条",
        gameCode = "warframe",
        order = 60)
public final class UpdateSisterPlugin extends BotPlugin {

    @Override
    public CommandResult handle(
            CommandInvocation invocation) {
        return CommandResult.text("信条远程数据源已停用。请由管理端上传 term.json，"
                + "完成预览和差异选择后再人工合并；当前信条查询仍可用。");
    }
}
