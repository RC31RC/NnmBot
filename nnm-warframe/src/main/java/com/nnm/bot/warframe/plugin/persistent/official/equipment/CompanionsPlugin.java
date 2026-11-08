package com.nnm.bot.warframe.plugin.persistent.official.equipment;

import java.util.regex.Matcher;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.warframe.persistent.official.pojo.query.catalog.WarframeCatalogPageQuery;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.persistent.official.pojo.vo.equipment.WarframeCompanionVO;
import com.nnm.bot.warframe.persistent.official.service.equipment.WarframeCompanionService;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.regex.Pattern;

@BotPluginCommand(code = WarframeCommandCodes.COMPANIONS, matchType = MatchType.PREFIX)
public final class CompanionsPlugin extends BotPlugin<Void, WarframeCatalogPageQuery> {
    /**
     * 取得已匹配的命令参数，供业务命令解析。
     *
     * @param invocation 当前命令调用
     * @param prepared 无额外准备输入
     * @return 已规范化的命令参数
     */
    @Override
    protected WarframeCatalogPageQuery parse(CommandInvocation invocation, Void prepared) {
        String query = invocation.arguments() == null ? "" : invocation.arguments().trim();
        int page = 1;
        Matcher matcher = PAGE.matcher(query);
        if (matcher.find()) {
            try {
                page = Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException exception) {
                return new WarframeCatalogPageQuery(query, page, "页码必须为有效的正整数：--page 2");
            }
            query = query.substring(0, matcher.start()).trim();
        }
        if (page < 1 || OPTION.matcher(query).find())
            return new WarframeCatalogPageQuery(query, page, "仅支持末尾分页参数：--page 2");
        if (query.length() > 200)
            return new WarframeCatalogPageQuery(query, page, "查询内容过长，请缩短到 200 字以内");
        return new WarframeCatalogPageQuery(query, page, null);
    }

    private static final Pattern PAGE = Pattern.compile("(?:^|\\s)--page(?:\\s+|=)([0-9]+)$");
    private static final Pattern OPTION = Pattern.compile("(?:^|\\s)--\\S*");

    @Resource
    private WarframeCompanionService companionService;

    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @param prepared 普通命令无需前置输入，固定为 null。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation, Void prepared, WarframeCatalogPageQuery parsed) {
        if (parsed.error() != null)
            return CommandResult.rejected(parsed.error());
        WarframeCompanionVO display =
            WarframeCompanionVO.page(parsed.keyword(), companionService.find(parsed.keyword()), parsed.page());
        display = new WarframeCompanionVO(display.query(), display.page(), display.totalPages(), display.totalCount(),
            display.items().stream().map(companionService::withImage).toList(), display.notice());
        return CommandResult.html(display.text(), display);
    }
}
