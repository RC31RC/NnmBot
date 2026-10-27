package com.nnm.bot.warframe.plugin.worldstate;

import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateCommandVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateDisplayMapper;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;
import com.nnm.bot.warframe.runtime.mapper.worldstate.OfficialWorldStateMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ReferenceWorldStateCommandsTest {
    private final ObjectMapper mapper = new ObjectMapper();

    private WorldStateDTO parse(String json) {
        OfficialWorldStateMapper worldStateMapper = new OfficialWorldStateMapper();
        ReflectionTestUtils.setField(worldStateMapper, "objectMapper", mapper);
        return worldStateMapper.map(json);
    }

    @Test
    void officialConclaveCategoriesAndDatesRetainAmountAndFilterRoot() {
        WorldStateDTO state = parse("""
                {"PVPChallengeInstances":[
                  {"Category":"PVPChallengeTypeCategory_DAILY","PVPMode":"PVPMODE_ALL",
                   "challengeTypeRefID":"/Lotus/PVP/PVPTimedChallengeKillsPrimaryEASY",
                   "params":[{"v":7}],"startDate":{"$date":{"$numberLong":"1788825600000"}},
                   "endDate":{"$date":{"$numberLong":"1788912000000"}}},
                  {"Category":"PVPChallengeTypeCategory_WEEKLY_ROOT"}]}
                """);
        assertThat(state.conclaveChallenges().getFirst()).satisfies(challenge -> {
            assertThat(challenge.daily()).isTrue();
            assertThat(challenge.amount()).isEqualTo(7);
            assertThat(challenge.expiry()).isEqualTo(Instant.ofEpochMilli(1788912000000L));
        });
        assertThat(state.conclaveChallenges().getLast().rootChallenge()).isTrue();
        WarframeWorldStateCommandVO output = WarframeWorldStateProjector.conclave(state);
        assertThat((List<?>) output.data().get("items")).hasSize(1);
        WarframeWorldStateVO display = WarframeWorldStateDisplayMapper.from("conclave", output.data());
        assertThat(mapper.valueToTree(display.items()).get(0).path("amount").asText()).isEqualTo("7");
        assertThat(display.unresolvedReferences()).extracting("reference")
                .contains("PVPMODE_ALL", "/Lotus/PVP/PVPTimedChallengeKillsPrimaryEASY");
    }

    @Test
    void parsesOfficialSupplementaryFieldsWithoutInventingOutpostTimes() {
        WorldStateDTO state = parse("""
                {"ProjectPct":[12.25,30.5],
                 "LibraryInfo":{"LastCompletedTargetType":"/Lotus/Language/Enemy"},
                 "Tmp":"{\\"sfn\\":505}",
                 "WeeklyVaultBonusRewards":[{"WeekCount":6,"BonusRegion":"/Lotus/Language/Earth",
                   "Rewards":[{"Reward":"/Lotus/Items/Reward","ItemCount":3,
                     "PointThreshold":500,"RewardClaimed":false}]}]}
                """);
        assertThat(state.constructionProgress().fomorian()).isEqualTo(12.25);
        assertThat(state.constructionProgress().unknown()).isNull();
        assertThat(state.simaris().active()).isFalse();
        assertThat(state.sentientOutpost().node()).isEqualTo("CrewBattleNode505");
        assertThat(state.sentientOutpost().expiry()).isNull();
        assertThat(state.clanInitiatives().getFirst().rewards().getFirst().points()).isEqualTo(500);
        WarframeWorldStateVO display = WarframeWorldStateDisplayMapper.from("clan-initiative",
                WarframeWorldStateProjector.clanInitiatives(state).data());
        assertThat(mapper.valueToTree(display.items()).get(0).path("rewards").get(0)
                .path("points").asText()).isEqualTo("500");
    }

    @Test
    void circuitUsesOnlyCurrentScheduleAndKeepsOrdinaryAndSteelRewardsSeparate() {
        WorldStateDTO state = parse("""
                {"EndlessXpSchedule":[{"CategoryChoices":[
                  {"Category":"EXC_NORMAL","Choices":["Mag","Volt"]},
                  {"Category":"EXC_HARD","Choices":["AckAndBrunt","NamiSolo"]}]},
                  {"CategoryChoices":[{"Category":"EXC_HARD","Choices":["Future"]}]}]}
                """);
        assertThat((List<?>) WarframeWorldStateProjector.circuit(state, false).data().get("items"))
                .hasSize(2);
        assertThat(WarframeWorldStateProjector.circuit(state, true).data().toString())
                .contains("Ack & Brunt", "Nami Solo").doesNotContain("Mag", "Future", "expiry");
    }

    @Test
    void calendarDatesAreGameYearNotSystemYearAndVaultPricesRetainNulls() {
        WorldStateDTO state = parse("""
                {"KnownCalendarSeasons":[{"Season":"CST_SPRING","YearIteration":4,
                   "Days":[{"day":32,"events":[{"type":"CET_CHALLENGE","challenge":"/Lotus/Calendar/Test"},
                     {"type":"CET_REWARD","reward":"/Lotus/Calendar/Reward"}]}]}],
                 "PrimeVaultTraders":[{"Node":"Relay","Manifest":[{"ItemType":"/Lotus/Pack"}]}]}
                """);
        assertThat(state.calendarSeasons().getFirst().days().getFirst().date()).isEqualTo("1999-02-01");
        assertThat(state.vaultTraders().getFirst().inventory().getFirst().primePrice()).isNull();
        WarframeWorldStateVO display = WarframeWorldStateDisplayMapper.from("calendar", WarframeWorldStateProjector.calendar(state).data());
        assertThat(display.unresolvedReferences()).extracting("reference")
                .contains("/Lotus/Calendar/Test", "/Lotus/Calendar/Reward");
        assertThat(mapper.valueToTree(display.items()).get(0).path("date").asText()).isEqualTo("1999-02-01");
    }

    @Test
    void newsPrefersChineseAndPreservesLinksAndSalesPreservePrices() {
        WorldStateDTO state = parse("""
                {"News":[{"message":"English","translations":{"zh":"中文新闻"},
                   "link":"https://example.test/news","date":"2026-09-01T00:00:00Z"}],
                 "FlashSales":[{"TypeName":"/Lotus/Items/Sale","Discount":50,
                   "RegularOverride":100,"PremiumOverride":25}]}
                """);
        assertThat(state.news().getFirst().title()).isEqualTo("中文新闻");
        WarframeWorldStateVO news = WarframeWorldStateDisplayMapper.from("news", WarframeWorldStateProjector.news(state).data());
        assertThat(mapper.valueToTree(news.items()).get(0).path("link").asText())
                .isEqualTo("https://example.test/news");
        WarframeWorldStateVO sale = WarframeWorldStateDisplayMapper.from("flash-sales", WarframeWorldStateProjector.flashSales(state).data());
        assertThat(mapper.valueToTree(sale.items()).get(0).path("platinum").asText()).isEqualTo("25");
    }

    @Test
    void malformedTmpDoesNotBreakOtherSectionsAndEmptySnapshotsStayEmpty() {
        WorldStateDTO state = parse("{\"Time\":1788912000,\"Tmp\":\"broken\"}");
        assertThat(state.sentientOutpost()).isNull();
        assertThat(WarframeWorldStateProjector.simaris(state).data().get("available")).isEqualTo(false);
        for (String command : List.of("calendar", "vault-trader", "clan-initiative", "conclave")) {
            assertThat(WarframeWorldStateDisplayMapper.from(command, Map.of("items", List.of())).items())
                    .isEmpty();
        }
    }
}
