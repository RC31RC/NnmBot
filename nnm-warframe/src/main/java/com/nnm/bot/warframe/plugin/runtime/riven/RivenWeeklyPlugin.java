package com.nnm.bot.warframe.plugin.runtime.riven;

import com.nnm.bot.warframe.runtime.pojo.vo.riven.WarframeRivenWeeklyVO;
import com.nnm.bot.warframe.runtime.pojo.dto.riven.WarframeRivenWeeklyCommandDTO;
import com.nnm.bot.warframe.runtime.pojo.query.riven.WarframeRivenWeeklyQuery;
import java.util.regex.Matcher;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.service.riven.WarframeRivenWeeklyService;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

@BotPluginCommand(code = WarframeCommandCodes.RIVEN_WEEKLY, matchType = MatchType.PREFIX)
public final class RivenWeeklyPlugin extends BotPlugin<Void, WarframeRivenWeeklyCommandDTO> {
    /**
     * 取得已匹配的命令参数，供业务命令解析。
     *
     * @param invocation 当前命令调用
     * @param prepared 无额外准备输入
     * @return 已规范化的命令参数
     */
    @Override
    protected WarframeRivenWeeklyCommandDTO parse(CommandInvocation invocation, Void prepared) {
        String query = invocation.arguments() == null ? "" : invocation.arguments().trim();
        Boolean rerolled = null;
        int page = 1;
        Set<String> seen = new HashSet<>();
        Matcher matcher = OPTION.matcher(query);
        while (matcher.find()) {
            String option = matcher.group(1);
            String value = matcher.group(2).toLowerCase(Locale.ROOT);
            if (!seen.add(option)) {
                return new WarframeRivenWeeklyCommandDTO(query, rerolled, page, "参数不能重复：" + option);
            }
            switch (option) {
                case "rerolled" -> {
                    if (!Set.of("true", "false").contains(value)) {
                        return new WarframeRivenWeeklyCommandDTO(query, rerolled, page, "已洗条件必须为 true 或 false");
                    }
                    rerolled = Boolean.valueOf(value);
                }
                case "page" -> {
                    if (!value.matches("[0-9]+") || value.matches("0+")) {
                        return new WarframeRivenWeeklyCommandDTO(query, rerolled, page, "页码必须为正整数");
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
            return new WarframeRivenWeeklyCommandDTO(query, rerolled, page, "查询过长或参数格式无效");
        }
        return new WarframeRivenWeeklyCommandDTO(query, rerolled, page, null);
    }

    private static final int PAGE_SIZE = 8;
    private static final Pattern OPTION = Pattern.compile("(?:^|\\s)--(rerolled|page)(?:=|\\s+)(\\S+)$");
    @Resource
    private WarframeRivenWeeklyService weeklyService;

    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @param prepared 普通命令无需前置输入，固定为 null。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation, Void prepared, WarframeRivenWeeklyCommandDTO parsed) {
        if (parsed.error() != null)
            return CommandResult.rejected(parsed.error());
        WarframeRivenWeeklyVO display = weeklyService
            .find(new WarframeRivenWeeklyQuery("pc", parsed.item(), parsed.rerolled(), parsed.page(), PAGE_SIZE));
        return CommandResult.html("紫卡周成交统计：" + display.pagination(), display);
    }
}
