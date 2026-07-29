package com.nnm.bot.warframe.infrastructure.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.warframe.domain.model.WorldState;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorldStateParserTest {

    private final WorldStateParser parser = new WorldStateParser(new ObjectMapper());

    @Test
    void mapsKnownSectionsWithoutLeakingJsonNodes() {
        WorldState state = parser.parse("""
                {
                  "timestamp": "2026-07-29T02:00:00Z",
                  "alerts": [{"id":"a1","activation":"2026-07-29T01:00:00Z","expiry":"2026-07-29T03:00:00Z",
                    "mission":{"node":"Earth1","type":"Capture","faction":"Grineer"},
                    "reward":{"asString":"Credits","items":["/Lotus/Items/Credits"]}}],
                  "fissures": [{"id":"f1","node":"Earth1","tier":"Lith","missionType":"Capture",
                    "activation":1722218400,"expiry":1722222000}],
                  "sorties": [{"id":"s1","boss":"Kela De Thaym","variants":[{"missionType":"Exterminate","modifier":"Cold","node":"Mars1"}],
                    "expiry":"2026-07-30T02:00:00Z"}],
                  "voidTraders": [{"location":"Pluto1","active":true,"manifest":[{"item":"Primed Mod","ducats":100,"credits":20000}]}],
                  "dailyDeals": [{"id":"d1","item":"Kavat","stock":10,"sold":2,"credits":1000,"platinum":5}]
                }
                """);

        assertThat(state.alerts()).hasSize(1);
        assertThat(state.alerts().getFirst().node()).isEqualTo("Earth1");
        assertThat(state.alerts().getFirst().rewardItems()).containsExactly("/Lotus/Items/Credits");
        assertThat(state.fissures()).singleElement().extracting(WorldState.Fissure::tier).isEqualTo("Lith");
        assertThat(state.allSorties()).singleElement().extracting(WorldState.Sortie::boss).isEqualTo("Kela De Thaym");
        assertThat(state.allVoidTraders()).singleElement().extracting(WorldState.VoidTrader::location).isEqualTo("Pluto1");
        assertThat(state.dailyDeals()).singleElement().extracting(WorldState.DailyDeal::item).isEqualTo("Kavat");
        assertThat(state.metadata().contentDigest()).hasSize(64);
        assertThat(state.timestamp()).isEqualTo(Instant.parse("2026-07-29T02:00:00Z"));
    }

    @Test
    void acceptsOfficialObjectOrSingleItemAliasesAndMissingSections() {
        WorldState state = parser.parse("""
                {"Time":{"$date":{"$numberLong":"1722218400000"}},
                 "Sorties":[],
                 "Sortie":{"_id":{"$oid":"s1"},"boss":"Natah"},
                 "voidTrader":{"node":"Saturn1","activation":1722218400,"expiry":1722222000},
                 "cetusCycle":{"isDay":true,"expiry":1722222000},
                 "DailyDeals":[{"StoreItem":"/Lotus/Store/Kavat","AmountTotal":10,"AmountSold":2,"OriginalPrice":1000,"SalePrice":5}],
                 "Events":[{"_id":{"$oid":"e1"},"Messages":[{"LanguageCode":"en","Message":"News"},{"LanguageCode":"zh","Message":"Chinese news"}]}],
                 "Goals":[{"_id":{"$oid":"g1"},"Desc":"Event name","ToolTip":"Event details","Count":23,"Goal":100,
                    "Reward":{"items":["/Lotus/StoreItems/EventReward"]}}],
                 "SeasonInfo":{"ActiveChallenges":[{"Challenge":"WeeklyMission","Daily":true}]},
                 "ActiveMissions":[{"_id":{"$oid":"f1"},"Node":"Earth1","MissionType":"Capture","Modifier":"VoidT1"}],
                 "VoidStorms":[{"_id":{"$oid":"v1"},"Node":"Railjack1","ActiveMissionTier":"VoidT2"}],
                 "LiteSorties":[{"_id":{"$oid":"l1"},"Boss":"Archon Amar","Missions":[{"modifierType":"Cold"}]}]}
                """, "official");

        assertThat(state.allSorties()).hasSize(1);
        assertThat(state.allSorties().getFirst().id()).isEqualTo("s1");
        assertThat(state.allVoidTraders()).hasSize(1);
        assertThat(state.cetusCycle().isDay()).isTrue();
        assertThat(state.metadata().source()).isEqualTo("official");
        assertThat(state.timestamp()).isEqualTo(Instant.ofEpochMilli(1722218400000L));
        assertThat(state.dailyDeals().getFirst().item()).isEqualTo("/Lotus/Store/Kavat");
        assertThat(state.news().getFirst().title()).isEqualTo("Chinese news");
        assertThat(state.events().getFirst().title()).isEqualTo("Event name");
        assertThat(state.events().getFirst().progress()).isEqualTo(23.0);
        assertThat(state.events().getFirst().rewards()).containsExactly("/Lotus/StoreItems/EventReward");
        assertThat(state.nightwave()).singleElement().extracting(WorldState.NightwaveMission::reputation)
                .isEqualTo(0);
        assertThat(state.allActiveMissions()).anyMatch(WorldState.ActiveMission::voidStorm);
        assertThat(state.archonHunts()).singleElement().extracting(WorldState.Sortie::boss)
                .isEqualTo("Archon Amar");
        assertThat(state.archonHunts().getFirst().variants().getFirst().modifier()).isEqualTo("Cold");
    }

    @Test
    void recognizesEverySupportedStandaloneSectionAndUsesCanonicalDigest() {
        WorldState compact = parser.parse("{\"VallisCycle\":{\"isWarm\":true}}", "test");
        WorldState formatted = parser.parse("""
                {
                  "VallisCycle": {
                    "isWarm": true
                  }
                }
                """, "test");

        assertThat(compact.vallisCycle().isWarm()).isTrue();
        assertThat(compact.metadata().contentDigest()).isEqualTo(formatted.metadata().contentDigest());
    }

    @Test
    void rejectsBlankOrNonObjectResponses() {
        assertThatThrownBy(() -> parser.parse(" "))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> parser.parse("[]"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("JSON object");
        assertThatThrownBy(() -> parser.parse("{}"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no recognized data");
    }
}
