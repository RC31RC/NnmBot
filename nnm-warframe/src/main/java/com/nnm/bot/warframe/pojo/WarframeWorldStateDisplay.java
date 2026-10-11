package com.nnm.bot.warframe.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.nnm.bot.core.plugin.annotation.CommandTextInline;

import java.util.List;

/**
 * 世界状态命令的展示协议。仅保留玩家需要的信息，原始引用及抓取元数据留在内部投影中。
 * 各输出模式共用这些对象，行内注解只影响文字布局，不删减字段。
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record WarframeWorldStateDisplay(
        @JsonPropertyDescription("项目")
        List<?> items,
        @JsonPropertyDescription("提示")
        String notice,
        @JsonPropertyDescription("未解析引用")
        List<UnresolvedReference> unresolvedReferences) {

    public WarframeWorldStateDisplay(List<?> items, String notice) {
        this(items, notice, List.of());
    }

    public WarframeWorldStateDisplay {
        items = items == null ? List.of() : List.copyOf(items);
        unresolvedReferences = unresolvedReferences == null
                ? List.of() : List.copyOf(unresolvedReferences);
    }

    /**
     * 供排查数据源使用的最小原始引用记录。原始值只出现在这个列表中，
     * 不再进入玩家正常阅读的任务、节点或奖励字段。
     */
    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record UnresolvedReference(
            @JsonPropertyDescription("命令")
            String command,
            @JsonPropertyDescription("字段")
            String fieldPath,
            @JsonPropertyDescription("原始值")
            String reference,
            @JsonPropertyDescription("状态")
            String status,
            @JsonPropertyDescription("候选")
            List<String> candidates) {

        public UnresolvedReference {
            command = command == null ? "" : command;
            fieldPath = fieldPath == null ? "" : fieldPath;
            reference = reference == null ? "" : reference;
            status = status == null ? "" : status;
            candidates = candidates == null ? List.of() : List.copyOf(candidates);
        }
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record Alert(
            @JsonPropertyDescription("节点")
            String node,
            @JsonPropertyDescription("任务")
            String mission,
            @JsonPropertyDescription("阵营")
            String faction,
            @JsonPropertyDescription("等级")
            String level,
            @JsonPropertyDescription("奖励")
            String reward,
            @JsonPropertyDescription("结束")
            String expiry,
            @JsonPropertyDescription("剩余")
            String remaining) {}

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @CommandTextInline
    public record Sortie(
            @JsonPropertyDescription("目标")
            String boss,
            @JsonPropertyDescription("结束")
            String expiry,
            @JsonPropertyDescription("剩余")
            String remaining,
            @JsonPropertyDescription("任务")
            List<Variant> variants) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record Variant(
            @JsonPropertyDescription("节点")
            String node,
            @JsonPropertyDescription("任务")
            String missionType,
            @JsonPropertyDescription("条件")
            String modifier) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record Mission(
            @JsonPropertyDescription("节点")
            String node,
            @JsonPropertyDescription("任务")
            String missionType,
            @JsonPropertyDescription("遗物等级")
            String tier,
            @JsonPropertyDescription("精华")
            String essence,
            @JsonPropertyDescription("结束")
            String expiry,
            @JsonPropertyDescription("剩余")
            String remaining) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record ArbitrationMission(
            @JsonPropertyDescription("节点")
            String node,
            @JsonPropertyDescription("任务")
            String missionType,
            @JsonPropertyDescription("阵营")
            String faction,
            @JsonPropertyDescription("精华/小时（社区参考）")
            String essence,
            @JsonPropertyDescription("开始")
            String activation,
            @JsonPropertyDescription("结束")
            String expiry) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record Cycle(
            @JsonPropertyDescription("地点")
            String name,
            @JsonPropertyDescription("状态")
            String state,
            @JsonPropertyDescription("结束")
            String expiry,
            @JsonPropertyDescription("剩余")
            String remaining) {}

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @CommandTextInline
    public record Trader(
            @JsonPropertyDescription("地点")
            String location,
            @JsonPropertyDescription("状态")
            String status,
            @JsonPropertyDescription("到达")
            String activation,
            @JsonPropertyDescription("离开")
            String expiry,
            @JsonPropertyDescription("库存")
            List<Product> inventory) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record Product(
            @JsonPropertyDescription("物品")
            String item,
            @JsonPropertyDescription("杜卡德金币")
            String ducats,
            @JsonPropertyDescription("现金")
            String credits) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record Deal(
            @JsonPropertyDescription("物品")
            String item,
            @JsonPropertyDescription("现金")
            String credits,
            @JsonPropertyDescription("白金")
            String platinum,
            @JsonPropertyDescription("总库存")
            String stock,
            @JsonPropertyDescription("已售")
            String sold,
            @JsonPropertyDescription("剩余")
            String remaining) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record Invasion(
            @JsonPropertyDescription("节点")
            String node,
            @JsonPropertyDescription("进度（%）")
            String completion,
            @JsonPropertyDescription("进攻方奖励")
            String attackerReward,
            @JsonPropertyDescription("防守方奖励")
            String defenderReward) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record Offering(
            @JsonPropertyDescription("节点")
            String node,
            @JsonPropertyDescription("奖励")
            String reward,
            @JsonPropertyDescription("结束")
            String expiry,
            @JsonPropertyDescription("剩余")
            String remaining) {}

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @CommandTextInline
    public record Bounty(
            @JsonPropertyDescription("集团")
            String syndicate,
            @JsonPropertyDescription("节点")
            List<String> nodes,
            @JsonPropertyDescription("刷新")
            String expiry,
            @JsonPropertyDescription("剩余")
            String remaining,
            @JsonPropertyDescription("赏金")
            List<Job> jobs) {}

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @CommandTextInline
    public record Job(
            @JsonPropertyDescription("任务")
            String type,
            @JsonPropertyDescription("说明")
            String description,
            @JsonPropertyDescription("等级")
            String level,
            @JsonPropertyDescription("最低段位")
            String minMR,
            @JsonPropertyDescription("各阶段声望")
            List<?> standingStages,
            @JsonPropertyDescription("区域")
            String location,
            @JsonPropertyDescription("剩余")
            String remaining,
            @JsonPropertyDescription("奖励")
            List<Reward> rewards,
            @JsonPropertyDescription("奖励说明")
            String rewardNotice) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record Reward(
            @JsonPropertyDescription("物品")
            String item,
            @JsonPropertyDescription("数量")
            String count,
            @JsonPropertyDescription("概率（%）")
            String chance,
            @JsonPropertyDescription("阶段")
            String stage,
            @JsonPropertyDescription("稀有度")
            String rarity) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record Challenge(
            @JsonPropertyDescription("任务")
            String title,
            @JsonPropertyDescription("要求")
            String description,
            @JsonPropertyDescription("声望")
            String reputation,
            @JsonPropertyDescription("类型")
            String frequency) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record Conclave(
            @JsonPropertyDescription("挑战")
            String title,
            @JsonPropertyDescription("要求")
            String description,
            @JsonPropertyDescription("模式")
            String mode,
            @JsonPropertyDescription("分类")
            String category,
            @JsonPropertyDescription("目标数量")
            String amount,
            @JsonPropertyDescription("声望")
            String standing,
            @JsonPropertyDescription("结束")
            String expiry,
            @JsonPropertyDescription("剩余")
            String remaining) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record Construction(
            @JsonPropertyDescription("项目")
            String name,
            @JsonPropertyDescription("建造进度（%）")
            String progress) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record SimarisTarget(
            @JsonPropertyDescription("公共研究目标")
            String target,
            @JsonPropertyDescription("状态")
            String status) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record Outpost(
            @JsonPropertyDescription("节点")
            String node,
            @JsonPropertyDescription("开始")
            String activation,
            @JsonPropertyDescription("结束")
            String expiry,
            @JsonPropertyDescription("剩余")
            String remaining) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record ClanInitiative(
            @JsonPropertyDescription("周次")
            String week,
            @JsonPropertyDescription("奖励区域")
            String region,
            @JsonPropertyDescription("奖励")
            List<ClanReward> rewards) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record ClanReward(
            @JsonPropertyDescription("物品")
            String item,
            @JsonPropertyDescription("数量")
            String count,
            @JsonPropertyDescription("积分门槛")
            String points,
            @JsonPropertyDescription("上游领取标记")
            String claimed) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record CircuitReward(
            @JsonPropertyDescription("本周选项")
            String item) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record CalendarDay(
            @JsonPropertyDescription("游戏内日期")
            String date,
            @JsonPropertyDescription("循环次数")
            String iteration,
            @JsonPropertyDescription("事件")
            List<CalendarEvent> events) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record CalendarEvent(
            @JsonPropertyDescription("任务或增益")
            String title,
            @JsonPropertyDescription("说明")
            String description,
            @JsonPropertyDescription("奖励")
            String reward,
            @JsonPropertyDescription("剧情人物")
            String dialogueName) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record VaultTrader(
            @JsonPropertyDescription("地点")
            String location,
            @JsonPropertyDescription("开始")
            String activation,
            @JsonPropertyDescription("结束")
            String expiry,
            @JsonPropertyDescription("库存")
            List<VaultProduct> inventory) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record VaultProduct(
            @JsonPropertyDescription("物品")
            String item,
            @JsonPropertyDescription("PrimePrice（上游未声明币种）")
            String primePrice,
            @JsonPropertyDescription("RegularPrice（上游未声明币种）")
            String regularPrice) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record WorldTimestamp(
            @JsonPropertyDescription("快照时间")
            String timestamp) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record News(
            @JsonPropertyDescription("标题")
            String title,
            @JsonPropertyDescription("链接")
            String link,
            @JsonPropertyDescription("发布")
            String publishedAt) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record FlashSale(
            @JsonPropertyDescription("物品")
            String item,
            @JsonPropertyDescription("优惠（%）")
            String discount,
            @JsonPropertyDescription("现金价格")
            String credits,
            @JsonPropertyDescription("白金价格")
            String platinum,
            @JsonPropertyDescription("结束")
            String expiry,
            @JsonPropertyDescription("剩余")
            String remaining) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record PersistentEnemy(
            @JsonPropertyDescription("敌人")
            String agent,
            @JsonPropertyDescription("等级")
            String rank,
            @JsonPropertyDescription("生命（%）")
            String health,
            @JsonPropertyDescription("最近发现地点")
            String location,
            @JsonPropertyDescription("最近发现时间")
            String discoveredAt,
            @JsonPropertyDescription("已发现")
            Boolean discovered) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record Archimedea(
            @JsonPropertyDescription("类型")
            String type,
            @JsonPropertyDescription("结束")
            String expiry,
            @JsonPropertyDescription("剩余")
            String remaining,
            @JsonPropertyDescription("个人词缀")
            List<String> personalModifiers,
            @JsonPropertyDescription("任务")
            List<ArchimedeaMission> missions) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record ArchimedeaMission(
            @JsonPropertyDescription("阵营")
            String faction,
            @JsonPropertyDescription("任务")
            String missionType,
            @JsonPropertyDescription("偏差")
            String deviation,
            @JsonPropertyDescription("风险")
            List<String> risks) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record WorldStateEntry(
            @JsonPropertyDescription("标题")
            String title,
            @JsonPropertyDescription("说明")
            String description,
            @JsonPropertyDescription("节点")
            String node,
            @JsonPropertyDescription("进度")
            String progress,
            @JsonPropertyDescription("目标")
            String goal,
            @JsonPropertyDescription("奖励")
            List<Reward> rewards,
            @JsonPropertyDescription("数值")
            String value,
            @JsonPropertyDescription("结束")
            String expiry,
            @JsonPropertyDescription("剩余")
            String remaining) {}
}
