package com.nnm.bot.warframe.plugin.persistent.official.upgrades;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.persistent.official.pojo.vo.upgrades.WarframeRivenDefinitionVO.AttributeVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.upgrades.WarframeRivenDefinitionVO.ChallengeVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.upgrades.WarframeRivenDefinitionVO.EntryVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.upgrades.WarframeRivenDefinitionVO.VO;
import java.util.regex.Matcher;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.CommandView;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.persistent.official.pojo.vo.upgrades.WarframeRivenDefinitionVO;
import com.nnm.bot.warframe.persistent.official.service.upgrades.WarframeRivenDefinitionService;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@BotPluginCommand(code = WarframeCommandCodes.RIVEN_DEFINITIONS, matchType = MatchType.PREFIX,
        name = "紫卡属性与挑战", gameCode = "warframe", order = 77)
public final class RivenDefinitionsPlugin extends BotPlugin {

    /**
     * 将业务数据转换为命令或页面所需的展示字段。
     *
     * @return 当前命令或页面需要的展示字段。
     */
    @Override
    public List<CommandView> views() {
        return List.of(CommandView.textImageAndMarkdown("warframe.riven-definitions"));
    }

    private static final Pattern OPTION = Pattern.compile("(?:^|\\s)--(page|mode)(?:\\s+|=)(\\S+)$");
    private static final Pattern UNKNOWN_OPTION = Pattern.compile("(?:^|\\s)--\\S*");
    @Resource
    private WarframeRivenDefinitionService definitionService;

    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation) {
        String query = invocation.arguments() == null ? "" : invocation.arguments().trim();
        String mode = "attributes";
        int page = 1;
        Set<String> seen = new HashSet<>();
        while (true) {
            Matcher matcher = OPTION.matcher(query);
            if (!matcher.find()) break;
            if (!seen.add(matcher.group(1))) return CommandResult.rejected("参数不能重复");
            if (matcher.group(1).equals("mode")) {
                mode = matcher.group(2);
                if (!Set.of("attributes", "challenges").contains(mode)) {
                    return CommandResult.rejected("mode 只支持 attributes / challenges");
                }
            } else {
                if (!matcher.group(2).matches("[0-9]+")) return CommandResult.rejected("页码必须为正整数");
                try {
                    page = Integer.parseInt(matcher.group(2));
                } catch (NumberFormatException exception) {
                    page = Integer.MAX_VALUE;
                }
            }
            query = query.substring(0, matcher.start()).trim();
        }
        if (page < 1 || UNKNOWN_OPTION.matcher(query).find()) return CommandResult.rejected("页码或参数不合法");
        if (query.length() > 200) return CommandResult.rejected("查询内容过长，请缩短到 200 字以内");
        WarframeRivenDefinitionVO display = WarframeRivenDefinitionVO.page(query, mode, definitionService.find(query, mode), page);
        StringBuilder text = new StringBuilder(mode.equals("attributes") ? "紫卡属性基准" : "紫卡挑战")
                .append("：").append(display.pagination());
        for (EntryVO item : display.items()) {
            text.append("\n").append(item.modName());
            if (item.attribute() != null) {
                AttributeVO attribute = item.attribute();
                text.append("\n").append(attribute.tag()).append(" · 正属性：").append(flag(attribute.canBeBuff()))
                        .append(" · 负属性：").append(flag(attribute.canBeCurse()));
                for (VO value : attribute.values()) {
                    text.append("\n").append(value.description() == null ? "未提供本地化描述" : value.description())
                            .append(" · 基础系数：").append(value.baseCoefficient().toPlainString());
                }
                if (CollUtil.isEmpty(attribute.values())) text.append("\n未采纳数值定义");
            } else {
                ChallengeVO challenge = item.challenge();
                text.append("\n").append(challenge.description() == null
                        ? challenge.challengeKey() : challenge.description());
                if (challenge.countMin() != null) {
                    text.append(" · 数量：").append(challenge.countMin()).append(" ~ ").append(challenge.countMax());
                }
                challenge.complications().forEach(condition -> text.append("\n附加条件：")
                        .append(condition.description() == null ? condition.complicationKey() : condition.description()));
            }
        }
        if (CollUtil.isEmpty(display.items())) text.append("\n没有找到匹配的已采纳定义");
        return CommandResult.view("warframe.riven-definitions", text.toString(), display);
    }

    /**
     * 读取输入中明确提供的布尔标记。
     *
     * @param value 待处理的字段值。
     * @return 输入中明确提供的布尔标记对应的文本。
     */
    private String flag(Boolean value) {
        return value == null ? "未提供" : value ? "可用" : "不可用";
    }
}
