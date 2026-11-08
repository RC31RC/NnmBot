package com.nnm.bot.warframe.plugin.persistent.official.reference;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.persistent.official.service.reference.WarframeTranslationService;
import jakarta.annotation.Resource;
import java.util.List;

/** 查询游戏文本翻译。 */
@BotPluginCommand(code = WarframeCommandCodes.TRANSLATION, matchType = MatchType.PREFIX)
// 从当前生效的官方语言根树查询，自维护完整根优先。
public final class TranslationPlugin extends BotPlugin<Void, String> {
    /**
     * 取得已匹配的命令参数，供业务命令解析。
     *
     * @param invocation 当前命令调用
     * @param prepared 无额外准备输入
     * @return 已规范化的命令参数
     */
    @Override
    protected String parse(CommandInvocation invocation, Void prepared) {
        return invocation.arguments() == null ? "" : invocation.arguments().trim();
    }

    @Resource
    private WarframeTranslationService catalog;

    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation, Void prepared, String parsed) {
        var display = catalog.translations(parsed);
        return CommandResult.html("翻译查询", display);
    }
}
