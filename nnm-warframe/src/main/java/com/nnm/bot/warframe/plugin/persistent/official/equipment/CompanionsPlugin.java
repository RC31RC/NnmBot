package com.nnm.bot.warframe.plugin.persistent.official.equipment;

import java.util.regex.Matcher;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.CommandView;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.persistent.official.pojo.vo.equipment.WarframeCompanionVO;
import com.nnm.bot.warframe.persistent.official.service.equipment.WarframeCompanionService;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.regex.Pattern;

@BotPluginCommand(code = WarframeCommandCodes.COMPANIONS, matchType = MatchType.PREFIX,
        name = "同伴资料", gameCode = "warframe", order = 85)
public final class CompanionsPlugin extends BotPlugin {

    /**
     * 将业务数据转换为命令或页面所需的展示字段。
     *
     * @return 当前命令或页面需要的展示字段。
     */
    @Override
    public List<CommandView> views() {
        return List.of(CommandView.textImageAndMarkdown("warframe.companions"));
    }

    private static final Pattern PAGE = Pattern.compile("(?:^|\\s)--page(?:\\s+|=)([0-9]+)$");
    private static final Pattern OPTION = Pattern.compile("(?:^|\\s)--\\S*");

    @Resource
    private WarframeCompanionService companionService;

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
            try {
                page = Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException exception) {
                return CommandResult.rejected("页码必须为有效的正整数：--page 2");
            }
            query = query.substring(0, matcher.start()).trim();
        }
        if (page < 1 || OPTION.matcher(query).find()) return CommandResult.rejected("仅支持末尾分页参数：--page 2");
        if (query.length() > 200) return CommandResult.rejected("查询内容过长，请缩短到 200 字以内");
        WarframeCompanionVO display = WarframeCompanionVO.page(query, companionService.find(query), page);
        display = new WarframeCompanionVO(display.query(), display.page(), display.totalPages(), display.totalCount(),
                display.items().stream().map(companionService::withImage).toList(), display.notice());
        return CommandResult.view("warframe.companions", display.text(), display);
    }
}
