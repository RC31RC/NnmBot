package com.nnm.bot.warframe.plugin.persistent.official.rewards;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.persistent.official.pojo.vo.rewards.WarframeRewardPoolVO.EntryVO;
import java.util.regex.Matcher;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.warframe.persistent.official.pojo.query.rewards.WarframeRewardPoolQuery;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.persistent.official.pojo.vo.rewards.WarframeRewardPoolVO;
import com.nnm.bot.warframe.persistent.official.service.rewards.WarframeRewardPoolService;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

@BotPluginCommand(code = WarframeCommandCodes.REWARD_POOLS, matchType = MatchType.PREFIX)
// Plus/DataSource/info-api -> reward_pool + reward_entry；只展示已采纳的同来源条目。
public final class RewardPoolsPlugin extends BotPlugin<Void, WarframeRewardPoolQuery> {
    /**
     * 取得已匹配的命令参数，供业务命令解析。
     *
     * @param invocation 当前命令调用
     * @param prepared 无额外准备输入
     * @return 已规范化的命令参数
     */
    @Override
    protected WarframeRewardPoolQuery parse(CommandInvocation invocation, Void prepared) {
        String query = invocation.arguments() == null ? "" : invocation.arguments().trim();
        int page = 1;
        Integer stage = null;
        HashSet<String> options = new HashSet<String>();
        while (true) {
            Matcher matcher = OPTION.matcher(query);
            if (!matcher.find())
                break;
            if (!options.add(matcher.group(1)))
                return new WarframeRewardPoolQuery(query, stage, page, "参数不能重复");
            int value;
            try {
                value = Integer.parseInt(matcher.group(2));
            } catch (NumberFormatException exception) {
                if ("stage".equals(matcher.group(1)))
                    return new WarframeRewardPoolQuery(query, stage, page, "阶段超出范围");
                value = Integer.MAX_VALUE;
            }
            if ("page".equals(matcher.group(1)))
                page = value;
            else
                stage = value;
            query = query.substring(0, matcher.start()).trim();
        }
        if (page < 1 || stage != null && stage < 1 || UNKNOWN_OPTION.matcher(query).find()) {
            return new WarframeRewardPoolQuery(query, stage, page, "页码和阶段必须为正整数");
        }
        if (query.length() > 200)
            return new WarframeRewardPoolQuery(query, stage, page, "查询内容过长，请缩短到 200 字以内");
        return new WarframeRewardPoolQuery(query, stage, page, null);
    }

    private static final Pattern OPTION = Pattern.compile("(?:^|\\s)--(page|stage)(?:\\s+|=)([0-9]+)$");
    private static final Pattern UNKNOWN_OPTION = Pattern.compile("(?:^|\\s)--\\S*");
    @Resource
    private WarframeRewardPoolService rewardPoolService;

    /**
     * 根据命令参数调用业务服务并组装命令结果。
     *
     * @param invocation 命令执行上下文，包含用户、作用域和已匹配参数。
     * @param prepared 普通命令无需前置输入，固定为 null。
     * @return 命令处理结果，包含文本、展示对象或明确的输入错误。
     */
    @Override
    public CommandResult handle(CommandInvocation invocation, Void prepared, WarframeRewardPoolQuery parsed) {
        if (parsed.error() != null)
            return CommandResult.rejected(parsed.error());
        String query = parsed.keyword();
        int page = parsed.page();
        Integer stage = parsed.stage();
        WarframeRewardPoolVO display = WarframeRewardPoolVO.page(query, rewardPoolService.find(query, stage), page);
        StringBuilder text = new StringBuilder("奖励池：").append(display.pagination());
        for (EntryVO item : display.items()) {
            text.append("\n").append(item.poolName());
            if (item.emptyPool()) {
                text.append(" · 无已采纳奖励");
                continue;
            }
            text.append(" · 阶段 ").append(item.stage()).append("\n").append(item.itemName()).append(" × ")
                .append(item.count());
            if (item.probability() != null)
                text.append(" · ")
                    .append(item.probability().multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString())
                    .append("%");
            if (item.rarity() != null)
                text.append(" · ").append(item.rarity());
        }
        if (CollUtil.isEmpty(display.items()))
            text.append("\n没有找到匹配的奖励池");
        return CommandResult.html(text.toString(), display);
    }
}
