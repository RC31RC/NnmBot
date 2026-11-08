package com.nnm.bot.warframe.plugin.persistent.official.worldstate;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.WarframeStateTranslationDetailsDTO;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.persistent.official.pojo.vo.worldstate.WarframeCalendarCommandVO;
import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeWorldStateRuleService;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Locale;

/** 查看游戏 1999 相关日历数据。 */
@BotPluginCommand(code = WarframeCommandCodes.CALENDAR_1999)
public final class Calendar1999Plugin extends BotPlugin<Void, String> {
    /**
     * 取得已匹配的命令参数，供业务命令解析。
     *
     * @param invocation 当前命令调用
     * @param prepared 无额外准备输入
     * @return 已规范化的命令参数
     */
    @Override
    protected String parse(CommandInvocation invocation, Void prepared) {
        return invocation.arguments() == null ? "" : invocation.arguments().trim().toLowerCase(Locale.ROOT);
    }

    @Resource
    private WarframeWorldStateRuleService worldStateRuleService;

    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @param prepared 普通命令无需前置输入，固定为 null。
     * @param parsed 已归一化的查询词。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation, Void prepared, String parsed) {
        String query = parsed;
        List<WarframeStateTranslationDetailsDTO> items = worldStateRuleService.stateTranslations().stream()
            // 当前目录是日历任务词典，不是官方 KnownCalendarSeasons 的实时日期排期。
            .filter(item -> contains(item.uniqueName(), "calendar1999"))
            .filter(item -> StrUtil.isBlank(query) || contains(item.uniqueName(), query)
                || contains(item.displayName(), query) || contains(item.description(), query))
            .toList();
        return CommandResult.html(CollUtil.isEmpty(items) ? "没有找到 1999 日历任务资料" : "1999 日历任务资料",
            new WarframeCalendarCommandVO(query,
                items.stream().map(WarframeCalendarCommandVO.CalendarEntryVO::from).toList(), "当前为静态任务资料，不包含实时日历日期排期"));
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
