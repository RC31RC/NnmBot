package com.nnm.bot.warframe.plugin.persistent.official.world;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.persistent.official.pojo.vo.world.WarframeSynthTargetVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.world.WarframeSynthTargetVO.EntryVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.world.WarframeSynthTargetVO.LocationVO;
import java.util.regex.Matcher;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.CommandView;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.persistent.official.service.world.WarframeSynthTargetService;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.regex.Pattern;

@BotPluginCommand(code = WarframeCommandCodes.SYNTH_TARGETS, matchType = MatchType.PREFIX,
        name = "合成目标地点", gameCode = "warframe", order = 73)
// 当前官方包没有合成目标地点资料，保留命令并明确返回不可用，不推测攻略。
public final class SynthTargetsPlugin extends BotPlugin {

    /**
     * 将业务数据转换为命令或页面所需的展示字段。
     *
     * @return 当前命令或页面需要的展示字段。
     */
    @Override
    public List<CommandView> views() {
        return List.of(CommandView.textImageAndMarkdown("warframe.synth-targets"));
    }

    private static final Pattern PAGE = Pattern.compile("(?:^|\\s)--page(?:\\s+|=)(\\S+)$");
    private static final Pattern UNKNOWN_OPTION = Pattern.compile("(?:^|\\s)--\\S*");
    @Resource
    private WarframeSynthTargetService targetService;

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
        if (page < 1 || UNKNOWN_OPTION.matcher(query).find()) return CommandResult.rejected("合成目标参数不合法");
        if (query.length() > 200) return CommandResult.rejected("查询内容过长，请缩短到 200 字以内");
        WarframeSynthTargetVO display = targetService.page(query, page);
        StringBuilder text = new StringBuilder("合成目标地点：").append(display.pagination())
                .append("\n历史地点攻略，不代表今日目标；概率与日期保留来源原文");
        for (EntryVO item : display.items()) {
            text.append("\n\n").append(item.name());
            if (CollUtil.isEmpty(item.locations())) text.append("\n地点未提供或未采纳");
            for (LocationVO location : item.locations()) {
                text.append("\n").append(location.mission());
                if (location.planet() != null) text.append(" · ").append(location.planet());
                if (location.missionType() != null) text.append(" · ").append(location.missionType());
                if (location.level() != null) text.append("\n等级：").append(location.level());
                if (location.faction() != null) text.append(" · 阵营：").append(location.faction());
                text.append("\n出现概率：").append(location.spawnRate() == null ? "未提供" : location.spawnRate())
                        .append(" · 验证日期：").append(location.lastVerified() == null ? "未提供" : location.lastVerified());
            }
        }
        if (CollUtil.isEmpty(display.items())) text.append("\n没有找到匹配的合成目标；当前官方数据未提供合成目标地点资料，无法查询");
        return CommandResult.view("warframe.synth-targets", text.toString(), display);
    }
}
