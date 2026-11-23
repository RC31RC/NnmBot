package com.nnm.bot.warframe.runtime.service.worldstate.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.runtime.cache.worldstate.WorldStateRuntimeSnapshot;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeDescendiaService;
import com.nnm.bot.warframe.runtime.service.vendor.WarframeVendorRotationService;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import cn.hutool.core.util.ObjUtil;
import com.nnm.bot.warframe.runtime.constant.WarframeBountyCycleConstants.Syndicate;
import com.nnm.bot.warframe.runtime.constant.WarframeConquestType;
import com.nnm.bot.warframe.runtime.constant.WarframeOpenWorldSyndicate;
import com.nnm.bot.warframe.runtime.constant.WarframeSyndicateMissions;
import com.nnm.bot.warframe.runtime.constant.WarframeWorldStateDisplayConstants;
import com.nnm.bot.warframe.runtime.constant.WarframeWorldStateScope;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateResolutionMapper;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateMarkupCleaner;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateCommandVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateFieldInspectionVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateFieldSectionVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateManagementVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateResolutionVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateSectionVO;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeReferenceProjectionEnrichmentService;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeCycleService;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeWorldStateManagementService;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeWorldStateRecordEnrichmentService;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeWorldStateService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 管理端和机器人共用同一套 WorldStateDTO 业务投影，避免后台重新解释原始上游 JSON。
 */
@Service
public class WarframeWorldStateManagementServiceImpl implements WarframeWorldStateManagementService {

    private static final String OTHER_RESEARCH_SECTION = "other-archimedeas";
    private static final Set<String> SPLIT_WORLD_STATE_FIELDS = Set.of("archimedeas", "circuitChoices", "fissures");
    private static final Map<String, FieldBinding> FIELD_BINDINGS = Map.ofEntries(
        Map.entry("alerts", binding("Alerts", "alerts")), Map.entry("sorties", binding("Sorties", "sorties")),
        Map.entry("archon-hunts", binding("LiteSorties", "archonHunts")),
        Map.entry("void-trader", binding("VoidTraders", "voidTrader", "voidTraders")),
        Map.entry("daily-deals", binding("DailyDeals", "dailyDeals")),
        Map.entry("flash-sales", binding("FlashSales", "flashSales")),
        Map.entry("in-game-market", binding("InGameMarket", "marketCategories")),
        Map.entry("global-upgrades", binding("GlobalUpgrades", "globalUpgrades")),
        Map.entry("temporary-activities", binding("Tmp", "temporaryActivity")),
        Map.entry("invasions", binding("Invasions", "invasions")),
        Map.entry("fissures", binding("ActiveMissions", "fissures")),
        Map.entry("steel-fissures", binding("ActiveMissions", "fissures")),
        Map.entry("void-storms", binding("VoidStorms", "activeMissions", "fissures")),
        Map.entry("steel-path", binding("steelPathOfferings", "steelPathOfferings")),
        Map.entry("cycles",
            new FieldBinding(
                List.of("cetusCycle", "vallisCycle", "cambionCycle", "earthCycle", "duviriCycle", "zarimanCycle"),
                List.of("cetusCycle", "vallisCycle", "cambionCycle", "earthCycle", "duviriCycle", "zarimanCycle"))),
        Map.entry("nightwave", binding("SeasonInfo", "nightwave")),
        Map.entry(WarframeCommandCodes.DEEP_ARCHIMEDEAS, binding("Conquests", "archimedeas")),
        Map.entry(WarframeCommandCodes.HEX_ARCHIMEDEAS, binding("Conquests", "archimedeas")),
        Map.entry(OTHER_RESEARCH_SECTION, binding("Conquests", "archimedeas")),
        Map.entry("descents", binding("Descents", "descents")), Map.entry("events", binding("Goals", "events")),
        Map.entry("news", binding("Events", "news")),
        Map.entry("construction-progress",
            new FieldBinding(List.of("ProjectPct", "constructionProgress"), List.of("constructionProgress"))),
        Map.entry("sentient-outposts",
            new FieldBinding(List.of("sentientOutposts", "Tmp"), List.of("sentientOutpost"))),
        Map.entry("clan-initiative", binding("WeeklyVaultBonusRewards", "clanInitiatives")),
        Map.entry("circuit-normal", binding("EndlessXpSchedule", "circuitChoices")),
        Map.entry("circuit-hard", binding("EndlessXpSchedule", "circuitChoices")),
        Map.entry("calendar", binding("KnownCalendarSeasons", "calendarSeasons")),
        Map.entry("vault-trader", binding("PrimeVaultTraders", "vaultTraders")),
        Map.entry("syndicate-missions", binding("SyndicateMissions", "syndicateMissions")),
        Map.entry("ostrons", binding("SyndicateMissions", "syndicateMissions")),
        Map.entry("solaris", binding("SyndicateMissions", "syndicateMissions")),
        Map.entry("entrati", binding("SyndicateMissions", "syndicateMissions")));

    @Resource
    private WarframeWorldStateService worldStateService;
    /** 用同一份协议快照为字段视图计算当前周期。 */
    @Resource
    private WarframeCycleService cycleService;
    @Resource
    private WarframeReferenceProjectionEnrichmentService projectionEnricher;
    @Resource
    private WarframeWorldStateRecordEnrichmentService recordEnricher;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private WarframeDescendiaService descendiaService;
    @Resource
    private WarframeVendorRotationService vendorRotation;

    /**
     * 组装当前世界状态管理视图。
     *
     * @return 当前世界状态各分区及刷新状态的管理视图。
     */
    @Override
    public WarframeWorldStateManagementVO current() {
        WorldStateDTO state = worldStateService.getCurrentState();
        if (ObjUtil.isNull(state)) {
            return new WarframeWorldStateManagementVO(false, null, null, null, false, null, Instant.now().toString(),
                List.of());
        }
        return currentView(state);
    }

    /**
     * 从同一份业务 DTO 组装可视化栏目，供当前视图和字段诊断共用。
     *
     * @param state 当前已解析的世界状态。
     * @return 管理端可视化分区及采集元数据。
     */
    private WarframeWorldStateManagementVO currentView(WorldStateDTO state) {
        Map<String, Object> modeledFields =
            objectMapper.convertValue(state, new TypeReference<LinkedHashMap<String, Object>>() {});
        List<SectionProjection> projections = orderedProjections(projections(state), modeledFields);
        List<WarframeWorldStateCommandVO> originals = projections.stream().map(SectionProjection::output).toList();
        List<WarframeWorldStateCommandVO> outputs =
            recordEnricher.enrich(originals, projectionEnricher.enrichAll(originals));
        List<WarframeWorldStateSectionVO> sections = new ArrayList<>(outputs.size());
        for (int index = 0; index < outputs.size(); index++) {
            add(sections, projections.get(index).key(), outputs.get(index));
        }

        return view(state, sections);
    }

    /**
     * 读取同一世界状态快照中的协议输入、业务解析字段和未完成引用解析。
     *
     * @return 仅包含 Java 模型已声明字段的结构化检查视图。
     */
    @Override
    public WarframeWorldStateFieldInspectionVO fields() {
        WorldStateRuntimeSnapshot snapshot = worldStateService.getCurrentSnapshot();
        if (snapshot == null || snapshot.state() == null) {
            return new WarframeWorldStateFieldInspectionVO(false, false, null, null, null, List.of());
        }

        WorldStateDTO state = cycleService.resolve(snapshot.state());
        Map<String, Object> protocolModel = snapshot.rawData() == null ? Map.of()
            : objectMapper.convertValue(snapshot.rawData(), new TypeReference<LinkedHashMap<String, Object>>() {});
        Map<String, Object> parsedModel =
            objectMapper.convertValue(state, new TypeReference<LinkedHashMap<String, Object>>() {});
        List<WarframeWorldStateFieldSectionVO> sections = currentView(state).sections().stream().map(section -> {
            FieldBinding binding = fieldBinding(section.key());
            Map<String, Object> protocolFields = selectFields(protocolModel, binding.protocolFields());
            Map<String, Object> parsedFields = selectFields(parsedModel, binding.parsedFields());
            Syndicate cycle = Syndicate.fromSectionKey(section.key());
            String syndicateCode = syndicateCode(section.key());
            Predicate<String> researchFilter = researchTypeFilter(section.key());
            boolean syndicateOverview = WarframeCommandCodes.SYNDICATE_MISSIONS.equals(section.key());
            var roundIds = section.items().stream().map(row -> stringValue(row, "id")).filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());
            if ((cycle != null || syndicateCode != null || syndicateOverview)
                && roundIds.size() == section.items().size()) {
                protocolFields = filterRows(protocolFields, "SyndicateMissions", "_id", roundIds::contains);
                parsedFields = filterRows(parsedFields, "syndicateMissions", "id", roundIds::contains);
            } else if (syndicateCode != null) {
                protocolFields = filterRows(protocolFields, "SyndicateMissions", "Tag", syndicateCode::equals);
                parsedFields = filterRows(parsedFields, "syndicateMissions", "syndicate", syndicateCode::equals);
            } else if (syndicateOverview) {
                protocolFields =
                    filterRows(protocolFields, "SyndicateMissions", "Tag", WarframeSyndicateMissions::contains);
                parsedFields =
                    filterRows(parsedFields, "syndicateMissions", "syndicate", WarframeSyndicateMissions::contains);
            } else if (researchFilter != null) {
                protocolFields = filterRows(protocolFields, "Conquests", "Type", researchFilter);
                parsedFields = filterRows(parsedFields, "archimedeas", "typeKey", researchFilter);
            }
            return new WarframeWorldStateFieldSectionVO(section.key(), section.title(), section.available(),
                section.itemCount(), protocolFields, parsedFields,
                Map.of("title", section.title(), "items", section.items()), section.pendingResolutions());
        }).toList();

        return new WarframeWorldStateFieldInspectionVO(true, snapshot.rawData() != null, snapshot.source(),
            instantValue(snapshot.fetchedAt()), snapshot.contentHash(), sections);
    }

    private static FieldBinding binding(String protocolField, String... parsedFields) {
        return new FieldBinding(List.of(protocolField), List.of(parsedFields));
    }

    private static FieldBinding fieldBinding(String sectionKey) {
        if (sectionKey.startsWith("bounty-cycle-"))
            return binding("SyndicateMissions", "syndicateMissions");
        return FIELD_BINDINGS.getOrDefault(sectionKey, FieldBinding.EMPTY);
    }

    private static Map<String, Object> selectFields(Map<String, Object> model, List<String> fields) {
        Map<String, Object> selected = new LinkedHashMap<>();
        for (String field : fields) {
            if (model.containsKey(field))
                selected.put(field, model.get(field));
        }
        return selected;
    }

    /** @param sectionKey 当前管理栏目；@return 该栏目唯一对应的集团代码，不适用时为空。 */
    private static String syndicateCode(String sectionKey) {
        for (WarframeOpenWorldSyndicate openWorld : WarframeOpenWorldSyndicate.values())
            if (openWorld.sectionKey().equals(sectionKey))
                return openWorld.code();
        return null;
    }

    private static Predicate<String> researchTypeFilter(String sectionKey) {
        return switch (sectionKey) {
            case WarframeCommandCodes.DEEP_ARCHIMEDEAS -> WarframeConquestType.DEEP::matches;
            case WarframeCommandCodes.HEX_ARCHIMEDEAS -> WarframeConquestType.HEX::matches;
            case OTHER_RESEARCH_SECTION ->
                type -> !WarframeConquestType.DEEP.matches(type) && !WarframeConquestType.HEX.matches(type);
            default -> null;
        };
    }

    /**
     * 按官方业务身份对齐协议模型和 Java 模型中的分区列表。
     *
     * @param fields 已选中的字段。
     * @param listField 任务列表字段。
     * @param identityField 各模型中记录身份的字段名。
     * @param matches 当前分区的身份筛选规则，缺少字符串身份时传入 null。
     * @return 仅保留所属分区的列表，顺序不变。
     */
    private static Map<String, Object> filterRows(Map<String, Object> fields, String listField, String identityField,
        Predicate<String> matches) {
        Object value = fields.get(listField);
        if (!(value instanceof List<?> rows))
            return fields;
        List<?> filtered = rows.stream().filter(row -> row instanceof Map<?, ?> entry
            && matches.test(entry.get(identityField) instanceof String code ? code : null)).toList();
        Map<String, Object> result = new LinkedHashMap<>(fields);
        result.put(listField, filtered);
        return result;
    }

    private static final Map<String,
        String> WORLD_STATE_FIELD_TITLES = Map.ofEntries(Map.entry("alerts", "警报"), Map.entry("fissures", "虚空裂缝"),
            Map.entry("invasions", "入侵"), Map.entry("sortie", "当前突击"), Map.entry("cetusCycle", "夜灵平原周期"),
            Map.entry("vallisCycle", "奥布山谷周期"), Map.entry("cambionCycle", "魔胎之境周期"), Map.entry("voidTrader", "当前虚空商人"),
            Map.entry("nightwave", "午夜电波"), Map.entry("timestamp", "上游时间"), Map.entry("sorties", "突击列表"),
            Map.entry("voidTraders", "虚空商人列表"), Map.entry("earthCycle", "地球周期"), Map.entry("duviriCycle", "双衍王境周期"),
            Map.entry("zarimanCycle", "扎里曼周期"), Map.entry("dailyDeals", "每日特惠"), Map.entry("events", "世界事件"),
            Map.entry("news", "新闻"), Map.entry("syndicateMissions", "集团任务"), Map.entry("steelPathOfferings", "钢铁之路奖励"),
            Map.entry("activeMissions", "活动任务"), Map.entry("archonHunts", "执刑官猎杀"), Map.entry("globalUpgrades", "全局增益"),
            Map.entry("flashSales", "限时促销"), Map.entry("metadata", "采集信息"), Map.entry("constructionProgress", "舰队建造进度"),
            Map.entry("sentientOutpost", "Sentient 前哨"), Map.entry("clanInitiatives", "氏族每周行动"),
            Map.entry("circuitChoices", "回廊装备"), Map.entry("calendarSeasons", "1999 日历赛季"),
            Map.entry("vaultTraders", "Prime 宝库商人"), Map.entry("descents", "沉沦之地"),
            Map.entry("marketCategories", "商城分类"), Map.entry("temporaryActivity", "社区活动与寻呼机"));

    private SectionProjection modeledProjection(String key, String title, Object values) {
        List<Map<String, Object>> rows = values == null ? List.of()
            : objectMapper.convertValue(values, new TypeReference<List<Map<String, Object>>>() {});
        return new SectionProjection(key,
            new WarframeWorldStateCommandVO(title, Map.of("title", title, "items", rows)));
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
        sections.add(new WarframeWorldStateSectionVO(WarframeWorldStateDisplayConstants.sectionKey(key),
            WarframeWorldStateDisplayConstants.sectionTitle(key, stringValue(output.data(), "title")),
            CollUtil.isNotEmpty(displayItems), displayItems.size(), displayItems,
            WarframeWorldStateResolutionMapper.pending(output.data(), output.unresolvedReferences())));
    }

    /**
     * 规范化两种视图的栏目并补齐共同目录；保留已存在投影，只为缺失类别创建原 DTO 投影或空栏目。
     *
     * @param projections 当前模式已组织的展示投影。
     * @param modeledFields Jackson 映射后的完整字段集合。
     * @return 栏目键、标题与顺序统一后的投影集合。
     */
    private List<SectionProjection> orderedProjections(List<SectionProjection> projections,
        Map<String, Object> modeledFields) {
        Map<String, SectionProjection> sections = new LinkedHashMap<>();
        projections.stream().map(projection -> {
            String key = WarframeWorldStateDisplayConstants.sectionKey(projection.key());
            WarframeWorldStateCommandVO output = projection.output();
            String title = WarframeWorldStateDisplayConstants.sectionTitle(key, stringValue(output.data(), "title"));
            Map<String, Object> data = new LinkedHashMap<>(output.data());
            data.put("title", title);
            return new SectionProjection(key,
                new WarframeWorldStateCommandVO(output.text(), data, output.unresolvedReferences()));
        }).filter(projection -> !"persistent-enemies".equals(projection.key()))
            .forEach(projection -> sections.putIfAbsent(projection.key(), projection));

        modeledFields.forEach((field, value) -> {
            if (WarframeWorldStateScope.NON_BUSINESS_SECTIONS.contains(field)
                || WarframeWorldStateDisplayConstants.DUPLICATE_WORLD_STATE_FIELDS.contains(field)
                || WarframeWorldStateDisplayConstants.CYCLE_WORLD_STATE_FIELDS.contains(field)
                || SPLIT_WORLD_STATE_FIELDS.contains(field) || "persistentEnemies".equals(field)) {
                return;
            }
            String key = WarframeWorldStateDisplayConstants.sectionKey(field);
            if (sections.containsKey(key))
                return;
            List<Map<String, Object>> rows =
                value == null ? List.of() : parsedRows(value instanceof List<?> list ? list : List.of(value));
            String title = WORLD_STATE_FIELD_TITLES.getOrDefault(field, key);
            sections.put(key, modeledProjection(key, title, rows));
        });

        WarframeWorldStateDisplayConstants.sectionKeys()
            .forEach(key -> sections.computeIfAbsent(key, section -> modeledProjection(section,
                WarframeWorldStateDisplayConstants.sectionTitle(section, section), List.of())));
        Arrays.stream(Syndicate.values()).forEach(syndicate -> sections.computeIfAbsent(syndicate.sectionKey(),
            key -> modeledProjection(key, syndicate.title(), List.of())));

        return sections.values().stream()
            .sorted(Comparator
                .comparingInt(
                    (SectionProjection projection) -> WarframeWorldStateDisplayConstants.sectionOrder(projection.key()))
                .thenComparing(SectionProjection::key))
            .toList();
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
            new SectionProjection("invasions", WarframeWorldStateProjector.invasions(state)),
            new SectionProjection("fissures",
                WarframeWorldStateProjector.fissures(state, mission -> !mission.hard() && !mission.voidStorm(), "裂隙")),
            new SectionProjection("steel-fissures",
                WarframeWorldStateProjector.fissures(state, mission -> mission.hard() && !mission.voidStorm(), "钢铁裂隙")),
            new SectionProjection("void-storms",
                WarframeWorldStateProjector.fissures(state, WorldStateDTO.ActiveMissionDTO::voidStorm, "九重天裂隙")),
            new SectionProjection("steel-path", vendorRotation.steelPath(state)),
            new SectionProjection("cycles", WarframeWorldStateProjector.cycles(state)),
            new SectionProjection("nightwave", WarframeWorldStateProjector.nightwave(state))));
        sections.addAll(bountyProjections(state));
        sections.addAll(List.of(
            // 官方快照 Conquests；科研条件保留原始值，术语由代码簿/补充数据解析。
            researchProjection(state, WarframeCommandCodes.DEEP_ARCHIMEDEAS, "深层科研"),
            researchProjection(state, WarframeCommandCodes.HEX_ARCHIMEDEAS, "时光科研"),
            new SectionProjection("descents", descendiaService.project(state)),
            new SectionProjection("events", WarframeWorldStateProjector.events(state)),
            new SectionProjection("news", WarframeWorldStateProjector.news(state)),
            new SectionProjection("construction-progress", WarframeWorldStateProjector.constructionProgress(state)),
            new SectionProjection("sentient-outposts", WarframeWorldStateProjector.sentientOutposts(state)),
            new SectionProjection("clan-initiative", WarframeWorldStateProjector.clanInitiatives(state)),
            new SectionProjection("circuit-normal", WarframeWorldStateProjector.circuit(state, false)),
            new SectionProjection("circuit-hard", WarframeWorldStateProjector.circuit(state, true)),
            new SectionProjection("calendar", WarframeWorldStateProjector.calendar(state)),
            new SectionProjection("vault-trader", WarframeWorldStateProjector.vaultTrader(state)),
            new SectionProjection(WarframeCommandCodes.SYNDICATE_MISSIONS,
                WarframeWorldStateProjector.syndicateMissions(state)),
            new SectionProjection("ostrons",
                WarframeWorldStateProjector.syndicate(state, "希图斯", WarframeOpenWorldSyndicate.CETUS::matches)),
            new SectionProjection("solaris",
                WarframeWorldStateProjector.syndicate(state, "索拉里斯", WarframeOpenWorldSyndicate.SOLARIS::matches)),
            new SectionProjection("entrati",
                WarframeWorldStateProjector.syndicate(state, "英择谛", WarframeOpenWorldSyndicate.ENTRATI::matches))));
        SectionProjection otherResearch = researchProjection(state, OTHER_RESEARCH_SECTION, "其他科研");
        if (!items(otherResearch.output().data().get("items")).isEmpty()) {
            sections.add(otherResearch);
        }
        return sections;
    }

    private static SectionProjection researchProjection(WorldStateDTO state, String sectionKey, String title) {
        Predicate<String> matches = researchTypeFilter(sectionKey);
        return new SectionProjection(sectionKey,
            WarframeWorldStateProjector.archimedeas(state, title, conquest -> matches.test(conquest.typeKey())));
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

    private record FieldBinding(List<String> protocolFields, List<String> parsedFields) {
        private static final FieldBinding EMPTY = new FieldBinding(List.of(), List.of());
    }
}
