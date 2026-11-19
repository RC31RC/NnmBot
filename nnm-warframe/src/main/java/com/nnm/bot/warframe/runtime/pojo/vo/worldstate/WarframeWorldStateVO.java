package com.nnm.bot.warframe.runtime.pojo.vo.worldstate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.nnm.bot.core.plugin.annotation.CommandTextInline;
import java.util.List;

/**
 * 世界状态命令的展示协议。仅保留玩家需要的信息；未解析字段保留原值，未完成的字段附带解析状态供人工核对，抓取元数据留在内部投影中。各输出模式共用这些对象，行内注解只影响文字布局。
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record WarframeWorldStateVO(@JsonPropertyDescription("项目") List<?> items,
    @JsonPropertyDescription("提示") String notice,
    @JsonPropertyDescription("待确认解析") List<WarframeWorldStateResolutionVO> pendingResolutions) {

    /**
     * 构造世界状态展示对象，并保存初始化参数。
     *
     * @param items 当前物品或成员列表。
     * @param notice 提示。
     * @param pendingResolutions 尚未完全解析的字段。
     */
    public WarframeWorldStateVO {
        items = items == null ? List.of() : List.copyOf(items);
        pendingResolutions = pendingResolutions == null ? List.of() : List.copyOf(pendingResolutions);
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record AlertVO(@JsonPropertyDescription("节点") String node, @JsonPropertyDescription("任务") String mission,
        @JsonPropertyDescription("阵营") String faction, @JsonPropertyDescription("等级") String level,
        @JsonPropertyDescription("奖励") String reward, @JsonPropertyDescription("结束") String expiry,
        @JsonPropertyDescription("剩余") String remaining) {
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @CommandTextInline
    public record SortieVO(@JsonPropertyDescription("目标") String boss, @JsonPropertyDescription("结束") String expiry,
        @JsonPropertyDescription("剩余") String remaining, @JsonPropertyDescription("任务") List<VariantVO> variants) {
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record VariantVO(@JsonPropertyDescription("节点") String node,
        @JsonPropertyDescription("任务") String missionType, @JsonPropertyDescription("条件") String modifier) {
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record MissionVO(@JsonPropertyDescription("节点") String node,
        @JsonPropertyDescription("任务") String missionType, @JsonPropertyDescription("遗物等级") String tier,
        @JsonPropertyDescription("精华") String essence, @JsonPropertyDescription("结束") String expiry,
        @JsonPropertyDescription("剩余") String remaining) {
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record ArbitrationMissionVO(@JsonPropertyDescription("节点") String node,
        @JsonPropertyDescription("任务") String missionType, @JsonPropertyDescription("阵营") String faction,
        @JsonPropertyDescription("精华/小时（社区参考）") String essence, @JsonPropertyDescription("开始") String activation,
        @JsonPropertyDescription("结束") String expiry) {
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record CycleVO(@JsonPropertyDescription("地点") String name, @JsonPropertyDescription("状态") String state,
        @JsonPropertyDescription("结束") String expiry, @JsonPropertyDescription("剩余") String remaining) {
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @CommandTextInline
    public record TraderVO(@JsonPropertyDescription("地点") String location, @JsonPropertyDescription("状态") String status,
        @JsonPropertyDescription("到达") String activation, @JsonPropertyDescription("离开") String expiry,
        @JsonPropertyDescription("库存") List<ProductVO> inventory) {
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record ProductVO(@JsonPropertyDescription("物品") String item, @JsonPropertyDescription("杜卡德金币") String ducats,
        @JsonPropertyDescription("现金") String credits) {
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record DealVO(@JsonPropertyDescription("物品") String item, @JsonPropertyDescription("现金") String credits,
        @JsonPropertyDescription("白金") String platinum, @JsonPropertyDescription("总库存") String stock,
        @JsonPropertyDescription("已售") String sold, @JsonPropertyDescription("剩余") String remaining) {
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record InvasionVO(@JsonPropertyDescription("节点") String node,
        @JsonPropertyDescription("进度（%）") String completion, @JsonPropertyDescription("进攻方奖励") String attackerReward,
        @JsonPropertyDescription("防守方奖励") String defenderReward,
        @JsonPropertyDescription("进攻方任务") String attackerMissions,
        @JsonPropertyDescription("防守方任务") String defenderMissions) {
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record OfferingVO(@JsonPropertyDescription("节点") String node, @JsonPropertyDescription("奖励") String reward,
        @JsonPropertyDescription("结束") String expiry, @JsonPropertyDescription("剩余") String remaining) {
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @CommandTextInline
    public record BountyVO(@JsonPropertyDescription("集团") String syndicate,
        @JsonPropertyDescription("节点") List<String> nodes, @JsonPropertyDescription("刷新") String expiry,
        @JsonPropertyDescription("剩余") String remaining, @JsonPropertyDescription("赏金") List<JobVO> jobs,
        @JsonPropertyDescription("当前任务（社区补充）") List<SupplementalBountyVO> supplementalJobs,
        @JsonPropertyDescription("静态候选池") List<BountyRewardPoolVO> staticRewardPools,
        @JsonPropertyDescription("任务来源说明") String supplementNotice) {
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @CommandTextInline
    public record SupplementalBountyVO(@JsonPropertyDescription("节点") String node,
        @JsonPropertyDescription("任务") String missionType, @JsonPropertyDescription("条件") String challenge,
        @JsonPropertyDescription("说明") String description, @JsonPropertyDescription("队友") String ally) {
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @CommandTextInline
    public record BountyRewardPoolVO(@JsonPropertyDescription("候选池") String name,
        @JsonPropertyDescription("候选奖励") List<String> rewards) {
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @CommandTextInline
    public record JobVO(@JsonPropertyDescription("任务") String type, @JsonPropertyDescription("说明") String description,
        @JsonPropertyDescription("等级") String level, @JsonPropertyDescription("最低段位") String minMR,
        @JsonPropertyDescription("各阶段声望") List<?> standingStages, @JsonPropertyDescription("区域") String location,
        @JsonPropertyDescription("剩余") String remaining, @JsonPropertyDescription("奖励") List<RewardVO> rewards,
        @JsonPropertyDescription("奖励说明") String rewardNotice) {
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record RewardVO(@JsonPropertyDescription("物品") String item, @JsonPropertyDescription("数量") String count,
        @JsonPropertyDescription("概率（%）") String chance, @JsonPropertyDescription("阶段") String stage,
        @JsonPropertyDescription("稀有度") String rarity) {
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record ChallengeVO(@JsonPropertyDescription("任务") String title,
        @JsonPropertyDescription("要求") String description, @JsonPropertyDescription("声望") String reputation,
        @JsonPropertyDescription("类型") String frequency) {
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record ConstructionVO(@JsonPropertyDescription("项目") String name,
        @JsonPropertyDescription("建造进度（%）") String progress) {
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record SimarisTargetVO(@JsonPropertyDescription("公共研究目标") String target,
        @JsonPropertyDescription("状态") String status) {
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record OutpostVO(@JsonPropertyDescription("节点") String node,
        @JsonPropertyDescription("开始") String activation, @JsonPropertyDescription("结束") String expiry,
        @JsonPropertyDescription("剩余") String remaining) {
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record ClanInitiativeVO(@JsonPropertyDescription("周次") String week,
        @JsonPropertyDescription("奖励区域") String region, @JsonPropertyDescription("奖励") List<ClanRewardVO> rewards) {
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record ClanRewardVO(@JsonPropertyDescription("物品") String item, @JsonPropertyDescription("数量") String count,
        @JsonPropertyDescription("积分门槛") String points, @JsonPropertyDescription("上游领取标记") String claimed) {
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record CircuitRewardVO(@JsonPropertyDescription("本周选项") String item) {
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record CalendarDayVO(@JsonPropertyDescription("游戏内日期") String date,
        @JsonPropertyDescription("事件") List<CalendarEventVO> events) {
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record CalendarEventVO(@JsonPropertyDescription("挑战") String challenge,
        @JsonPropertyDescription("说明") String description, @JsonPropertyDescription("奖励") String reward) {
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record VaultTraderVO(@JsonPropertyDescription("地点") String location,
        @JsonPropertyDescription("开始") String activation, @JsonPropertyDescription("结束") String expiry,
        @JsonPropertyDescription("库存") List<VaultProductVO> inventory) {
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record VaultProductVO(@JsonPropertyDescription("物品") String item,
        @JsonPropertyDescription("PrimePrice（上游未声明币种）") String primePrice,
        @JsonPropertyDescription("RegularPrice（上游未声明币种）") String regularPrice) {
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record WorldTimestampVO(@JsonPropertyDescription("快照时间") String timestamp) {
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record NewsVO(@JsonPropertyDescription("标题") String title, @JsonPropertyDescription("链接") String link,
        @JsonPropertyDescription("发布") String publishedAt) {
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record FlashSaleVO(@JsonPropertyDescription("物品") String item,
        @JsonPropertyDescription("优惠（%）") String discount, @JsonPropertyDescription("现金价格") String credits,
        @JsonPropertyDescription("白金价格") String platinum, @JsonPropertyDescription("结束") String expiry,
        @JsonPropertyDescription("剩余") String remaining) {
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record PersistentEnemyVO(@JsonPropertyDescription("敌人") String agent,
        @JsonPropertyDescription("等级") String rank, @JsonPropertyDescription("生命（%）") String health,
        @JsonPropertyDescription("最近发现地点") String location, @JsonPropertyDescription("最近发现时间") String discoveredAt,
        @JsonPropertyDescription("已发现") Boolean discovered) {
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record ArchimedeaVO(@JsonPropertyDescription("类型") String type, @JsonPropertyDescription("结束") String expiry,
        @JsonPropertyDescription("剩余") String remaining,
        @JsonPropertyDescription("个人词缀") List<String> personalModifiers,
        @JsonPropertyDescription("任务") List<ArchimedeaMissionVO> missions) {
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record ArchimedeaMissionVO(@JsonPropertyDescription("阵营") String faction,
        @JsonPropertyDescription("任务") String missionType, @JsonPropertyDescription("偏差") String deviation,
        @JsonPropertyDescription("风险") List<String> risks) {
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record WorldStateEntryVO(@JsonPropertyDescription("标题") String title,
        @JsonPropertyDescription("说明") String description, @JsonPropertyDescription("节点") String node,
        @JsonPropertyDescription("进度") String progress, @JsonPropertyDescription("目标") String goal,
        @JsonPropertyDescription("奖励") List<RewardVO> rewards, @JsonPropertyDescription("数值") String value,
        @JsonPropertyDescription("结束") String expiry, @JsonPropertyDescription("剩余") String remaining) {
    }
}
