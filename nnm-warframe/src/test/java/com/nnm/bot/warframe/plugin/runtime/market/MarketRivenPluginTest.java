package com.nnm.bot.warframe.plugin.runtime.market;

import com.fasterxml.jackson.databind.JsonNode;
import com.nnm.bot.warframe.runtime.rawdata.market.MarketResponseRawData.ItemRawData;
import com.nnm.bot.core.plugin.model.CommandResult;
import java.io.InputStream;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.warframe.runtime.mapper.market.WarframeMarketItemMapper;
import com.nnm.bot.warframe.plugin.CommandReplyTestData;
import com.nnm.bot.warframe.runtime.pojo.vo.market.WarframeMarketAuctionVO;
import com.nnm.bot.warframe.runtime.pojo.query.market.WarframeMarketAuctionQuery;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketAuctionPageDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketItemDTO;
import com.nnm.bot.warframe.runtime.service.market.WarframeMarketService;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeItemResolverService;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.WarframeRivenAttributeDTO;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeCommunityCatalogService;
import com.nnm.bot.warframe.persistent.official.service.upgrades.WarframeRivenAttributeService;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MarketRivenPluginTest {
    private final MarketRivenPlugin plugin = new MarketRivenPlugin();
    private final WarframeMarketService service = mock(WarframeMarketService.class);
    private final WarframeItemResolverService resolver = mock(WarframeItemResolverService.class);
    private final WarframeCommunityCatalogService community = mock(WarframeCommunityCatalogService.class);
    private final WarframeRivenAttributeService definitions = mock(WarframeRivenAttributeService.class);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(plugin, "marketService", service);
        ReflectionTestUtils.setField(plugin, "itemResolver", resolver);
        ReflectionTestUtils.setField(plugin, "catalog", community);
        ReflectionTestUtils.setField(plugin, "officialAttributes", definitions);
        when(definitions.definitions()).thenReturn(List.of(
                definition("multishot", false),
                definition("critical_chance", false),
                definition("critical_damage", false),
                definition("base_damage_/_melee_damage", false),
                definition("critical_chance_on_slide_attack", false),
                definition("recoil", false)));
        when(community.rivenAliases()).thenReturn(List.of(
                attribute("多重", "multishot"),
                attribute("暴击", "critical_chance"),
                attribute("爆伤", "critical_damage"),
                attribute("基双爆", "base_damage_/_melee_damage,critical_chance,critical_damage"),
                attribute("多双爆", "multishot,critical_chance,critical_damage"),
                attribute("划爆", "critical_chance_on_slide_attack"),
                attribute("后坐力", "recoil")));
        when(service.queryAuctions(any())).thenAnswer(invocation -> {
            WarframeMarketAuctionQuery query = invocation.getArgument(0);
            return Optional.of(new WarframeMarketAuctionPageDTO(query.type(), query.weaponName(),
                    query.weaponSlug(), query.positiveStats(), query.negativeStats(),
                    PageResult.of(List.of(), query.page(), 10, 0), query.requireNegative()));
        });
    }

    @Test
    void actualUpstreamAliasesRequireLocalCriticalChanceAlias() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<WarframeRivenAttributeDTO> attributes = new ArrayList<>();
        try (InputStream stream = getClass().getResourceAsStream("/warframe/market_riven_tion.json")) {
            for (JsonNode node : mapper.readTree(stream)) attributes.add(definition(node.path("url_name").asText(),
                    node.path("effect").asText(), node.path("search_only").asBoolean()));
        }
        ArrayList<WarframeCommunityRecordDTO<WarframeCommunityRecordDTO.RivenAliasDTO>> aliases = new ArrayList<WarframeCommunityRecordDTO<WarframeCommunityRecordDTO.RivenAliasDTO>>();
        try (InputStream stream = getClass().getResourceAsStream("/warframe/market_riven_tion_alias.json")) {
            for (JsonNode node : mapper.readTree(stream)) aliases.add(attribute(node.path("cn").asText(), node.path("en").asText()));
        }
        assertThat(aliases).noneMatch(record -> record.details().aliasName().equals("暴击"));
        when(definitions.definitions()).thenReturn(attributes);
        when(community.rivenAliases()).thenReturn(aliases);
        when(service.fetchAuctionWeapons("riven")).thenReturn(List.of(item("latron", "Latron")));
        when(resolver.auctionWeaponSearchTerms("拉特昂")).thenReturn(List.of("Latron"));
        assertThat(CommandReplyTestData.text(plugin.handle(invocation("拉特昂 多重 暴击 爆伤 负任意"))))
                .contains("无法识别词条").contains("暴击");
        verify(service, never()).queryAuctions(any());

        aliases.add(attribute("暴击", "critical_chance"));
        plugin.handle(invocation("拉特昂 多重 暴击 爆伤 负任意"));
        WarframeMarketAuctionQuery query = captureQuery();
        assertThat(query.positiveStats()).containsExactly("critical_chance", "critical_damage", "multishot");
        assertThat(query.requireNegative()).isTrue();
    }

    @Test
    void preservesMultiwordWeaponAndParsesAttributesAndPage() throws Exception {
        when(service.fetchAuctionWeapons("riven")).thenReturn(List.of(item("dual_toxocyst", "Dual Toxocyst")));

        CommandResult result = plugin.handle(invocation("Dual Toxocyst 暴击 负后坐力 2"));

        WarframeMarketAuctionQuery query = captureQuery();
        assertThat(query.weaponSlug()).isEqualTo("dual_toxocyst");
        assertThat(query.positiveStats()).containsExactly("critical_chance");
        assertThat(query.negativeStats()).containsExactly("recoil");
        assertThat(query.page()).isEqualTo(2);
        assertThat(CommandReplyTestData.data(result)).isInstanceOf(WarframeMarketAuctionVO.class);
    }

    @Test
    void parsesMultiwordEffectsAndAliasesAfterPrimeWeapon() throws Exception {
        when(service.fetchAuctionWeapons("riven"))
                .thenReturn(List.of(item("reaper_prime", "Reaper Prime")));
        when(definitions.definitions()).thenReturn(List.of(
                definition("critical_chance", "Critical Chance"),
                definition("critical_damage", "Critical Damage"),
                definition("recoil", "Weapon Recoil")));
        when(community.rivenAliases())
                .thenReturn(List.of(attribute("Double Critical", "critical_chance,critical_damage")));

        plugin.handle(invocation("Reaper Prime Double Critical 负Weapon Recoil 2"));

        WarframeMarketAuctionQuery query = captureQuery();
        assertThat(query.weaponSlug()).isEqualTo("reaper_prime");
        assertThat(query.positiveStats()).containsExactly("critical_chance", "critical_damage");
        assertThat(query.negativeStats()).containsExactly("recoil");
        assertThat(query.page()).isEqualTo(2);
    }

    @Test
    void negativeMarkerDoesNotLeakIntoFollowingMultiwordPositive() throws Exception {
        when(service.fetchAuctionWeapons("riven")).thenReturn(List.of(item("latron", "Latron")));
        when(definitions.definitions()).thenReturn(List.of(
                definition("critical_chance", "Critical Chance"),
                definition("critical_damage", "Critical Damage")));

        plugin.handle(invocation("latron 负 Critical Chance Critical Damage"));

        WarframeMarketAuctionQuery query = captureQuery();
        assertThat(query.negativeStats()).containsExactly("critical_chance");
        assertThat(query.positiveStats()).containsExactly("critical_damage");
    }

    @Test
    void longestMultiwordEffectWinsOverShorterAlias() throws Exception {
        when(service.fetchAuctionWeapons("riven")).thenReturn(List.of(item("latron", "Latron")));
        when(definitions.definitions()).thenReturn(List.of(
                definition("critical_chance", "Critical Chance"),
                definition("critical_damage", "Critical Damage")));
        when(community.rivenAliases())
                .thenReturn(List.of(attribute("Critical", "critical_damage")));

        plugin.handle(invocation("latron Critical Chance"));

        assertThat(captureQuery().positiveStats()).containsExactly("critical_chance");
    }

    @Test
    void multiwordEffectsCanEndWithJoinedChineseStat() throws Exception {
        when(service.fetchAuctionWeapons("riven")).thenReturn(List.of(item("latron", "Latron")));
        when(definitions.definitions()).thenReturn(List.of(
                definition("critical_chance", "Critical Chance"),
                definition("multishot", "Multishot")));

        plugin.handle(invocation("latron Critical Chance多重 负任意"));

        WarframeMarketAuctionQuery query = captureQuery();
        assertThat(query.positiveStats()).containsExactly("critical_chance", "multishot");
        assertThat(query.requireNegative()).isTrue();
    }

    @Test
    void ambiguousMultiwordAliasIsNotResolvedByShorterPrefix() throws Exception {
        when(service.fetchAuctionWeapons("riven")).thenReturn(List.of(item("latron", "Latron")));
        when(definitions.definitions()).thenReturn(List.of(
                definition("critical_chance", "Critical Chance"),
                definition("critical_damage", "Critical Damage")));
        when(community.rivenAliases()).thenReturn(List.of(
                attribute("Critical", "critical_damage"),
                attribute("Critical Chance", "critical_damage")));

        assertThat(CommandReplyTestData.text(plugin.handle(invocation("latron Critical Chance"))))
                .contains("词条存在多个候选");
        verify(service, never()).queryAuctions(any());
    }

    @Test
    void multiwordConditionsStillRejectDuplicatesLimitsAndUnrecognizedRemainders() throws Exception {
        when(service.fetchAuctionWeapons("riven")).thenReturn(List.of(item("latron", "Latron")));
        when(definitions.definitions()).thenReturn(List.of(
                definition("critical_chance", "Critical Chance"),
                definition("critical_damage", "Critical Damage"),
                definition("multishot", "Multishot"),
                definition("recoil", "Weapon Recoil")));

        for (String condition : List.of("Critical Chance Critical Chance",
                "Critical Chance 负Critical Chance", "负Critical Chance 负Weapon Recoil",
                "Critical Chance Critical Damage Multishot Weapon Recoil")) {
            assertThat(CommandReplyTestData.text(plugin.handle(invocation("latron " + condition))))
                    .as(condition).contains("最多指定三个不同正面词条和一个负面词条");
        }
        for (String condition : List.of("Critical", "Critical Chance unknown", "负面", "负 负任意")) {
            assertThat(CommandReplyTestData.text(plugin.handle(invocation("latron " + condition)))).as(condition).isNotBlank();
        }
        verify(service, never()).queryAuctions(any());
    }

    @Test
    void negativeBoundaryCannotBeConsumedByMultiwordAlias() throws Exception {
        when(service.fetchAuctionWeapons("riven")).thenReturn(List.of(item("latron", "Latron")));
        when(community.rivenAliases()).thenReturn(List.of(
                attribute("多重 负后坐力", "critical_chance"),
                attribute("多重", "multishot"),
                attribute("后坐力", "recoil")));

        plugin.handle(invocation("latron 多重 负后坐力"));

        WarframeMarketAuctionQuery query = captureQuery();
        assertThat(query.positiveStats()).containsExactly("multishot");
        assertThat(query.negativeStats()).containsExactly("recoil");
    }

    @Test
    void communityTranslationResolvesAgainstRivenCatalogNotMarketSet() throws Exception {
        when(service.fetchAuctionWeapons("riven")).thenReturn(List.of(item("latron", "Latron")));
        when(resolver.auctionWeaponSearchTerms("拉特昂")).thenReturn(List.of("Latron"));

        plugin.handle(invocation("拉特昂"));

        assertThat(captureQuery().weaponSlug()).isEqualTo("latron");
        verify(service, never()).fetchItems();
        verify(resolver, never()).resolve(anyString());
    }

    @Test
    void anyNegativeIsPassedAsARealFilter() throws Exception {
        when(service.fetchAuctionWeapons("riven")).thenReturn(List.of(item("latron", "Latron")));

        plugin.handle(invocation("latron 负任意"));

        assertThat(captureQuery().requireNegative()).isTrue();
    }

    @Test
    void parsesCompleteChineseWeaponAndThreePositiveStatsWithAnyNegative() throws Exception {
        when(service.fetchAuctionWeapons("riven")).thenReturn(List.of(item("latron", "Latron")));
        when(resolver.auctionWeaponSearchTerms("拉特昂")).thenReturn(List.of("Latron"));

        plugin.handle(invocation("拉特昂 多重 暴击 爆伤 负任意"));

        WarframeMarketAuctionQuery query = captureQuery();
        assertThat(query.weaponSlug()).isEqualTo("latron");
        assertThat(query.positiveStats())
                .containsExactly("critical_chance", "critical_damage", "multishot");
        assertThat(query.negativeStats()).isEmpty();
        assertThat(query.requireNegative()).isTrue();
    }

    @Test
    void parsesCompoundPositiveAndNegativeStatsWithoutExtendingWeaponName() throws Exception {
        when(service.fetchAuctionWeapons("riven"))
                .thenReturn(List.of(item("reaper_prime", "Reaper Prime")));
        when(resolver.auctionWeaponSearchTerms("收割者 Prime")).thenReturn(List.of("Reaper Prime"));

        plugin.handle(invocation("收割者 Prime 基双爆 负划爆"));

        WarframeMarketAuctionQuery query = captureQuery();
        assertThat(query.weaponSlug()).isEqualTo("reaper_prime");
        assertThat(query.positiveStats())
                .containsExactly("base_damage_/_melee_damage", "critical_chance", "critical_damage");
        assertThat(query.negativeStats())
                .containsExactly("critical_chance_on_slide_attack");
    }

    @Test
    void parsesBaseDamageCompoundAlias() throws Exception {
        when(service.fetchAuctionWeapons("riven"))
                .thenReturn(List.of(item("orthos_prime", "Orthos Prime")));

        plugin.handle(invocation("Orthos Prime 基双爆"));

        assertThat(captureQuery().positiveStats())
                .containsExactly("base_damage_/_melee_damage", "critical_chance", "critical_damage");
    }

    @Test
    void parsesConfiguredMultishotCompound() throws Exception {
        when(service.fetchAuctionWeapons("riven")).thenReturn(List.of(item("latron", "Latron")));
        plugin.handle(invocation("latron 多双爆 负任意"));
        assertThat(captureQuery().positiveStats())
                .containsExactly("critical_chance", "critical_damage", "multishot");
    }

    @Test
    void compoundAliasIsNotHardcoded() throws Exception {
        when(service.fetchAuctionWeapons("riven")).thenReturn(List.of(item("latron", "Latron")));
        when(community.rivenAliases()).thenReturn(List.of(attribute("多重", "multishot")));
        assertThat(CommandReplyTestData.text(plugin.handle(invocation("latron 多双爆")))).contains("无法识别词条");
        verify(service, never()).queryAuctions(any());
    }

    @Test
    void handlesJoinedAttributesAndRejectsInvalidCombinations() throws Exception {
        when(service.fetchAuctionWeapons("riven")).thenReturn(List.of(item("latron", "Latron")));
        plugin.handle(invocation("latron 多重暴击爆伤 负任意"));
        assertThat(captureQuery().positiveStats())
                .containsExactly("critical_chance", "critical_damage", "multishot");
        clearInvocations(service);
        for (String conditions : List.of("多重 多重", "多重 负多重", "负多双爆",
                "基双爆 多重", "负")) {
            assertThat(CommandReplyTestData.text(plugin.handle(invocation("latron " + conditions)))).isNotBlank();
        }
        verify(service, never()).queryAuctions(any());
    }

    @Test
    void unknownWeaponOrAttributeNeverReachesAuctionApi() throws Exception {
        when(service.fetchAuctionWeapons("riven")).thenReturn(List.of(item("latron", "Latron")));

        assertThat(CommandReplyTestData.text(plugin.handle(invocation("latron 错误词条")))).contains("检查");
        verify(service, never()).queryAuctions(any());
    }

    @Test
    void unavailableCatalogDoesNotSendGuessedSlug() {
        when(service.fetchAuctionWeapons("riven")).thenReturn(List.of());

        assertThat(CommandReplyTestData.text(plugin.handle(invocation("latron")))).contains("暂时无法获取拍卖武器目录");
        verify(service, never()).queryAuctions(any());
    }

    @Test
    void ambiguousAttributeRequiresClarification() throws Exception {
        when(service.fetchAuctionWeapons("riven")).thenReturn(List.of(item("latron", "Latron")));
        when(community.rivenAliases()).thenReturn(List.of(
                attribute("暴击", "critical_chance"), attribute("暴击", "critical_damage")));

        assertThat(CommandReplyTestData.text(plugin.handle(invocation("latron 暴击")))).contains("词条存在多个候选");
        verify(service, never()).queryAuctions(any());
    }

    @Test
    void rejectsUnknownSearchOnlyAndPlaceholderCodesEvenThroughAliases() throws Exception {
        when(service.fetchAuctionWeapons("riven")).thenReturn(List.of(item("latron", "Latron")));
        when(definitions.definitions()).thenReturn(List.of(
                definition("critical_chance", false),
                definition("search_stat", true),
                definition("has", false),
                definition("none", false)));
        when(community.rivenAliases()).thenReturn(List.of(
                attribute("未知", "unknown"),
                attribute("错误组合", "critical_chance,unknown"),
                attribute("仅搜索", "search_stat"),
                attribute("有占位", "has"),
                attribute("无占位", "none"),
                attribute("搜索组合", "critical_chance,search_stat")));

        for (String condition : List.of("未知", "错误组合", "仅搜索", "有占位", "无占位",
                "搜索组合", "unknown", "search_stat", "has", "none")) {
            assertThat(CommandReplyTestData.text(plugin.handle(invocation("latron " + condition))))
                    .as(condition).contains("无法识别词条");
        }
        verify(service, never()).queryAuctions(any());
    }

    @Test
    void aliasCannotSupplyDefinitionWhenFormalCatalogIsEmpty() throws Exception {
        when(service.fetchAuctionWeapons("riven")).thenReturn(List.of(item("latron", "Latron")));
        when(definitions.definitions()).thenReturn(List.of());

        assertThat(CommandReplyTestData.text(plugin.handle(invocation("latron 多重")))).contains("无法识别词条");
        verify(service, never()).queryAuctions(any());
    }

    @Test
    void compoundOrderDoesNotCreateFalseAmbiguity() throws Exception {
        when(service.fetchAuctionWeapons("riven")).thenReturn(List.of(item("latron", "Latron")));
        when(community.rivenAliases()).thenReturn(List.of(
                attribute("双爆", "critical_chance,critical_damage"),
                attribute("双爆", "critical_damage,critical_chance")));

        plugin.handle(invocation("latron 双爆"));

        assertThat(captureQuery().positiveStats())
                .containsExactly("critical_chance", "critical_damage");
    }

    private static WarframeRivenAttributeDTO definition(String code, boolean searchOnly) {
        return definition(code, code, searchOnly);
    }

    private static WarframeRivenAttributeDTO definition(String code, String effect) {
        return definition(code, effect, false);
    }

    private static WarframeRivenAttributeDTO definition(String code, String effect, boolean searchOnly) {
        return new WarframeRivenAttributeDTO(code, effect, effect, "default", false, false,
                null, searchOnly, null, "%", Set.of(), List.of());
    }

    private WarframeMarketAuctionQuery captureQuery() {
        ArgumentCaptor<WarframeMarketAuctionQuery> captor = ArgumentCaptor.forClass(WarframeMarketAuctionQuery.class);
        verify(service).queryAuctions(captor.capture());
        return captor.getValue();
    }

    private static CommandInvocation invocation(String input) {
        return new CommandInvocation(mock(BotMessageContext.class), "market-riven", "zk", input);
    }

    private static WarframeCommunityRecordDTO<WarframeCommunityRecordDTO.RivenAliasDTO>
            attribute(String name, String code) {
        return new WarframeCommunityRecordDTO<>(1L, name, new WarframeCommunityRecordDTO.RivenAliasDTO(code, name));
    }

    private static WarframeMarketItemDTO item(String slug, String name) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        WarframeMarketItemMapper parser = new WarframeMarketItemMapper();
        ReflectionTestUtils.setField(parser, "objectMapper", mapper);
        return parser.parse(mapper.readValue(
                "{\"slug\":\"" + slug + "\",\"i18n\":{\"en\":{\"name\":\"" + name + "\"}}}", ItemRawData.class));
    }
}
