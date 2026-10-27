package com.nnm.bot.warframe.plugin.worldstate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.core.plugin.response.CommandDataTextFormatter;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateDisplayMapper;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class WorldStateDisplayDataTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @ParameterizedTest
    @ValueSource(strings = {"alerts", "sorties", "archon-hunts", "arbitration",
            "arbitration-table", "fissures", "steel-fissures", "void-storms", "cycles",
            "duviri-cycle", "void-trader", "daily-deals", "invasions", "steel-path",
            "ostrons", "solaris", "entrati", "nightwave", "cavia", "holdfasts", "hex-bounties",
            "archimedeas", "deep-archimedeas", "hex-archimedeas", "conclave", "news",
            "flash-sales", "persistent-enemies", "syndicate-missions", "construction-progress",
            "simaris", "sentient-outposts", "clan-initiative", "circuit-normal", "circuit-hard",
            "calendar", "vault-trader", "timestamp", "events", "global-upgrades"})
    void coversEveryExistingWorldStateCommandAndRetainsStaleNotice(String command) {
        WarframeWorldStateVO display = WarframeWorldStateDisplayMapper.from(command,
                Map.of("items", List.of(), "stale", true, "source", "internal"));
        assertThat(display.items()).isEmpty();
        assertThat(display.notice()).contains("缓存数据");
        assertThat(mapper.valueToTree(display).has("source")).isFalse();
    }

    @Test
    void selectsAlertFieldsAndDoesNotRepeatRewardItems() {
        WarframeWorldStateVO display = WarframeWorldStateDisplayMapper.from("alerts", Map.of(
                "source", "warframe-official", "items", List.of(Map.of(
                        "id", "internal-id", "node", "外侧终点（冥王星）",
                        "mission", "防御", "faction", "Corpus",
                        "minEnemyLevel", 10, "maxEnemyLevel", 15,
                        "reward", "Umbra Forma 蓝图, 10000 现金",
                        "rewardItems", List.of("Umbra Forma 蓝图"),
                        "remaining", "35分钟"))));

        String text = format(display);
        assertThat(text).contains("节点: 外侧终点（冥王星） | 任务: 防御",
                "等级: 10-15", "剩余: 35分钟").containsOnlyOnce("Umbra Forma 蓝图")
                .doesNotContain("internal-id", "warframe-official", "rewardItems", "未提供");
        assertThat(mapper.valueToTree(display).path("items").get(0).path("reward").asText())
                .isEqualTo("Umbra Forma 蓝图, 10000 现金");
    }

    @Test
    void rendersStructuredAlertRewardCountWithoutRepeatingRewardItems() {
        WarframeWorldStateVO display = WarframeWorldStateDisplayMapper.from("alerts", Map.of("items",
                List.of(Map.of("node", "地球 - 测试节点", "mission", "生存",
                        "faction", "感染者", "reward",
                        "合成材料 x75, 10000 现金",
                        "rewardItems", List.of(Map.of("item", "合成材料", "count", 75))))));

        assertThat(format(display)).contains("奖励: 合成材料 x75, 10000 现金")
                .containsOnlyOnce("合成材料 x75");
    }

    @Test
    void retainsSortieVariantsUnderCompactHeader() {
        WarframeWorldStateVO display = WarframeWorldStateDisplayMapper.from("sorties", Map.of(
                "title", "突击", "items", List.of(Map.of(
                        "boss", "Lephantis", "remaining", "5小时",
                        "variants", List.of(
                                Map.of("node", "Dirus", "missionType", "破坏", "modifier", "辐射灾害"),
                                Map.of("node", "Acheron", "missionType", "移动防御",
                                        "modifier", "敌人护甲强化"))))));
        assertThat(format(display)).contains("目标: Lephantis", "任务:",
                "节点: Dirus | 任务: 破坏 | 条件: 辐射灾害",
                "节点: Acheron | 任务: 移动防御 | 条件: 敌人护甲强化")
                .doesNotContain("title:", "variants:", "未提供");
    }

    @Test
    void retainsBountyDetailsAndRewardsInBothSerializedAndTextData() {
        WarframeWorldStateVO display = WarframeWorldStateDisplayMapper.from("solaris", Map.of("items",
                List.of(Map.of("syndicate", "索拉里斯联盟", "remaining", "37分钟",
                        "jobs", List.of(Map.of(
                                "type", "物资回收", "description", "取回被扣押的物资。",
                                "minEnemyLevel", 5, "maxEnemyLevel", 15,
                                "standingStages", List.of(100, 200),
                                "rewards", List.of(Map.of("item", "奥罗金电池",
                                        "count", 2, "chance", 25, "stage", 1))))))));
        assertThat(format(display)).contains("集团: 索拉里斯联盟", "任务: 物资回收",
                "说明: 取回被扣押的物资。", "等级: 5-15", "各阶段声望: 100、200",
                "物品: 奥罗金电池 | 数量: 2 | 概率（%）: 25 | 阶段: 1")
                .doesNotContain("jobType", "rewardPoolReference", "未提供");
        assertThat(mapper.valueToTree(display).path("items").get(0).path("jobs").get(0)
                .path("rewards").get(0).path("item").asText()).isEqualTo("奥罗金电池");
    }

    @Test
    void supplementalBountyTextShowsResolvedFieldsAndLabelsCandidatePools() {
        WarframeWorldStateVO display = WarframeWorldStateDisplayMapper.from("holdfasts", Map.of("items", List.of(Map.of(
                "syndicate", "坚守者", "jobs", List.of(),
                "supplementStatus", "expiry_matches_official_php",
                "supplementalJobs", List.of(Map.of("node", "SolNode231", "nodeName", "哈拉科防线",
                        "missionType", "歼灭", "challenge", "/Lotus/Types/Challenges/Test",
                        "challengeName", "高效歼灭", "challengeDescription", "在 6 分钟内完成任务",
                        "ally", "/Lotus/Types/UnknownAlly", "resolutionStatus", "ally_unresolved")),
                "staticRewardPools", List.of(Map.of("poolId", "internal-pool", "name", "等级 50-55",
                        "rewards", List.of("内融核心")))))));
        assertThat(format(display)).contains("哈拉科防线", "高效歼灭", "在 6 分钟内完成任务",
                "社区补充", "静态候选池", "候选奖励", "不代表本轮必得奖励")
                .doesNotContain("internal-pool", "UnknownAlly", "SolNode231", "ally_unresolved");
    }

    @Test
    void filtersCompletedInvasionsAndOtherCyclesFromDuviri() {
        WarframeWorldStateVO invasions = WarframeWorldStateDisplayMapper.from("invasions", Map.of("items",
                List.of(Map.of("node", "旧节点", "completed", true),
                        Map.of("node", "当前节点", "completed", false))));
        assertThat(format(invasions)).contains("当前节点").doesNotContain("旧节点");

        WarframeWorldStateVO cycles = WarframeWorldStateDisplayMapper.from("duviri-cycle", Map.of("items",
                List.of(Map.of("name", "地球", "value", Map.of("value", "白天")),
                        Map.of("name", "双衍王境", "value", Map.of("value", "喜悦")))));
        assertThat(format(cycles)).contains("双衍王境", "喜悦").doesNotContain("地球");
    }

    @Test
    void rendersStructuredInvasionRewardCountsWithoutInternalReferences() {
        WarframeWorldStateVO display = WarframeWorldStateDisplayMapper.from("invasions", Map.of("items",
                List.of(Map.of("node", "地球 - 测试节点", "completion", 93.3,
                        "attackerRewards", List.of(Map.of("item", "奥罗金电池", "count", 3)),
                        "defenderRewards", List.of(Map.of("item", "费洛蒙样本", "count", 3))))));

        assertThat(format(display)).contains("进攻方奖励: 奥罗金电池 x3",
                "防守方奖励: 费洛蒙样本 x3");
    }

    @Test
    void neverExposesInternalReferencesWhenReferenceEnrichmentHasNoMatch() {
        WarframeWorldStateVO display = WarframeWorldStateDisplayMapper.from("alerts", Map.of("items",
                List.of(Map.of(
                        "node", "/Lotus/Language/Locations/UnknownNode",
                        "mission", "MT_DEFENSE",
                        "faction", "SORTIE_FACTION_UNKNOWN",
                        "rewardItems", List.of("/Lotus/StoreItems/UnknownReward")))));
        assertThat(format(display))
                .contains("节点: 未收录地点", "任务: 未收录任务", "阵营: 未收录阵营",
                        "奖励: 未收录奖励", "字段: items[0].node",
                        "原始值: /Lotus/Language/Locations/UnknownNode",
                        "原始值: MT_DEFENSE", "原始值: SORTIE_FACTION_UNKNOWN",
                        "原始值: /Lotus/StoreItems/UnknownReward")
                .doesNotContain("/Lotus/Language/Locations/UnknownNode（未解析）",
                        "MT_DEFENSE（未解析）",
                        "SORTIE_FACTION_UNKNOWN（未解析）",
                        "/Lotus/StoreItems/UnknownReward（未解析）");

        WarframeWorldStateVO sortie = WarframeWorldStateDisplayMapper.from("sorties", Map.of("items",
                List.of(Map.of(
                        "boss", "SORTIE_BOSS_UNKNOWN",
                        "variants", List.of(Map.of(
                                "node", "/Lotus/Maps/UnknownNode",
                                "missionType", "MT_SABOTAGE",
                                "modifier", "SORTIE_MODIFIER_UNKNOWN"))))));
        assertThat(format(sortie))
                .contains("目标: 未收录目标", "节点: 未收录地点",
                        "任务: 未收录任务", "条件: 未收录条件",
                        "原始值: SORTIE_BOSS_UNKNOWN",
                        "原始值: /Lotus/Maps/UnknownNode",
                        "原始值: MT_SABOTAGE",
                        "原始值: SORTIE_MODIFIER_UNKNOWN")
                .doesNotContain("SORTIE_BOSS_UNKNOWN（未解析）",
                        "/Lotus/Maps/UnknownNode（未解析）",
                        "MT_SABOTAGE（未解析）",
                        "SORTIE_MODIFIER_UNKNOWN（未解析）");
    }

    @Test
    void filtersInternalBountyLocationAndRewardPoolReferences() {
        WarframeWorldStateVO display = WarframeWorldStateDisplayMapper.from("solaris", Map.of("items",
                List.of(Map.of(
                        "syndicate", "SOLARIS_UNION",
                        "jobs", List.of(Map.of(
                                "type", "/Lotus/Types/Gameplay/Venus/Jobs/UnknownJob",
                                "locationTag", "/Lotus/Language/Locations/UnknownLocation",
                                "rewardNotice", "rewardPoolReference",
                                "rewards", List.of(Map.of(
                                        "item", "rewardPoolReference"))))))));
        assertThat(format(display))
                .contains("集团: 未收录集团", "任务: 未收录任务",
                        "区域: 未收录地点", "奖励说明: 未收录奖励",
                        "物品: 未收录奖励",
                        "原始值: SOLARIS_UNION",
                        "原始值: /Lotus/Types/Gameplay/Venus/Jobs/UnknownJob",
                        "原始值: /Lotus/Language/Locations/UnknownLocation",
                        "原始值: rewardPoolReference")
                .doesNotContain("SOLARIS_UNION（未解析）",
                        "/Lotus/Types/Gameplay/Venus/Jobs/UnknownJob（未解析）",
                        "/Lotus/Language/Locations/UnknownLocation（未解析）",
                        "rewardPoolReference（未解析）");
    }

    @Test
    void filtersInternalFissureTierReference() {
        WarframeWorldStateVO display = WarframeWorldStateDisplayMapper.from("fissures", Map.of("items",
                List.of(Map.of(
                        "node", "地球（节点）",
                        "missionType", "防御",
                        "tier", "VoidT2"))));
        assertThat(format(display))
                .contains("遗物等级: 未收录纪元", "原始值: VoidT2")
                .doesNotContain("VoidT2（未解析）");
    }

    @Test
    void rendersOfficialVoidStormAndLiteSortieFallbacksWithoutInternalReferences() {
        WarframeWorldStateVO storm = WarframeWorldStateDisplayMapper.from("void-storms", Map.of("items",
                List.of(Map.of(
                        "node", "/Lotus/Language/Locations/UnknownRailjackNode",
                        "tier", "VoidT1",
                        "remaining", "20分钟"))));
        assertThat(format(storm))
                .contains("节点: 未收录地点", "任务: 未知任务", "遗物等级: 未收录纪元",
                        "原始值: /Lotus/Language/Locations/UnknownRailjackNode",
                        "原始值: VoidT1")
                .doesNotContain("/Lotus/Language/Locations/UnknownRailjackNode（未解析）",
                        "VoidT1（未解析）");

        WarframeWorldStateVO archon = WarframeWorldStateDisplayMapper.from("archon-hunts", Map.of("items",
                List.of(Map.of("boss", "SORTIE_BOSS_AMAR", "variants", List.of(
                        Map.of("node", "SolNode11", "missionType", "MT_RESCUE"))))));
        assertThat(format(archon))
                .contains("目标: 未收录目标", "节点: SolNode11", "任务: 未收录任务",
                        "条件: 未知条件", "原始值: SORTIE_BOSS_AMAR",
                        "原始值: MT_RESCUE")
                .doesNotContain("SORTIE_BOSS_AMAR（未解析）", "MT_RESCUE（未解析）");
    }

    @Test
    void rendersOfficialVoidTraderWithoutManifestAsAnEmptyInventory() {
        WarframeWorldStateVO display = WarframeWorldStateDisplayMapper.from("void-trader", Map.of("items",
                List.of(Map.of(
                        "location", "水星中继站",
                        "active", false,
                        "activation", "09-18 18:00",
                        "expiry", "09-20 18:00",
                        "inventory", List.of()))));

        String text = format(display);
        assertThat(text).contains("地点: 水星中继站", "状态: 未到达",
                "到达: 09-18 18:00", "离开: 09-20 18:00")
                .doesNotContain("商品:", "未提供", "Manifest");
        assertThat(mapper.valueToTree(display).path("items").get(0).path("inventory")).isEmpty();
    }

    @Test
    void filtersInternalNightwaveChallengeReference() {
        WarframeWorldStateVO display = WarframeWorldStateDisplayMapper.from("nightwave", Map.of("items",
                List.of(Map.of(
                        "title", "/Lotus/Types/Challenges/Seasons/WeeklyHard/SeasonWeeklyHardFallenAngel",
                        "description", "/Lotus/Language/Nightwave/UnknownDescription",
                        "reputation", 450,
                        "daily", false))));
        assertThat(format(display))
                .contains("任务: 未收录数据", "要求: 未收录说明",
                        "声望: 450", "类型: 每周",
                        "原始值: /Lotus/Types/Challenges/Seasons/WeeklyHard/SeasonWeeklyHardFallenAngel",
                        "原始值: /Lotus/Language/Nightwave/UnknownDescription")
                .doesNotContain("/Lotus/Types/Challenges/Seasons/WeeklyHard/SeasonWeeklyHardFallenAngel（未解析）",
                        "/Lotus/Language/Nightwave/UnknownDescription（未解析）");
    }

    private String format(WarframeWorldStateVO display) {
        CommandDataTextFormatter formatter = new CommandDataTextFormatter();
        ReflectionTestUtils.setField(formatter, "objectMapper", mapper);
        return formatter.format(display, "");
    }
}
