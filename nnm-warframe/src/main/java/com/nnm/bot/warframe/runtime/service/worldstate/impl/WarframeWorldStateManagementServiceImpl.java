package com.nnm.bot.warframe.runtime.service.worldstate.impl;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.runtime.cache.worldstate.WorldStateRuntimeSnapshot;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

/**
 * 管理端和机器人共用同一套 WorldStateDTO 业务投影，避免后台重新解释原始上游 JSON。
 */
@Service
public class WarframeWorldStateManagementServiceImpl
        implements WarframeWorldStateManagementService {

    private static final Map<String, String> RAW_SECTION_TITLES = Map.ofEntries(
            Map.entry("conquests", "征服任务"),
            Map.entry("constructionprojects", "建造项目"),
            Map.entry("descents", "恶魔降临"),
            Map.entry("endlessxpschedule", "无尽经验计划"),
            Map.entry("experimentrecommended", "推荐实验"),
            Map.entry("featuredguilds", "精选氏族"),
            Map.entry("flashsales", "限时闪购"),
            Map.entry("globalupgrades", "全局增益"),
            Map.entry("hubevents", "中继站活动"),
            Map.entry("ingamemarket", "游戏内市场"),
            Map.entry("knowncalendarseasons", "已知日历季节"),
            Map.entry("libraryinfo", "图鉴信息"),
            Map.entry("nodeoverrides", "节点覆盖"),
            Map.entry("pvpactivetournaments", "PVP 进行中赛事"),
            Map.entry("pvpalternativemodes", "PVP 另类模式"),
            Map.entry("pvpchallengeinstances", "PVP 挑战"),
            Map.entry("persistentenemies", "持续敌人"),
            Map.entry("primeaccessavailability", "Prime Access"),
            Map.entry("primetokenavailability", "Prime Token"),
            Map.entry("primevaultavailabilities", "Prime Vault 可用性"),
            Map.entry("primevaulttraders", "Prime Vault 商人"),
            Map.entry("projectpct", "项目进度"),
            Map.entry("pvpsales", "PVP 销售"),
            Map.entry("skusales", "SKU 销售"),
            Map.entry("twitchpromos", "Twitch 促销"),
            Map.entry("weeklyvaultbonusrewards", "每周 Vault 奖励"));

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
        List<WarframeWorldStateCommandVO> outputs = projectionEnricher.enrichAll(
                projections.stream().map(SectionProjection::output).toList());
        List<WarframeWorldStateSectionVO> sections = new ArrayList<>(outputs.size());
        for (int index = 0; index < outputs.size(); index++) {
            add(sections, projections.get(index).key(), outputs.get(index));
        }
        sections.addAll(rawSections(worldStateService.getCurrentSnapshot()));

        WarframeWorldStateCommandVO metadata = WarframeWorldStateProjector.alerts(state);
        Map<String, Object> data = metadata.data();
        return new WarframeWorldStateManagementVO(
                true,
                stringValue(data, "source"),
                stringValue(data, "fetchedAt"),
                stringValue(data, "providerTimestamp"),
                Boolean.TRUE.equals(data.get("stale")),
                stringValue(data, "updatedAt"),
                List.copyOf(sections));
    }

    /**
     * WorldStateDTO 的顶层字段会随官方版本增加。已建模字段走稳定业务投影，
     *   同时保留原始响应中的每个顶层字段，确保新增字段、技术字段和解析失败
     *   的字段仍可在管理端查看。动态世界数据只存在当前 JVM 快照中。
     *
     * @param snapshot 快照。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WarframeWorldStateSectionVO> rawSections(WorldStateRuntimeSnapshot snapshot) {
        if (snapshot == null || StrUtil.isBlank(snapshot.rawJson())) {
            return List.of();
        }
        try {
            // 原文检查面板保留未建模的顶层字段；这些数据不参与业务解析或任务补全。
            Map<String, Object> root = objectMapper.readValue(snapshot.rawJson(), new TypeReference<>() {});
            if (root == null) return List.of();
            List<WarframeWorldStateSectionVO> sections = new ArrayList<>();
            root.forEach((key, value) -> {
                String normalized = key.toLowerCase(Locale.ROOT);
                List<Map<String, Object>> items = rawItems(value);
                sections.add(new WarframeWorldStateSectionVO(
                        "raw-" + normalized,
                        RAW_SECTION_TITLES.getOrDefault(normalized, "原始：" + key),
                        value != null && CollUtil.isNotEmpty(items), items.size(), items));
            });
            return List.copyOf(sections);
        } catch (JsonProcessingException exception) {
            // 原始投影只是管理端补充数据，解析失败时不能影响已完成的业务投影。
            return List.of();
        }
    }

    /**
     * 将原始快照的顶层成员组织为后台诊断条目。
     *
     * @param value 待处理的字段值。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<Map<String, Object>> rawItems(Object value) {
        List<Map<String, Object>> items = new ArrayList<>();
        if (value instanceof List<?> values) {
            values.forEach(item -> items.add(rawItem(item)));
        } else {
            items.add(rawItem(value));
        }
        return List.copyOf(items);
    }

    /**
     * 将原始检查面板的一项内容转换为展示字段。
     *
     * @param value 待处理的字段值。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private Map<String, Object> rawItem(Object value) {
        if (!(value instanceof Map<?, ?> source)) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("value", value);
            return Collections.unmodifiableMap(item);
        }
        if (CollUtil.isEmpty(source)) {
            return Map.of("value", Map.of());
        }
        Map<String, Object> item = new LinkedHashMap<>();
        source.forEach((key, entryValue) -> item.put(String.valueOf(key), entryValue));
        return Collections.unmodifiableMap(item);
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
            return new WarframeWorldStateRawVO(
                    false, null, null, null, List.of(), null);
        }
        return new WarframeWorldStateRawVO(
                snapshot.rawJson() != null,
                snapshot.source(),
                snapshot.fetchedAt() == null ? null : snapshot.fetchedAt().toString(),
                snapshot.contentHash(),
                snapshot.topLevelFields(),
                snapshot.rawJson());
    }

    /**
     * 加入一条数据并维护当前容器。
     *
     * @param sections 分区集合。
     * @param key 当前记录或字段的查找键。
     * @param output 需要补全或转换的输出数据。
     */
    private static void add(
            List<WarframeWorldStateSectionVO> sections,
            String key,
            WarframeWorldStateCommandVO output) {
        List<Map<String, Object>> items = items(output.data().get("items"));
        sections.add(new WarframeWorldStateSectionVO(
                key,
                stringValue(output.data(), "title"),
                CollUtil.isNotEmpty(items),
                items.size(),
                items));
    }

    /**
     * 组织当前世界状态各业务分区的展示投影。
     *
     * @param state 已经解析的当前世界状态。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<SectionProjection> projections(WorldStateDTO state) {
        return List.of(
                new SectionProjection("alerts", WarframeWorldStateProjector.alerts(state)),
                new SectionProjection("sorties", WarframeWorldStateProjector.sorties(state, false)),
                new SectionProjection("archon-hunts", WarframeWorldStateProjector.sorties(state, true)),
                new SectionProjection("void-trader", WarframeWorldStateProjector.voidTrader(state)),
                new SectionProjection("arbitration", WarframeWorldStateProjector.arbitration(state, false)),
                new SectionProjection("daily-deals", WarframeWorldStateProjector.dailyDeals(state)),
                new SectionProjection("invasions", WarframeWorldStateProjector.invasions(state)),
                new SectionProjection("fissures", WarframeWorldStateProjector.fissures(
                        state, mission -> !mission.hard() && !mission.voidStorm(), "裂隙")),
                new SectionProjection("steel-fissures", WarframeWorldStateProjector.fissures(
                        state, mission -> mission.hard() && !mission.voidStorm(), "钢铁裂隙")),
                new SectionProjection("void-storms", WarframeWorldStateProjector.fissures(
                        state, WorldStateDTO.ActiveMissionDTO::voidStorm, "九重天裂隙")),
                new SectionProjection("steel-path", WarframeWorldStateProjector.steelPath(state)),
                new SectionProjection("cycles", WarframeWorldStateProjector.cycles(state)),
                new SectionProjection("nightwave", WarframeWorldStateProjector.nightwave(state)),
                // 官方快照 Conquests；科研条件保留原始值，术语由代码簿/补充数据解析。
                new SectionProjection("archimedeas", WarframeWorldStateProjector.archimedeas(
                        state, "科研", conquest -> true)),
                new SectionProjection("deep-archimedeas", WarframeWorldStateProjector.archimedeas(
                        state, "深层科研",
                        conquest -> "CTLAB".equals(normalize(conquest.typeKey())))),
                new SectionProjection("hex-archimedeas", WarframeWorldStateProjector.archimedeas(
                        state, "时光科研",
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
                new SectionProjection("syndicate-missions", WarframeWorldStateProjector.syndicate(
                        state, "集团任务", value -> true)),
                new SectionProjection("ostrons", WarframeWorldStateProjector.syndicate(
                        state, "希图斯", contains("ostrons", "cetus", "希图斯"))),
                new SectionProjection("solaris", WarframeWorldStateProjector.syndicate(
                        state, "索拉里斯", contains("solaris", "vallis", "索拉里斯"))),
                new SectionProjection("entrati", WarframeWorldStateProjector.syndicate(
                        state, "英择谛", contains("entrati", "deimos", "英择谛"))));
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
