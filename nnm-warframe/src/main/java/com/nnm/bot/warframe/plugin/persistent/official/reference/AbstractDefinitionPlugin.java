package com.nnm.bot.warframe.plugin.persistent.official.reference;

import com.nnm.bot.warframe.persistent.official.pojo.vo.reference.WarframeDefinitionVO;
import java.util.regex.Matcher;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.warframe.persistent.official.pojo.query.reference.WarframeDefinitionQuery;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.service.reference.WarframeDefinitionService;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.regex.Pattern;

abstract class AbstractDefinitionPlugin extends BotPlugin<Void, WarframeDefinitionQuery> {
    /**
     * 取得已匹配的命令参数，供业务命令解析。
     *
     * @param invocation 当前命令调用
     * @param prepared 无额外准备输入
     * @return 已规范化的命令参数
     */
    @Override
    protected WarframeDefinitionQuery parse(CommandInvocation invocation, Void prepared) {
        String query = invocation.arguments() == null ? "" : invocation.arguments().trim();
        int page = 1;
        Matcher matcher = PAGE.matcher(query);
        if (matcher.find()) {
            if (!matcher.group(1).matches("[0-9]+"))
                return new WarframeDefinitionQuery(query, page, "页码必须为正整数");
            try {
                page = Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException exception) {
                page = Integer.MAX_VALUE;
            }
            query = query.substring(0, matcher.start()).trim();
        }
        if (page < 1 || OPTION.matcher(query).find())
            return new WarframeDefinitionQuery(query, page, "定义查询参数不合法");
        if (query.length() > 500)
            return new WarframeDefinitionQuery(query, page, "查询内容过长，请缩短到 500 字以内");
        return new WarframeDefinitionQuery(query, page, null);
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
     * @param prepared 普通命令无需前置输入，固定为 null。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation, Void prepared, WarframeDefinitionQuery parsed) {
        if (parsed.error() != null)
            return CommandResult.rejected(parsed.error());
        WarframeDefinitionVO display = definitionService.query(domain(), parsed.keyword(), parsed.page());
        return CommandResult.html(display.text(), display);
    }
}
