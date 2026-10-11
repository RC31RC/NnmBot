package com.nnm.bot.warframe.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.nnm.bot.core.plugin.annotation.CommandTextInline;
import com.nnm.bot.warframe.model.entity.WarframeCommunityRelicReward;

import java.math.BigDecimal;
import java.util.List;

/** 遗物及奖励的命令展示字段，数据库关联键不参与用户输出。 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record WarframeRelicDisplay(
        @JsonPropertyDescription("查询")
        String query,
        @JsonPropertyDescription("遗物")
        List<Relic> items,
        @JsonPropertyDescription("奖励")
        List<Reward> rewards) {

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record Relic(
            @JsonPropertyDescription("遗物")
            String relicName,
            @JsonPropertyDescription("入库")
            Boolean vaulted,
            @JsonPropertyDescription("奖励")
            List<Reward> rewards) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record Reward(
            @JsonPropertyDescription("物品")
            String rewardName,
            @JsonPropertyDescription("稀有度")
            String rarity,
            @JsonPropertyDescription("概率（%）")
            BigDecimal chance) {

        public static Reward from(WarframeCommunityRelicReward reward) {
            String rarity = switch (reward.getRarity() == null ? "" : reward.getRarity()) {
                case "Rare" -> "稀有";
                case "Uncommon" -> "罕见";
                case "Common" -> "常见";
                default -> reward.getRarity();
            };
            return new Reward(reward.getRewardName(), rarity, reward.getChance());
        }
    }
}
