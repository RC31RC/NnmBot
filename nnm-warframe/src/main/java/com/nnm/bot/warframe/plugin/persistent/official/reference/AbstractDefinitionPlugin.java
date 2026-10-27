package com.nnm.bot.warframe.plugin.persistent.official.reference;

import com.nnm.bot.warframe.persistent.official.pojo.vo.reference.WarframeDefinitionVO;
import java.util.regex.Matcher;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.CommandView;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.service.reference.WarframeDefinitionService;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.regex.Pattern;

abstract class AbstractDefinitionPlugin extends BotPlugin {

    /**
     * 将业务数据转换为命令或页面所需的展示字段。
     *
     * @return 当前命令或页面需要的展示字段。
     */
    @Override
    public List<CommandView> views() {
        return List.of(CommandView.textImageAndMarkdown("warframe.definitions"));
    }

    private static final Pattern PAGE = Pattern.compile("(?:^|\\s)--page(?:\\s+|=)(\\S+)$");
    private static final Pattern OPTION = Pattern.compile("(?:^|\\s)--\\S*");
    @Resource
    private WarframeDefinitionService definitionService;

    /**
     * 取得当前独立数据文件的领域标识。
     *
     * @return 当前独立 JSON 的领域标识。
     */
    protected abstract Category domain();

    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation) {
        String query = invocation.arguments() == null ? "" : invocation.arguments().trim();
        int page = 1;
        Matcher matcher = PAGE.matcher(query);
        if (matcher.find()) {
            if (!matcher.group(1).matches("[0-9]+")) return CommandResult.rejected("页码必须为正整数");
            try {
                page = Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException exception) {
                page = Integer.MAX_VALUE;
            }
            query = query.substring(0, matcher.start()).trim();
        }
        if (page < 1 || OPTION.matcher(query).find()) return CommandResult.rejected("定义查询参数不合法");
        if (query.length() > 500) return CommandResult.rejected("查询内容过长，请缩短到 500 字以内");
        WarframeDefinitionVO display = definitionService.query(domain(), query, page);
        return CommandResult.view("warframe.definitions", display.text(), display);
    }
}
