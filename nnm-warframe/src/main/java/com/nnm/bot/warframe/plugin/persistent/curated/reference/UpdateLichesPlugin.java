package com.nnm.bot.warframe.plugin.persistent.curated.reference;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;

/** 通用术语可在管理端刷新；独立赤毒库存尚未接入，本命令不绕过审核更新。 */
@BotPluginCommand(
        code = WarframeCommandCodes.UPDATE_LICHES,
        matchType = MatchType.EXACT,
        name = "更新赤毒",
        gameCode = "warframe",
        order = 61)
public final class UpdateLichesPlugin extends BotPlugin {

    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation) {
        return CommandResult.text("通用术语可在管理端刷新，但尚未接入独立赤毒库存数据源。"
                + "旧 term.json 请在管理端指定 DataSource 来源上传，"
                + "完成预览和差异选择后人工合并；此命令不执行更新。");
    }
}
