package com.nnm.bot.warframe.runtime.service.worldstate.impl;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.warframe.runtime.cache.worldstate.WorldStateRuntimeSnapshot;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.worldstate.DescendiaRewardGroupDTO;
import com.nnm.bot.warframe.persistent.curated.service.worldstate.WarframeDescendiaRewardCatalogService;
import com.nnm.bot.warframe.persistent.curated.service.worldstate.WarframeSteelPathRotationService;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.worldstate.SteelPathRotationDTO;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.OfficialVendorDTO;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialLookupService;
import cn.hutool.core.util.ObjectUtil;
import com.nnm.bot.warframe.runtime.constant.WarframeBountyCycleConstants.Syndicate;
import com.nnm.bot.warframe.runtime.constant.WarframeConquestType;
import com.nnm.bot.warframe.runtime.constant.WarframeOpenWorldSyndicate;
import com.nnm.bot.warframe.runtime.constant.WarframeWorldStateDisplayConstants;
import com.nnm.bot.warframe.runtime.constant.WarframeWorldStateScope;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateResolutionMapper;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateMarkupCleaner;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateCommandVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateManagementVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateRawVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateSectionVO;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeReferenceProjectionEnrichmentService;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeWorldStateManagementService;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeWorldStateRecordEnrichmentService;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeWorldStateService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 管理端和机器人共用同一套 WorldStateDTO 业务投影，避免后台重新解释原始上游 JSON。
 */
@Service
public class WarframeWorldStateManagementServiceImpl implements WarframeWorldStateManagementService {

    private static final String STEEL_PATH_VENDOR =
        "/Lotus/Types/Game/VendorManifests/Hubs/TeshinHardModeVendorManifest";
    private static final String STEEL_PATH_CANDIDATE_NOTICE = "官方每周轮换候选商品，当前轮次尚未确认";

    @Resource
    private WarframeWorldStateService worldStateService;
    @Resource
    private WarframeReferenceProjectionEnrichmentService projectionEnricher;
    @Resource
    private WarframeWorldStateRecordEnrichmentService recordEnricher;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private WarframeOfficialLookupService officialLookup;
    @Resource
    private WarframeDescendiaRewardCatalogService descendiaRewards;
    @Resource
    private WarframeSteelPathRotationService steelPathRotation;

    /**
     * 组装当前世界状态管理视图。
     *
     * @return 当前世界状态各分区及刷新状态的管理视图。
     */
    @Override
    public WarframeWorldStateManagementVO current() {
        WorldStateDTO state = worldStateService.getCurrentState();
        if (ObjectUtil.isNull(state)) {
            return new WarframeWorldStateManagementVO(false, null, null, null, false, null, Instant.now().toString(),
                List.of());
        }

        List<SectionProjection> projections = projections(state);
        List<WarframeWorldStateCommandVO> originals = projections.stream().map(SectionProjection::output).toList();
        List<WarframeWorldStateCommandVO> outputs =
            recordEnricher.enrich(originals, projectionEnricher.enrichAll(originals));
        List<WarframeWorldStateSectionVO> sections = new ArrayList<>(outputs.size());
        for (int index = 0; index < outputs.size(); index++) {
            add(sections, projections.get(index).key(), outputs.get(index));
        }

        return view(state, sections);
    }

    private static final Map<String,
        String> PARSED_SECTION_TITLES = Map.ofEntries(Map.entry("alerts", "警报"), Map.entry("fissures", "虚空裂缝"),
            Map.entry("invasions", "入侵"), Map.entry("sortie", "当前突击"), Map.entry("cetusCycle", "夜灵平原周期"),
            Map.entry("vallisCycle", "奥布山谷周期"), Map.entry("cambionCycle", "魔胎之境周期"), Map.entry("voidTrader", "当前虚空商人"),
            Map.entry("nightwave", "午夜电波"), Map.entry("timestamp", "上游时间"), Map.entry("sorties", "突击列表"),
            Map.entry("voidTraders", "虚空商人列表"), Map.entry("earthCycle", "地球周期"), Map.entry("duviriCycle", "双衍王境周期"),
            Map.entry("zarimanCycle", "扎里曼周期"), Map.entry("dailyDeals", "每日特惠"), Map.entry("events", "世界事件"),
            Map.entry("news", "新闻"), Map.entry("syndicateMissions", "集团任务"), Map.entry("steelPathOfferings", "钢铁之路奖励"),
            Map.entry("activeMissions", "活动任务"), Map.entry("archonHunts", "执刑官猎杀"), Map.entry("buildLabel", "游戏版本"),
            Map.entry("globalUpgrades", "全局增益"), Map.entry("flashSales", "限时促销"),
            Map.entry("persistentEnemies", "持续敌人"), Map.entry("metadata", "采集信息"),
            Map.entry("constructionProgress", "舰队建造进度"), Map.entry("simaris", "中枢研究"),
            Map.entry("sentientOutpost", "Sentient 前哨"), Map.entry("clanInitiatives", "氏族每周行动"),
            Map.entry("circuitChoices", "回廊装备"), Map.entry("calendarSeasons", "1999 日历赛季"),
            Map.entry("vaultTraders", "Prime 宝库商人"), Map.entry("weeklyChallenges", "每周挑战"),
            Map.entry("darkSectors", "黑暗地带"), Map.entry("descents", "深层下降"), Map.entry("marketCategories", "商城分类"),
            Map.entry("temporaryActivity", "社区活动与寻呼机"), Map.entry("kuvaMissions", "赤毒任务"));

    /**
     * 将已解析 DTO 转换为独立展示投影，保留所有嵌套字段。
     *
     * @return 当前实例完整解析快照；没有数据时返回明确不可用状态。
     */
    @Override
    public WarframeWorldStateManagementVO parsed() {
        WorldStateDTO state = worldStateService.getCurrentState();
        if (state == null) {
            return new WarframeWorldStateManagementVO(false, null, null, null, false, null, Instant.now().toString(),
                List.of());
        }
        Map<String, Object> fields =
            objectMapper.convertValue(state, new TypeReference<LinkedHashMap<String, Object>>() {});
        List<SectionProjection> projections = new ArrayList<>();
        fields.forEach((key, value) -> {
            if (WarframeWorldStateScope.NON_BUSINESS_SECTIONS.contains(key)) {
                return; // 技术/独立业务数据留在原快照，不占当前世界状态的导航分区。
            }
            List<Map<String, Object>> rows =
                value == null ? List.of() : parsedRows(value instanceof List<?> list ? list : List.of(value));
            String title = PARSED_SECTION_TITLES.getOrDefault(key, key);
            if ("fissures".equals(key)) {
                addParsedProjection(projections, "fissures", "裂隙",
                    rows.stream()
                        .filter(
                            row -> !Boolean.TRUE.equals(row.get("hard")) && !Boolean.TRUE.equals(row.get("voidStorm")))
                        .toList());
                addParsedProjection(projections, "steel-fissures", "钢铁裂隙",
                    rows.stream()
                        .filter(
                            row -> Boolean.TRUE.equals(row.get("hard")) && !Boolean.TRUE.equals(row.get("voidStorm")))
                        .toList());
                addParsedProjection(projections, "void-storms", "九重天裂隙",
                    rows.stream().filter(row -> Boolean.TRUE.equals(row.get("voidStorm"))).toList());
                return; // 不把同一条裂隙同时显示在汇总与子类中。
            }
            if ("archimedeas".equals(key)) {
                rows = rows.stream().map(row -> {
                    Map<String, Object> display = new LinkedHashMap<>(row);
                    display.put("type", WarframeConquestType.displayName((String)row.get("typeKey")));
                    return display;
                }).toList();
                addParsedProjection(projections, "deep-archimedeas", "深层科研", rows.stream()
                    .filter(row -> WarframeConquestType.DEEP.matches((String)row.get("typeKey"))).toList());
                addParsedProjection(projections, "hex-archimedeas", "时光科研",
                    rows.stream().filter(row -> WarframeConquestType.HEX.matches((String)row.get("typeKey"))).toList());
                List<Map<String, Object>> others =
                    rows.stream().filter(row -> !WarframeConquestType.DEEP.matches((String)row.get("typeKey"))
                        && !WarframeConquestType.HEX.matches((String)row.get("typeKey"))).toList();
                if (!others.isEmpty()) {
                    addParsedProjection(projections, "other-archimedeas", "其他科研", others);
                }
                return; // 原始科研集合已拆成互斥分区，不重复展示。
            }
            if ("steelPathOfferings".equals(key) && rows.isEmpty()) {
                rows = items(steelPath(state).data().get("items"));
            }
            if ("descents".equals(key)) {
                addParsedProjection(projections, "descents", title, items(descents(state).data().get("items")));
                return; // 两种视图只展示当前周次，未来排期保留在原始快照。
            }
            if ("temporaryActivity".equals(key)) {
                addParsedProjection(projections, key, title,
                    items(WarframeWorldStateProjector.temporaryActivities(state).data().get("items")));
                return;
            }
            if ("calendarSeasons".equals(key)) {
                addParsedProjection(projections, "calendarSeasons", title,
                    items(WarframeWorldStateProjector.calendar(state).data().get("items")));
                return; // 已建模视图也不展示没有事件内容的日历日期。
            }
            if ("syndicateMissions".equals(key)) {
                rows = rows.stream().filter(row -> syndicateOnly(String.valueOf(row.get("syndicate")))).toList();
                projections.addAll(bountyProjections(state));
                for (WarframeOpenWorldSyndicate region : WarframeOpenWorldSyndicate.values()) {
                    addParsedProjection(projections, region.sectionKey(), region.title(),
                        items(WarframeWorldStateProjector.syndicate(state, region.title(), region::matches).data()
                            .get("items")));
                }
            }
            if ("circuitChoices".equals(key)) {
                // 完整分类和原始选项仍保留在回廊装备分区；额外给展示端提供可查图片的逐件奖励。
                addParsedProjection(projections, "circuit-normal", "普通回廊",
                    items(WarframeWorldStateProjector.circuit(state, false).data().get("items")));
                addParsedProjection(projections, "circuit-hard", "灵化轮换",
                    items(WarframeWorldStateProjector.circuit(state, true).data().get("items")));
            }
            addParsedProjection(projections, key, title, rows);
        });
        List<WarframeWorldStateCommandVO> originals = projections.stream().map(SectionProjection::output).toList();
        List<WarframeWorldStateCommandVO> enriched =
            recordEnricher.enrich(originals, projectionEnricher.enrichAll(originals));
        List<WarframeWorldStateSectionVO> sections = new ArrayList<>(enriched.size());
        for (int index = 0; index < enriched.size(); index++) {
            add(sections, projections.get(index).key(), enriched.get(index));
        }
        return view(state, sections);
    }

    /**
     * 保留完整解析字段，并让展示投影使用与命令相同的批量引用解析。
     *
     * @param sections 待增强的分区。
     * @param key 分区编码。
     * @param title 可读标题。
     * @param rows 完整解析条目。
     */
    private static void addParsedProjection(List<SectionProjection> sections, String key, String title,
        List<Map<String, Object>> rows) {
        sections.add(
            new SectionProjection(key, new WarframeWorldStateCommandVO(title, Map.of("title", title, "items", rows))));
    }

    /**
     * 组织详细分区的展示行；保留空值与原始键，展示副本再由引用解析补齐。
     *
     * @param values 已解析字段的展示值。
     * @return 可序列化的展示行。
     */
    private static List<Map<String, Object>> parsedRows(List<?> values) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Object value : values) {
            Map<String, Object> row = new LinkedHashMap<>();
            if (value instanceof Map<?, ?> fields) {
                fields.forEach((key, field) -> row.put(String.valueOf(key), field));
            } else {
                row.put("value", value);
            }
            rows.add(row);
        }
        return List.copyOf(rows);
    }

    /**
     * 命令摘要与完整解析视图共用同一份采集元数据。
     *
     * @param state 当前已解析状态。
     * @param sections 当前展示分区。
     * @return 管理端快照视图。
     */
    private static WarframeWorldStateManagementVO view(WorldStateDTO state,
        List<WarframeWorldStateSectionVO> sections) {
        WarframeWorldStateCommandVO metadata = WarframeWorldStateProjector.alerts(state);
        Map<String, Object> data = metadata.data();
        return new WarframeWorldStateManagementVO(true, stringValue(data, "source"),
            instantValue(state.metadata() == null ? null : state.metadata().fetchedAt()),
            instantValue(state.metadata() == null ? null : state.metadata().providerTimestamp()),
            Boolean.TRUE.equals(data.get("stale")), instantValue(state.timestamp()), Instant.now().toString(),
            List.copyOf(sections));
    }

    /**
     * 统一使用带时区的 ISO 时刻供客户端计算时间和展示。
     *
     * @param instant 当前快照中的时刻。
     * @return 可解析的 ISO 时刻，缺省时为空。
     */
    private static String instantValue(Instant instant) {
        return instant == null ? null : instant.toString();
    }

    /**
     * 读取排障使用的原始快照和采集信息。
     *
     * @return 用于排障的世界状态原文及采集信息。
     */
    @Override
    public WarframeWorldStateRawVO raw() {
        WorldStateRuntimeSnapshot snapshot = worldStateService.getCurrentSnapshot();
        if (snapshot == null) {
            return new WarframeWorldStateRawVO(false, null, null, null, List.of(), null);
        }
        return new WarframeWorldStateRawVO(snapshot.rawJson() != null, snapshot.source(),
            snapshot.fetchedAt() == null ? null : snapshot.fetchedAt().toString(), snapshot.contentHash(),
            snapshot.topLevelFields(), snapshot.rawJson());
    }

    /**
     * 加入一条数据并维护当前容器。
     *
     * @param sections 分区集合。
     * @param key 当前记录或字段的查找键。
     * @param output 需要补全或转换的输出数据。
     */
    private static void add(List<WarframeWorldStateSectionVO> sections, String key,
        WarframeWorldStateCommandVO output) {
        List<Map<String, Object>> items = items(output.data().get("items"));
        List<Map<String, Object>> displayItems =
            items.stream().map(item -> (Map<String, Object>)WarframeWorldStateMarkupCleaner.cleanValue(item)).toList();
        sections.add(new WarframeWorldStateSectionVO(key, stringValue(output.data(), "title"),
            CollUtil.isNotEmpty(displayItems), displayItems.size(), displayItems,
            WarframeWorldStateResolutionMapper.pending(output.data(), output.unresolvedReferences())));
    }

    /**
     * 组织当前世界状态各业务分区的展示投影。
     *
     * @param state 已经解析的当前世界状态。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<SectionProjection> projections(WorldStateDTO state) {
        List<SectionProjection> sections = new ArrayList<>(List.of(
            new SectionProjection("alerts", WarframeWorldStateProjector.alerts(state)),
            new SectionProjection("sorties", WarframeWorldStateProjector.sorties(state, false)),
            new SectionProjection("archon-hunts", WarframeWorldStateProjector.sorties(state, true)),
            new SectionProjection("void-trader", WarframeWorldStateProjector.voidTrader(state)),
            new SectionProjection("daily-deals", WarframeWorldStateProjector.dailyDeals(state)),
            new SectionProjection("flash-sales", WarframeWorldStateProjector.flashSales(state)),
            new SectionProjection("in-game-market", WarframeWorldStateProjector.market(state)),
            new SectionProjection("global-upgrades", WarframeWorldStateProjector.globalUpgrades(state)),
            new SectionProjection("temporary-activities", WarframeWorldStateProjector.temporaryActivities(state)),
            new SectionProjection("kuva-missions", WarframeWorldStateProjector.kuvaMissions(state)),
            new SectionProjection("persistent-enemies", WarframeWorldStateProjector.persistentEnemies(state)),
            new SectionProjection("invasions", WarframeWorldStateProjector.invasions(state)),
            new SectionProjection("fissures",
                WarframeWorldStateProjector.fissures(state, mission -> !mission.hard() && !mission.voidStorm(), "裂隙")),
            new SectionProjection("steel-fissures",
                WarframeWorldStateProjector.fissures(state, mission -> mission.hard() && !mission.voidStorm(), "钢铁裂隙")),
            new SectionProjection("void-storms",
                WarframeWorldStateProjector.fissures(state, WorldStateDTO.ActiveMissionDTO::voidStorm, "九重天裂隙")),
            new SectionProjection("steel-path", steelPath(state)),
            new SectionProjection("cycles", WarframeWorldStateProjector.cycles(state)),
            new SectionProjection("nightwave", WarframeWorldStateProjector.nightwave(state))));
        sections.addAll(bountyProjections(state));
        sections.addAll(List.of(
            // 官方快照 Conquests；科研条件保留原始值，术语由代码簿/补充数据解析。
            new SectionProjection("deep-archimedeas",
                WarframeWorldStateProjector.archimedeas(state, "深层科研",
                    conquest -> WarframeConquestType.DEEP.matches(conquest.typeKey()))),
            new SectionProjection("hex-archimedeas",
                WarframeWorldStateProjector.archimedeas(state, "时光科研",
                    conquest -> WarframeConquestType.HEX.matches(conquest.typeKey()))),
            new SectionProjection("descents", descents(state)),
            new SectionProjection("events", WarframeWorldStateProjector.events(state)),
            new SectionProjection("news", WarframeWorldStateProjector.news(state)),
            new SectionProjection("construction-progress", WarframeWorldStateProjector.constructionProgress(state)),
            new SectionProjection("simaris", WarframeWorldStateProjector.simaris(state)),
            new SectionProjection("sentient-outposts", WarframeWorldStateProjector.sentientOutposts(state)),
            new SectionProjection("clan-initiative", WarframeWorldStateProjector.clanInitiatives(state)),
            new SectionProjection("circuit-normal", WarframeWorldStateProjector.circuit(state, false)),
            new SectionProjection("circuit-hard", WarframeWorldStateProjector.circuit(state, true)),
            new SectionProjection("calendar", WarframeWorldStateProjector.calendar(state)),
            new SectionProjection("vault-trader", WarframeWorldStateProjector.vaultTrader(state)),
            new SectionProjection("syndicate-missions",
                WarframeWorldStateProjector.syndicate(state, "集团任务",
                    WarframeWorldStateManagementServiceImpl::syndicateOnly)),
            new SectionProjection("ostrons",
                WarframeWorldStateProjector.syndicate(state, "希图斯", WarframeOpenWorldSyndicate.CETUS::matches)),
            new SectionProjection("solaris",
                WarframeWorldStateProjector.syndicate(state, "索拉里斯", WarframeOpenWorldSyndicate.SOLARIS::matches)),
            new SectionProjection("entrati",
                WarframeWorldStateProjector.syndicate(state, "英择谛", WarframeOpenWorldSyndicate.ENTRATI::matches))));
        SectionProjection otherResearch = new SectionProjection("other-archimedeas",
            WarframeWorldStateProjector.archimedeas(state, "其他科研",
                conquest -> !WarframeConquestType.DEEP.matches(conquest.typeKey())
                    && !WarframeConquestType.HEX.matches(conquest.typeKey())));
        if (!items(otherResearch.output().data().get("items")).isEmpty()) {
            sections.add(otherResearch);
        }
        return sections;
    }

    /**
     * 按当前周挑战的楼层挂接人工维护的官方公告固定奖池，实际每周选择仍由游戏负责。
     *
     * @param state 当前世界状态。
     * @return 附带普通与钢铁楼层候选奖励的当前周展示内容。
     */
    private WarframeWorldStateCommandVO descents(WorldStateDTO state) {
        WarframeWorldStateCommandVO current = WarframeWorldStateProjector.descents(state);
        Map<Integer, Map<String, DescendiaRewardGroupDTO>> catalog = new LinkedHashMap<>();
        for (DescendiaRewardGroupDTO group : descendiaRewards.findAll())
            catalog.computeIfAbsent(group.floor(), ignored -> new LinkedHashMap<>()).put(group.difficulty(), group);
        List<Map<String, Object>> weeks = new ArrayList<>();
        for (Map<String, Object> week : items(current.data().get("items"))) {
            Map<String, Object> enriched = new LinkedHashMap<>(week);
            List<Map<String, Object>> challenges = new ArrayList<>();
            for (Map<String, Object> challenge : items(week.get("challenges"))) {
                Map<String, Object> entry = new LinkedHashMap<>(challenge);
                if (challenge.get("index") instanceof Number floor) {
                    var rewards = catalog.getOrDefault(floor.intValue(), Map.of());
                    putReward(entry, "normalReward", rewards.get("normal"));
                    putReward(entry, "steelPathReward", rewards.get("steelPath"));
                }
                challenges.add(entry);
            }
            enriched.put("challenges", challenges);
            enriched.put("rewardStatus", "楼层奖励候选（非本周实际抽中项；每周奖励由游戏单独随机）");
            if (catalog.isEmpty())
                enriched.put("rewardStatus", "楼层奖池尚未入库；无法展示候选奖励");
            else
                enriched.put("rewardSource",
                    catalog.values().stream().flatMap(byDifficulty -> byDifficulty.values().stream())
                        .map(DescendiaRewardGroupDTO::sourceUrl).findFirst().orElse(null));
            weeks.add(enriched);
        }
        Map<String, Object> data = new LinkedHashMap<>(current.data());
        data.put("items", weeks);
        return new WarframeWorldStateCommandVO(current.text(), data);
    }

    /**
     * 仅给有正式定义的奖励楼层补候选奖池，其他层不杜撰奖励。
     *
     * @param challenge 当前楼层的展示记录。
     * @param key 难度对应的展示字段。
     * @param group 固定奖励或候选奖池；无奖励时为空。
     */
    private static void putReward(Map<String, Object> challenge, String key, DescendiaRewardGroupDTO group) {
        if (group == null)
            return;
        Map<String, Object> reward = new LinkedHashMap<>();
        reward.put("label", group.label());
        reward.put("options", group.options());
        reward.put("guaranteed", group.guaranteed());
        if (group.note() != null)
            reward.put("note", group.note());
        challenge.put(key, reward);
    }

    private WarframeWorldStateCommandVO steelPath(WorldStateDTO state) {
        WarframeWorldStateCommandVO current = WarframeWorldStateProjector.steelPath(state);
        if (!state.steelPathOfferings().isEmpty())
            return current;
        var vendors = officialLookup.records(WarframeOfficialDataDomain.VENDOR, List.of(STEEL_PATH_VENDOR));
        if (vendors == null)
            return current;
        var record = vendors.get(STEEL_PATH_VENDOR.toLowerCase(Locale.ROOT));
        if (record == null)
            return current;
        if (!(record.data() instanceof OfficialVendorDTO vendor) || vendor.getItems() == null)
            return current;
        boolean missingUmbraBlueprintIcon = vendor.getItems().stream().anyMatch(entry -> entry != null
            && WarframeWorldStateDisplayConstants.UMBRA_FORMA_STORE_BLUEPRINT.equals(entry.getStoreItem())
            && (entry.getItemReferences() == null || entry.getItemReferences().stream()
                .noneMatch(ref -> ref != null && ref.getIconUrl() != null && ref.getIconUrl().startsWith("https://"))));
        String umbraProductIcon = null;
        if (missingUmbraBlueprintIcon) {
            String productKey = WarframeWorldStateDisplayConstants.UMBRA_FORMA_PRODUCT;
            var products = officialLookup.records(WarframeOfficialDataDomain.ITEM_LOCALIZATION, List.of(productKey));
            var product = products == null ? null : products.get(productKey.toLowerCase(Locale.ROOT));
            if (product != null)
                umbraProductIcon = WarframeOfficialValues.iconUrl(product.data());
        }
        List<Map<String, Object>> candidates = new ArrayList<>();
        for (var entry : vendor.getItems()) {
            if (!Boolean.TRUE.equals(entry.getRotatedWeekly()))
                continue;
            var reference = entry.getItemReferences() == null ? null
                : entry.getItemReferences().stream()
                    .filter(value -> value != null && entry.getStoreItem() != null
                        && entry.getStoreItem().equals(value.getUniqueName()))
                    .findFirst().orElseGet(() -> entry.getItemReferences() == null ? null
                        : entry.getItemReferences().stream().filter(value -> value != null).findFirst().orElse(null));
            Map<String, Object> item = new LinkedHashMap<>();
            String name =
                reference == null ? null : firstText(reference.getNameZh(), reference.getNameEn(), reference.getName());
            item.put("item", name == null ? entry.getStoreItem() : name);
            item.put("storeItem", entry.getStoreItem());
            if (entry.getQuantity() != null)
                item.put("count", entry.getQuantity());
            if (reference != null && reference.getIconUrl() != null && reference.getIconUrl().startsWith("https://")) {
                item.put("imageUrl", reference.getIconUrl());
            } else if (WarframeWorldStateDisplayConstants.UMBRA_FORMA_STORE_BLUEPRINT.equals(entry.getStoreItem())
                && umbraProductIcon != null && umbraProductIcon.startsWith("https://")) {
                // 已采纳蓝图没有图标时借用对应成品的官方图片，不替换蓝图名称与兑换价格。
                item.put("imageUrl", umbraProductIcon);
            }
            if (entry.getItemPrices() != null) {
                item.put("prices", entry.getItemPrices().stream().filter(value -> value != null).map(price -> {
                    Map<String, Object> amount = new LinkedHashMap<>();
                    var currency = price.getItemReferences() == null ? null
                        : price.getItemReferences().stream().filter(value -> value != null).findFirst().orElse(null);
                    amount.put("item", currency == null ? price.getItemType()
                        : firstText(currency.getNameZh(), currency.getNameEn(), currency.getName()));
                    amount.put("count", price.getItemCount());
                    return amount;
                }).toList());
                List<String> costs = items(item.get("prices")).stream()
                    .filter(cost -> cost.get("count") != null && cost.get("item") != null)
                    .map(cost -> cost.get("count") + " " + cost.get("item")).toList();
                if (!costs.isEmpty())
                    item.put("price", String.join("、", costs));
            }
            candidates.add(item);
        }
        Map<String, Object> data = new LinkedHashMap<>(current.data());
        SteelPathRotationDTO rotation = steelPathRotation.current(state.metadata().providerTimestamp());
        if (rotation != null) {
            Map<String, Object> selected = candidates.stream()
                .filter(candidate -> rotation.storeItem().equals(candidate.get("storeItem"))).findFirst().orElse(null);
            if (selected != null) {
                Map<String, Object> featured = new LinkedHashMap<>(selected);
                featured.remove("storeItem");
                featured.put("rotationIndex", rotation.index() + 1);
                featured.put("activationInstant", rotation.activation().toString());
                featured.put("expiryInstant", rotation.expiry().toString());
                data.put("itemCount", 1);
                List<Map<String, Object>> ordered = new ArrayList<>();
                for (int index = 0; index < rotation.orderedItems().size(); index++) {
                    String key = rotation.orderedItems().get(index);
                    int sequence = index + 1;
                    candidates.stream().filter(candidate -> key.equals(candidate.get("storeItem"))).findFirst()
                        .ifPresent(candidate -> {
                            Map<String, Object> entry = new LinkedHashMap<>(candidate);
                            entry.remove("storeItem");
                            entry.put("rotationIndex", sequence);
                            ordered.add(entry);
                        });
                }
                featured.put("rotation", ordered);
                data.put("items", List.of(featured));
                data.put("rotation", ordered);
                return new WarframeWorldStateCommandVO("本周钢铁奖励："
                    + (featured.get("count") instanceof BigDecimal count && count.compareTo(BigDecimal.ONE) > 0
                        ? count.toPlainString() + " × " : "")
                    + featured.get("item"), data);
            }
        }
        candidates.forEach(candidate -> candidate.remove("storeItem"));
        data.put("items", candidates);
        data.put("candidateNotice", STEEL_PATH_CANDIDATE_NOTICE);
        data.put("itemCount", candidates.size());
        return new WarframeWorldStateCommandVO(current.text(), data);
    }

    /** @param candidates 候选文本；@return 第一个非空文本。 */
    private static String firstText(String... candidates) {
        for (String candidate : candidates)
            if (candidate != null && !candidate.isBlank())
                return candidate;
        return null;
    }

    /** @param code 官方集团代码；@return 未在独立地区赏金分区展示的集团。 */
    private static boolean syndicateOnly(String code) {
        if (Syndicate.supports(code))
            return false;
        for (WarframeOpenWorldSyndicate region : WarframeOpenWorldSyndicate.values())
            if (region.matches(code))
                return false;
        return true;
    }

    /**
     * 按地区拆分补充赏金，两种管理视图共用相同分区定义。
     *
     * @param state 当前已解析世界状态。
     * @return 各集团独立的任务、轮次状态与候选奖励池投影。
     */
    private static List<SectionProjection> bountyProjections(WorldStateDTO state) {
        return Arrays.stream(Syndicate.values()).map(syndicate -> new SectionProjection(syndicate.sectionKey(),
            WarframeWorldStateProjector.bountyCycle(state, syndicate))).toList();
    }

    /**
     * 从展示投影中取得条目列表，缺省时返回空集合。
     *
     * @param value 待处理的字段值。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<Map<String, Object>> items(Object value) {
        if (!(value instanceof List<?> values)) {
            return List.of();
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object item : values) {
            if (!(item instanceof Map<?, ?> source)) {
                continue;
            }
            Map<String, Object> copy = new LinkedHashMap<>();
            source.forEach((key, entryValue) -> copy.put(String.valueOf(key), entryValue));
            result.add(Collections.unmodifiableMap(copy));
        }
        return List.copyOf(result);
    }

    /**
     * 读取已经组织完成的展示字段中的文本值。
     *
     * @param data 当前操作的数据。
     * @param key 当前记录或字段的查找键。
     * @return 已经组织完成的展示字段中的文本值对应的文本。
     */
    private static String stringValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value == null ? null : String.valueOf(value);
    }

    private record SectionProjection(String key, WarframeWorldStateCommandVO output) {
    }
}
