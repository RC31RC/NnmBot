package com.nnm.bot.warframe.plugin.persistent.official.reference;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.CommandView;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.persistent.official.service.reference.WarframeTranslationService;
import jakarta.annotation.Resource;
import java.util.List;

/** 查询游戏文本翻译。 */
@BotPluginCommand(
        code = WarframeCommandCodes.TRANSLATION,
        matchType = MatchType.PREFIX,
        name = "翻译",
        gameCode = "warframe",
        order = 63)
// 从当前生效的官方语言根树查询，自维护完整根优先。
public final class TranslationPlugin extends BotPlugin {

    /**
     * 将业务数据转换为命令或页面所需的展示字段。
     *
     * @return 当前命令或页面需要的展示字段。
     */
    @Override
    public List<CommandView> views() {
        return List.of(CommandView.textImageAndMarkdown("warframe.community"));
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
    public CommandResult handle(
            CommandInvocation invocation) {
        var display = catalog.translations(invocation.arguments());
        return CommandResult.view("warframe.community", "翻译查询", display);
    }
}
