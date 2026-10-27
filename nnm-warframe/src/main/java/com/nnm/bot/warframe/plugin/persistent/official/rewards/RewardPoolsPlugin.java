package com.nnm.bot.warframe.plugin.persistent.official.rewards;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.persistent.official.pojo.vo.rewards.WarframeRewardPoolVO.EntryVO;
import java.util.regex.Matcher;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.CommandView;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.persistent.official.pojo.vo.rewards.WarframeRewardPoolVO;
import com.nnm.bot.warframe.persistent.official.service.rewards.WarframeRewardPoolService;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

@BotPluginCommand(code = WarframeCommandCodes.REWARD_POOLS, matchType = MatchType.PREFIX,
        name = "奖励池", gameCode = "warframe", order = 72)
// Plus/DataSource/info-api -> reward_pool + reward_entry；只展示已采纳的同来源条目。
public final class RewardPoolsPlugin extends BotPlugin {

    /**
     * 将业务数据转换为命令或页面所需的展示字段。
     *
     * @return 当前命令或页面需要的展示字段。
     */
    @Override
    public List<CommandView> views() {
        return List.of(CommandView.textImageAndMarkdown("warframe.reward-pools"));
    }

    private static final Pattern OPTION = Pattern.compile("(?:^|\\s)--(page|stage)(?:\\s+|=)([0-9]+)$");
    private static final Pattern UNKNOWN_OPTION = Pattern.compile("(?:^|\\s)--\\S*");
    @Resource
    private WarframeRewardPoolService rewardPoolService;

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
        Integer stage = null;
        HashSet<String> options = new HashSet<String>();
        while (true) {
            Matcher matcher = OPTION.matcher(query);
            if (!matcher.find()) break;
            if (!options.add(matcher.group(1))) return CommandResult.rejected("参数不能重复");
            int value;
            try {
                value = Integer.parseInt(matcher.group(2));
            } catch (NumberFormatException exception) {
                if ("stage".equals(matcher.group(1))) return CommandResult.rejected("阶段超出范围");
                value = Integer.MAX_VALUE;
            }
            if ("page".equals(matcher.group(1))) page = value;
            else stage = value;
            query = query.substring(0, matcher.start()).trim();
        }
        if (page < 1 || stage != null && stage < 1 || UNKNOWN_OPTION.matcher(query).find()) {
            return CommandResult.rejected("页码和阶段必须为正整数");
        }
        if (query.length() > 200) return CommandResult.rejected("查询内容过长，请缩短到 200 字以内");
        WarframeRewardPoolVO display = WarframeRewardPoolVO.page(query, rewardPoolService.find(query, stage), page);
        StringBuilder text = new StringBuilder("奖励池：").append(display.pagination());
        for (EntryVO item : display.items()) {
            text.append("\n").append(item.poolName());
            if (item.emptyPool()) {
                text.append(" · 无已采纳奖励");
                continue;
            }
            text.append(" · 阶段 ").append(item.stage()).append("\n").append(item.itemName())
                    .append(" × ").append(item.count());
            if (item.probability() != null) text.append(" · ")
                    .append(item.probability().multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString())
                    .append("%");
            if (item.rarity() != null) text.append(" · ").append(item.rarity());
        }
        if (CollUtil.isEmpty(display.items())) text.append("\n没有找到匹配的奖励池");
        return CommandResult.view("warframe.reward-pools", text.toString(), display);
    }
}
