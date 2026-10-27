package com.nnm.bot.warframe.plugin.runtime.market;

import com.nnm.bot.warframe.persistent.curated.service.reference.impl.WarframeItemResolverServiceImpl;
import com.nnm.bot.warframe.runtime.constant.WarframeMarketConstants.OrderFilter;

import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketOrderPageDTO;

import com.fasterxml.jackson.databind.JsonNode;
import com.nnm.bot.warframe.runtime.rawdata.market.MarketResponseRawData.ItemRawData;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.warframe.persistent.official.parser.input.WarframeOfficialInputParser;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import com.nnm.bot.warframe.constant.WarframeCacheNames;
import com.nnm.bot.warframe.runtime.mapper.market.WarframeMarketItemMapper;
import com.nnm.bot.warframe.plugin.CommandReplyTestData;
import com.nnm.bot.warframe.runtime.pojo.dto.market.WarframeMarketItemDTO;
import com.nnm.bot.warframe.runtime.pojo.vo.market.WarframeMarketOrderVO;
import com.nnm.bot.warframe.runtime.pojo.query.market.WarframeMarketOrderQuery;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeItemResolutionDTO;
import com.nnm.bot.warframe.runtime.service.market.WarframeMarketService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeItemResolverService;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeCommunityCatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class MarketPluginTest {
    private final MarketPlugin plugin = new MarketPlugin();
    private final WarframeItemResolverService resolver = mock(WarframeItemResolverService.class);
    private final WarframeMarketService service = mock(WarframeMarketService.class);
    private final WarframeMarketOrderPageDTO response = new WarframeMarketOrderPageDTO(
            "测试商品", "query", "slug", "pc", "ALL", false, null,
            PageResult.of(List.of(), 1, 10, 0), "", "", null, List.of());

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(plugin, "itemResolver", resolver);
        ReflectionTestUtils.setField(plugin, "marketService", service);
        when(resolver.resolve(anyString())).thenReturn(WarframeItemResolutionDTO.notFound());
        when(resolver.marketSearchTerms(anyString())).thenReturn(List.of());
        when(service.queryOrders(any())).thenReturn(Optional.of(response));
    }

    @Test
    @org.junit.jupiter.api.condition.EnabledIfSystemProperty(named = "nnm.test.dataset-directory", matches = ".+")
    void releasedMarketCatalogResolvesAllLegacyChineseComponentInputs() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        var parser = new WarframeMarketItemMapper();
        ReflectionTestUtils.setField(parser, "objectMapper", mapper);
        var official = mock(WarframeOfficialRecordReadService.class);
        var community = mock(WarframeCommunityCatalogService.class);
        var real = new WarframeItemResolverServiceImpl();
        ReflectionTestUtils.setField(real, "officialCatalogService", official);
        ReflectionTestUtils.setField(real, "communityCatalogService", community);
        ReflectionTestUtils.setField(real, "cacheManager", new ConcurrentMapCacheManager(WarframeCacheNames.ITEM_RESOLUTION_INDEX));
        var directory = java.nio.file.Path.of(System.getProperty("nnm.test.dataset-directory"));
        var snapshotParser = new com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialSnapshotParser();
        ReflectionTestUtils.setField(snapshotParser, "objectMapper", mapper);
        var rows = new java.util.ArrayList<WarframeOfficialRecordReadDTO>();
        var items = new java.util.ArrayList<WarframeMarketItemDTO>();
        for (var imported : snapshotParser.readRecords(directory, java.util.Set.of("market/trade-items.json"))) {
            JsonNode node = mapper.readTree(imported.rawJson());
            rows.add(new WarframeOfficialRecordReadDTO((long) rows.size() + 1, imported.domain(), imported.recordKey(),
                    imported.path(), imported.domain().domainCode(), imported.officialName(),
                    imported.localizedName(), null, null, imported.description(), null, null, node.path("slug").asText(), (OfficialObjectDTO) WarframeOfficialInputParser.parseCompleteJson(mapper, imported.domain(), imported.rawJson())));
            items.add(parser.parse(mapper.readValue(imported.rawJson(), ItemRawData.class)));
        }
        when(official.records()).thenReturn(rows);
        when(official.records(WarframeOfficialDataDomain.MARKET_ITEM)).thenReturn(rows);
        when(service.fetchItems()).thenReturn(items);
        ReflectionTestUtils.setField(plugin, "itemResolver", real);
        for (String[] example : List.of(
                new String[]{"Ash机体蓝图", "ash_prime_chassis_blueprint"},
                new String[]{"Ash系统", "ash_prime_systems_blueprint"},
                new String[]{"Ash头", "ash_prime_neuroptics_blueprint"},
                new String[]{"布莱顿枪机", "braton_prime_receiver"},
                new String[]{"布莱顿枪管", "braton_prime_barrel"},
                new String[]{"布莱顿枪托", "braton_prime_stock"},
                new String[]{"Ash一套", "ash_prime_set"},
                new String[]{"Ash套装", "ash_prime_set"},
                new String[]{"Ash蓝图", "ash_prime_blueprint"})) {
            clearInvocations(service);
            var result = plugin.handle(invocation(example[0]));
            assertThat(result.successful()).as(example[0] + " => " + CommandReplyTestData.text(result)).isTrue();
            verify(service).queryOrders(argThat(query -> query.marketSlug().equals(example[1])));
        }
    }

    @Test
    void equallyRankedMarketCandidatesDoNotSilentlyPickFirstSlug() throws Exception {
        when(service.fetchItems()).thenReturn(List.of(item("arcane_a", "Arcane A"), item("arcane_b", "Arcane B")));
        assertThat(plugin.handle(invocation("arcane")).successful()).isFalse();
        verify(service, never()).queryOrders(any());
    }

    @Test
    void parsesMultiwordNameAndPageIntoPojo() {
        WarframeMarketOrderQuery query = MarketPlugin.parseQuery("满级 买家 PC ash prime set 2");
        assertThat(query.itemInput()).isEqualTo("ash prime set");
        assertThat(query.platform()).isEqualTo("pc");
        assertThat(query.page()).isEqualTo(2);
        assertThat(query.maxLevel()).isTrue();
        assertThat(query.filter()).isEqualTo(OrderFilter.BUY);
        assertThat(MarketPlugin.parseQuery(null).itemInput()).isEmpty();
        assertThat(MarketPlugin.parseQuery("ash 99999999999999999999").page()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    void ashPrefersPrimeSetAndSelectsCompactCommandData() throws Exception {
        when(service.fetchItems()).thenReturn(List.of(item("ash_prime_chassis", "Ash Prime Chassis"),
                item("ash_prime_set", "Ash Prime Set")));
        CommandResult result = plugin.handle(invocation("ash 2"));
        assertThat(CommandReplyTestData.data(result)).isEqualTo(WarframeMarketOrderVO.from(response));
        ArgumentCaptor<WarframeMarketOrderQuery> query = ArgumentCaptor.forClass(WarframeMarketOrderQuery.class);
        verify(service).queryOrders(query.capture());
        assertThat(query.getValue().marketSlug()).isEqualTo("ash_prime_set");
        assertThat(query.getValue().page()).isEqualTo(2);
    }

    @Test
    void aliasPlusPartUsesExpandedSearchTerms() throws Exception {
        when(resolver.marketSearchTerms("母牛机体")).thenReturn(List.of("Hildryn Chassis"));
        when(service.fetchItems()).thenReturn(List.of(item("hildryn_prime_set", "Hildryn Prime Set"),
                item("hildryn_prime_chassis", "Hildryn Prime Chassis")));
        plugin.handle(invocation("母牛机体"));
        verify(service).queryOrders(argThat(query -> query.marketSlug().equals("hildryn_prime_chassis")));
    }

    @Test
    void bareMagPrefersExactPrimeFamilyOverOrdinaryNameAndSubstringMatches() throws Exception {
        when(service.fetchItems()).thenReturn(List.of(
                item("mag", "Mag"), item("mag_prime_chassis", "Mag Prime Chassis"),
                item("mag_prime_set", "Mag Prime Set"),
                item("magistar_prime_set", "Magistar Prime Set"),
                item("amalgam_serration", "Amalgam Serration")));
        plugin.handle(invocation("mag"));
        verify(service).queryOrders(argThat(query -> query.marketSlug().equals("mag_prime_set")));
    }

    @Test
    void explicitPartSlugAndFullNameRemainExact() throws Exception {
        when(service.fetchItems()).thenReturn(List.of(
                item("mag_prime_chassis", "Mag Prime Chassis"),
                item("mag_prime_set", "Mag Prime Set")));
        plugin.handle(invocation("mag_prime_chassis"));
        plugin.handle(invocation("Mag Prime Chassis"));
        verify(service, times(2)).queryOrders(
                argThat(query -> query.marketSlug().equals("mag_prime_chassis")));
    }

    @Test
    void ordinaryModExactNameIsNotReplacedByPrimedVersion() throws Exception {
        when(service.fetchItems()).thenReturn(List.of(
                item("ravage", "Ravage"), item("primed_ravage", "Primed Ravage")));
        plugin.handle(invocation("ravage"));
        verify(service).queryOrders(argThat(query -> query.marketSlug().equals("ravage")));
    }

    @Test
    void realResolverCombinesNekrosAliasWithShortHeadAndChassis() throws Exception {
        WarframeCommunityCatalogService community = installRealResolver();
        when(community.aliases()).thenReturn(List.of(new WarframeCommunityRecordDTO<>(1L, "摸尸", new WarframeCommunityRecordDTO.AliasDTO("摸尸", "Nekros"))));
        when(service.fetchItems()).thenReturn(List.of(
                item("nekros_prime_set", "Nekros Prime Set"),
                item("nekros_prime_chassis", "Nekros Prime Chassis"),
                item("nekros_prime_neuroptics", "Nekros Prime Neuroptics")));
        plugin.handle(invocation("摸尸头"));
        plugin.handle(invocation("摸尸机体"));
        verify(service).queryOrders(argThat(query -> query.marketSlug().equals("nekros_prime_neuroptics")));
        verify(service).queryOrders(argThat(query -> query.marketSlug().equals("nekros_prime_chassis")));
    }

    @Test
    void realResolverCombinesCommunityAliasWithMarketComponent() throws Exception {
        WarframeCommunityCatalogService community = installRealResolver();
        when(community.aliases()).thenReturn(List.of(new WarframeCommunityRecordDTO<>(1L, "母牛", new WarframeCommunityRecordDTO.AliasDTO("母牛", "Hildryn"))));
        when(service.fetchItems()).thenReturn(List.of(
                item("hildryn_prime_set", "Hildryn Prime Set"),
                item("hildryn_prime_chassis", "Hildryn Prime Chassis")));

        plugin.handle(invocation("母牛机体 2"));

        verify(service).queryOrders(argThat(query ->
                query.marketSlug().equals("hildryn_prime_chassis") && query.page() == 2));
    }

    @Test
    void realResolverUsesLatronTranslationAndFindsPrimeSet() throws Exception {
        WarframeCommunityCatalogService community = installRealResolver();
        when(service.fetchItems()).thenReturn(List.of(
                item("latron_prime_barrel", "Latron Prime Barrel"),
                item("latron_prime_set", "Latron Prime Set"),
                item("laetum", "Laetum")));

        plugin.handle(invocation("拉特昂"));

        verify(service).queryOrders(argThat(query -> query.marketSlug().equals("latron_prime_set")));
    }

    @Test
    void compactPrimedModCommandFindsPrimedSlugAndRequestsMaxRank() throws Exception {
        WarframeCommunityCatalogService community = installRealResolver();
        when(service.fetchItems()).thenReturn(List.of(
                item("ravage", "Ravage"),
                item("primed_ravage", "Primed Ravage")));

        plugin.handle(invocation("破灭p满级"));

        verify(service).queryOrders(argThat(query ->
                query.marketSlug().equals("primed_ravage") && query.maxLevel()));
    }

    @Test
    void knownSlugStillWorksWhenCatalogIsUnavailable() {
        when(service.fetchItems()).thenThrow(new IllegalStateException("unavailable"));
        assertThat(CommandReplyTestData.data(plugin.handle(invocation("wukong prime set"))))
                .isEqualTo(WarframeMarketOrderVO.from(response));
        verify(service).queryOrders(argThat(query -> query.marketSlug().equals("wukong_prime_set")));
    }

    @Test
    void unknownChineseInputNeverReachesOrderApi() {
        when(service.fetchItems()).thenReturn(List.of());
        assertThat(CommandReplyTestData.text(plugin.handle(invocation("不存在的物品")))).contains("没有找到");
        verify(service, never()).queryOrders(any());
    }

    @Test
    void serializedPageRetainsExistingTemplateFieldNames() {
        JsonNode tree = new ObjectMapper().valueToTree(response);
        assertThat(tree.path("orders").isArray()).isTrue();
        assertThat(tree.path("page").asInt()).isEqualTo(1);
        assertThat(tree.path("pageSize").asInt()).isEqualTo(10);
        assertThat(tree.has("orderPage")).isFalse();
        assertThat(tree.has("rawJson")).isFalse();
    }

    private static CommandInvocation invocation(String args) {
        return new CommandInvocation(mock(BotMessageContext.class), "market", "wm", args);
    }

    private WarframeCommunityCatalogService installRealResolver() {
        WarframeItemResolverService real = new WarframeItemResolverServiceImpl();
        WarframeCommunityCatalogService community = mock(WarframeCommunityCatalogService.class);
        ReflectionTestUtils.setField(real, "communityCatalogService", community);
        WarframeOfficialRecordReadService official = mock(WarframeOfficialRecordReadService.class);
        ReflectionTestUtils.setField(real, "officialCatalogService", official);
        when(official.records()).thenReturn(List.of(
                official(WarframeOfficialDataDomain.WEAPON, "Latron", "拉特昂", "/Lotus/Latron"),
                official(WarframeOfficialDataDomain.UPGRADE, "Ravage", "破灭", "/Lotus/Ravage")));
        when(official.records(WarframeOfficialDataDomain.MARKET_ITEM)).thenReturn(List.of(
                official(WarframeOfficialDataDomain.MARKET_ITEM, "Hildryn Prime Chassis", "Hildryn Prime 机体", "hildryn_prime_chassis"),
                official(WarframeOfficialDataDomain.MARKET_ITEM, "Nekros Prime Chassis", "Nekros Prime 机体", "nekros_prime_chassis"),
                official(WarframeOfficialDataDomain.MARKET_ITEM, "Nekros Prime Neuroptics", "Nekros Prime 头部神经光元", "nekros_prime_neuroptics")));
        ReflectionTestUtils.setField(real, "cacheManager",
                new ConcurrentMapCacheManager(WarframeCacheNames.ITEM_RESOLUTION_INDEX));
        ReflectionTestUtils.setField(plugin, "itemResolver", real);
        return community;
    }

    private static WarframeMarketItemDTO item(String slug, String name) throws Exception {
        WarframeMarketItemMapper parser = new WarframeMarketItemMapper();
        ReflectionTestUtils.setField(parser, "objectMapper", new ObjectMapper());
        return parser.parse(new ObjectMapper().readValue(
                "{\"slug\":\"" + slug + "\",\"i18n\":{\"en\":{\"name\":\"" + name + "\"}}}", ItemRawData.class));
    }

    private static WarframeOfficialRecordReadDTO official(WarframeOfficialDataDomain domain, String en, String zh, String key) {
        return new WarframeOfficialRecordReadDTO(1L, domain, key, domain.productPath(), domain.domainCode(),
                en, zh, null, null, null, null, null,
                domain == WarframeOfficialDataDomain.MARKET_ITEM ? key : null, null);
    }
}
