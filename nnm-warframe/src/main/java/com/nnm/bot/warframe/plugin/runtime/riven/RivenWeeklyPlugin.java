package com.nnm.bot.warframe.plugin.runtime.riven;

import com.nnm.bot.warframe.runtime.pojo.vo.riven.WarframeRivenWeeklyVO;
import java.util.regex.Matcher;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.CommandView;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.service.riven.WarframeRivenWeeklyService;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

@BotPluginCommand(
        code = WarframeCommandCodes.RIVEN_WEEKLY,
        matchType = MatchType.PREFIX,
        name = "紫卡周成交统计",
        gameCode = "warframe",
        order = 63)
public final class RivenWeeklyPlugin extends BotPlugin {

    /**
     * 将业务数据转换为命令或页面所需的展示字段。
     *
     * @return 当前命令或页面需要的展示字段。
     */
    @Override
    public List<CommandView> views() {
        return List.of(CommandView.textImageAndMarkdown("warframe.riven-weekly"));
    }

    private static final Pattern OPTION = Pattern.compile("(?:^|\\s)--(platform|rerolled|page)(?:=|\\s+)(\\S+)$");
    @Resource
    private WarframeRivenWeeklyService weeklyService;

    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation) {
        String query = invocation.arguments() == null ? "" : invocation.arguments().trim();
        String platform = "pc";
        Boolean rerolled = null;
        int page = 1;
        Set<String> seen = new HashSet<>();
        Matcher matcher = OPTION.matcher(query);
        while (matcher.find()) {
            String option = matcher.group(1);
            String value = matcher.group(2).toLowerCase(Locale.ROOT);
            if (!seen.add(option)) {
                return CommandResult.rejected("参数不能重复：" + option);
            }
            switch (option) {
                case "platform" -> {
                    if (!Set.of("pc", "ps4", "xb1", "swi").contains(value)) {
                        return CommandResult.rejected("平台必须为 pc、ps4、xb1 或 swi");
                    }
                    platform = value;
                }
                case "rerolled" -> {
                    if (!Set.of("true", "false").contains(value)) {
                        return CommandResult.rejected("已洗条件必须为 true 或 false");
                    }
                    rerolled = Boolean.valueOf(value);
                }
                case "page" -> {
                    if (!value.matches("[0-9]+") || value.matches("0+")) {
                        return CommandResult.rejected("页码必须为正整数");
                    }
                    try {
                        page = Integer.parseInt(value);
                    } catch (NumberFormatException exception) {
                        page = Integer.MAX_VALUE;
                    }
                }
                default -> throw new IllegalStateException("Unsupported weekly Riven option");
            }
            query = query.substring(0, matcher.start()).trim();
            matcher = OPTION.matcher(query);
        }
        if (query.contains("--") || query.length() > 200) {
            return CommandResult.rejected("查询过长或参数格式无效");
        }
        WarframeRivenWeeklyVO display = weeklyService.find(platform, query, rerolled, page);
        return CommandResult.view("warframe.riven-weekly", "紫卡周成交统计：" + display.pagination(), display);
    }
}
