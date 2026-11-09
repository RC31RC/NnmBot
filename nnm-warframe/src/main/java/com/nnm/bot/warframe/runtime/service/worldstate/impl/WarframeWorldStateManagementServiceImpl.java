package com.nnm.bot.warframe.runtime.service.worldstate.impl;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.warframe.runtime.cache.worldstate.WorldStateRuntimeSnapshot;
import cn.hutool.core.util.ObjectUtil;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateCommandVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateManagementVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateRawVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateSectionVO;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeReferenceProjectionEnrichmentService;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeWorldStateManagementService;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeWorldStateService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

/**
 * 管理端和机器人共用同一套 WorldStateDTO 业务投影，避免后台重新解释原始上游 JSON。
 */
@Service
public class WarframeWorldStateManagementServiceImpl implements WarframeWorldStateManagementService {

    @Resource
    private WarframeWorldStateService worldStateService;
    @Resource
    private WarframeReferenceProjectionEnrichmentService projectionEnricher;
    @Resource
    private ObjectMapper objectMapper;

    /**
     * 组装当前世界状态管理视图。
     *
     * @return 当前世界状态各分区及刷新状态的管理视图。
     */
    @Override
    public WarframeWorldStateManagementVO current() {
        WorldStateDTO state = worldStateService.getCurrentState();
        if (ObjectUtil.isNull(state)) {
            return new WarframeWorldStateManagementVO(false, null, null, null, false, null, List.of());
        }

        List<SectionProjection> projections = projections(state);
        List<WarframeWorldStateCommandVO> outputs =
            projectionEnricher.enrichAll(projections.stream().map(SectionProjection::output).toList());
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
            Map.entry("arbitration", "世界状态仲裁"), Map.entry("nightwave", "午夜电波"), Map.entry("timestamp", "上游时间"),
            Map.entry("sorties", "突击列表"), Map.entry("voidTraders", "虚空商人列表"), Map.entry("earthCycle", "地球周期"),
            Map.entry("duviriCycle", "双衍王境周期"), Map.entry("zarimanCycle", "扎里曼周期"), Map.entry("dailyDeals", "每日特惠"),
            Map.entry("events", "世界事件"), Map.entry("news", "新闻"), Map.entry("syndicateMissions", "集团任务"),
            Map.entry("steelPathOfferings", "钢铁之路奖励"), Map.entry("activeMissions", "活动任务"),
            Map.entry("archonHunts", "执刑官猎杀"), Map.entry("buildLabel", "游戏版本"), Map.entry("globalUpgrades", "全局增益"),
            Map.entry("flashSales", "闪购"), Map.entry("persistentEnemies", "持续敌人"), Map.entry("metadata", "采集信息"),
            Map.entry("archimedeas", "科研任务"), Map.entry("conclaveChallenges", "武形秘仪挑战"),
            Map.entry("constructionProgress", "舰队建造进度"), Map.entry("simaris", "中枢研究"),
            Map.entry("sentientOutpost", "Sentient 前哨"), Map.entry("clanInitiatives", "氏族每周行动"),
            Map.entry("circuitChoices", "回廊装备"), Map.entry("calendarSeasons", "1999 日历赛季"),
            Map.entry("vaultTraders", "Prime 宝库商人"), Map.entry("weeklyChallenges", "每周挑战"),
            Map.entry("darkSectors", "黑暗地带"), Map.entry("descents", "深层下降"));

    /**
     * 将已解析 DTO 转换为独立展示投影，保留所有嵌套字段。
     *
     * @return 当前实例完整解析快照；没有数据时返回明确不可用状态。
     */
    @Override
    public WarframeWorldStateManagementVO parsed() {
        WorldStateDTO state = worldStateService.getCurrentState();
        if (state == null) {
            return new WarframeWorldStateManagementVO(false, null, null, null, false, null, List.of());
        }
        Map<String, Object> fields =
            objectMapper.convertValue(state, new TypeReference<LinkedHashMap<String, Object>>() {});
        List<WarframeWorldStateSectionVO> sections = new ArrayList<>();
        fields.forEach((key, value) -> {
            List<Map<String, Object>> rows;
            if (value == null) {
                rows = List.of();
            } else if (value instanceof List<?>) {
                rows = parsedRows((List<?>)value);
            } else {
                rows = parsedRows(List.of(value));
            }
            sections.add(new WarframeWorldStateSectionVO(key, PARSED_SECTION_TITLES.getOrDefault(key, key),
                !rows.isEmpty(), rows.size(), rows));
        });
        return view(state, sections);
    }

    /**
     * 组织详细分区的展示行；空值保留在字段中，不重解释上游协议。
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
        return new WarframeWorldStateManagementVO(true, stringValue(data, "source"), stringValue(data, "fetchedAt"),
            stringValue(data, "providerTimestamp"), Boolean.TRUE.equals(data.get("stale")),
            stringValue(data, "updatedAt"), List.copyOf(sections));
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
        sections.add(new WarframeWorldStateSectionVO(key, stringValue(output.data(), "title"),
            CollUtil.isNotEmpty(items), items.size(), items));
    }

    /**
     * 组织当前世界状态各业务分区的展示投影。
     *
     * @param state 已经解析的当前世界状态。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<SectionProjection> projections(WorldStateDTO state) {
        return List.of(new SectionProjection("alerts", WarframeWorldStateProjector.alerts(state)),
            new SectionProjection("sorties", WarframeWorldStateProjector.sorties(state, false)),
            new SectionProjection("archon-hunts", WarframeWorldStateProjector.sorties(state, true)),
            new SectionProjection("void-trader", WarframeWorldStateProjector.voidTrader(state)),
            new SectionProjection("arbitration", WarframeWorldStateProjector.arbitration(state, false)),
            new SectionProjection("daily-deals", WarframeWorldStateProjector.dailyDeals(state)),
            new SectionProjection("invasions", WarframeWorldStateProjector.invasions(state)),
            new SectionProjection("fissures",
                WarframeWorldStateProjector.fissures(state, mission -> !mission.hard() && !mission.voidStorm(), "裂隙")),
            new SectionProjection("steel-fissures",
                WarframeWorldStateProjector.fissures(state, mission -> mission.hard() && !mission.voidStorm(), "钢铁裂隙")),
            new SectionProjection("void-storms",
                WarframeWorldStateProjector.fissures(state, WorldStateDTO.ActiveMissionDTO::voidStorm, "九重天裂隙")),
            new SectionProjection("steel-path", WarframeWorldStateProjector.steelPath(state)),
            new SectionProjection("cycles", WarframeWorldStateProjector.cycles(state)),
            new SectionProjection("nightwave", WarframeWorldStateProjector.nightwave(state)),
            // 官方快照 Conquests；科研条件保留原始值，术语由代码簿/补充数据解析。
            new SectionProjection("archimedeas",
                WarframeWorldStateProjector.archimedeas(state, "科研", conquest -> true)),
            new SectionProjection("deep-archimedeas",
                WarframeWorldStateProjector.archimedeas(state, "深层科研",
                    conquest -> "CTLAB".equals(normalize(conquest.typeKey())))),
            new SectionProjection("hex-archimedeas",
                WarframeWorldStateProjector.archimedeas(state, "时光科研",
                    conquest -> "CTHEX".equals(normalize(conquest.typeKey())))),
            new SectionProjection("descents", WarframeWorldStateProjector.descents(state)),
            new SectionProjection("events", WarframeWorldStateProjector.events(state)),
            new SectionProjection("news", WarframeWorldStateProjector.news(state)),
            new SectionProjection("conclave", WarframeWorldStateProjector.conclave(state)),
            new SectionProjection("construction-progress", WarframeWorldStateProjector.constructionProgress(state)),
            new SectionProjection("simaris", WarframeWorldStateProjector.simaris(state)),
            new SectionProjection("sentient-outposts", WarframeWorldStateProjector.sentientOutposts(state)),
            new SectionProjection("clan-initiative", WarframeWorldStateProjector.clanInitiatives(state)),
            new SectionProjection("circuit-normal", WarframeWorldStateProjector.circuit(state, false)),
            new SectionProjection("circuit-hard", WarframeWorldStateProjector.circuit(state, true)),
            new SectionProjection("calendar", WarframeWorldStateProjector.calendar(state)),
            new SectionProjection("vault-trader", WarframeWorldStateProjector.vaultTrader(state)),
            new SectionProjection("timestamp", WarframeWorldStateProjector.timestamp(state)),
            new SectionProjection("syndicate-missions",
                WarframeWorldStateProjector.syndicate(state, "集团任务", value -> true)),
            new SectionProjection("ostrons",
                WarframeWorldStateProjector.syndicate(state, "希图斯", contains("ostrons", "cetus", "希图斯"))),
            new SectionProjection("solaris",
                WarframeWorldStateProjector.syndicate(state, "索拉里斯", contains("solaris", "vallis", "索拉里斯"))),
            new SectionProjection("entrati",
                WarframeWorldStateProjector.syndicate(state, "英择谛", contains("entrati", "deimos", "英择谛"))));
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
            result.add(Map.copyOf(copy));
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

    /**
     * 判断文本或集合是否包含指定内容。
     *
     * @param candidates 候选。
     * @return 筛选谓词对象。
     */
    private static Predicate<String> contains(String... candidates) {
        return value -> {
            String normalized = value == null ? "" : value.toLowerCase(Locale.ROOT);
            for (String candidate : candidates) {
                if (normalized.contains(candidate.toLowerCase(Locale.ROOT))) {
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * 规范化输入，消除查询或比较时的格式差异。
     *
     * @param value 待校验或规范化的文本。
     * @return 规范化后的文本，供比较或查找使用。
     */
    private static String normalize(String value) {
        return value == null ? "" : value.replaceAll("\\s+", "").toUpperCase(Locale.ROOT);
    }

    private record SectionProjection(String key, WarframeWorldStateCommandVO output) {
    }
}
