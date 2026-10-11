package com.nnm.bot.warframe.plugin;

import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.warframe.application.WarframeRelicService;
import com.nnm.bot.warframe.model.entity.WarframeCommunityRelic;
import com.nnm.bot.warframe.model.entity.WarframeCommunityRelicReward;
import com.nnm.bot.warframe.pojo.WarframeRelicDisplay;
import jakarta.annotation.Resource;
import java.util.List;

/**
 * 遗物查询公共层。
 *
 * <p>使用 {@code wf_community_relic} 查询遗物名称和入库状态，使用
 * {@code wf_community_relic_reward} 按遗物键查询奖励及稀有度。</p>
 */
abstract class AbstractRelicPlugin extends BotPlugin {
    @Resource
    protected WarframeRelicService relicService;

    protected CommandResult relics(String keyword) {
        List<WarframeCommunityRelic> relics = relicService.findRelics(keyword);
        // 关联键仅用于查询奖励，不进入任何输出模式的展示数据。
        var result = relics.stream().map(relic -> new WarframeRelicDisplay.Relic(
                relic.getRelicName(), relic.getVaulted(),
                relicService.findRewards(relic.getRecordKey(), null).stream()
                        .map(WarframeRelicDisplay.Reward::from).toList())).toList();
        return CommandResult.of(result.isEmpty() ? "没有找到匹配的遗物" : "遗物：找到 " + result.size() + " 条",
                new WarframeRelicDisplay(keyword, result, List.of()));
    }

    protected CommandResult trash(String rarity, String keyword) {
        List<WarframeCommunityRelicReward> rewards = relicService.findRewards(keyword, rarity);
        return CommandResult.of(rewards.isEmpty() ? "没有找到匹配的" + rarity + "遗物奖励"
                        : rarity + "遗物奖励：找到 " + rewards.size() + " 条",
                new WarframeRelicDisplay(keyword, List.of(),
                        rewards.stream().map(WarframeRelicDisplay.Reward::from).distinct().toList()));
    }

    protected String argument(CommandInvocation invocation) {
        return invocation.arguments() == null ? "" : invocation.arguments().trim();
    }
}
