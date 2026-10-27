package com.nnm.bot.warframe.plugin.persistent.official.worldstate;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.WarframeStateTranslationDetailsDTO;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.CommandView;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.persistent.official.pojo.vo.worldstate.WarframeCalendarCommandVO;
import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeWorldStateRuleService;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Locale;

/** 查看游戏 1999 相关日历数据。 */
@BotPluginCommand(
        code = WarframeCommandCodes.CALENDAR_1999,
        name = "1999日历",
        gameCode = "warframe",
        order = 74,
        allowRelatedAction = true)
public final class Calendar1999Plugin extends BotPlugin {

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
    private WarframeWorldStateRuleService worldStateRuleService;

    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(
            CommandInvocation invocation) {
        String query = invocation.arguments().trim().toLowerCase(Locale.ROOT);
        List<WarframeStateTranslationDetailsDTO> items = worldStateRuleService.stateTranslations().stream()
                // 当前目录是日历任务词典，不是官方 KnownCalendarSeasons 的实时日期排期。
                .filter(item -> contains(item.uniqueName(), "calendar1999"))
                .filter(item -> StrUtil.isBlank(query)
                        || contains(item.uniqueName(), query)
                        || contains(item.displayName(), query)
                        || contains(item.description(), query))
                .toList();
        return CommandResult.view("warframe.community",
                CollUtil.isEmpty(items) ? "没有找到 1999 日历任务资料" : "1999 日历任务资料",
                new WarframeCalendarCommandVO(query, items.stream()
                        .map(WarframeCalendarCommandVO.CalendarEntryVO::from).toList(),
                        "当前为静态任务资料，不包含实时日历日期排期"));
    }

    /**
     * 判断文本或集合是否包含指定内容。
     *
     * @param value 待校验或规范化的文本。
     * @param query 当前业务的查询条件。
     * @return 满足文本或集合是否包含指定内容时为真，否则为假。
     */
    private static boolean contains(String value, String query) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(query);
    }
}
