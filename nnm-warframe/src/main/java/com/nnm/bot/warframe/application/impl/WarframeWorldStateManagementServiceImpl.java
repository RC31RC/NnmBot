package com.nnm.bot.warframe.application.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.warframe.application.WarframeReferenceProjectionEnricher;
import com.nnm.bot.warframe.application.WarframeWorldStateManagementService;
import com.nnm.bot.warframe.application.WorldStateService;
import com.nnm.bot.warframe.application.WorldStateRuntimeSnapshot;
import com.nnm.bot.warframe.application.plugin.AbstractWorldStateCommandPlugin.WorldStateCommandOutput;
import com.nnm.bot.warframe.application.plugin.WarframeCommandData;
import com.nnm.bot.warframe.domain.model.WorldState;
import com.nnm.bot.warframe.dto.WarframeWorldStateItem;
import com.nnm.bot.warframe.dto.WarframeWorldStateRawItem;
import com.nnm.bot.warframe.dto.WarframeWorldStateSection;
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
 * 管理端和机器人共用同一套 WorldState 业务投影，避免后台重新解释原始上游 JSON。
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
    private WorldStateService worldStateService;
    @Resource
    private WarframeReferenceProjectionEnricher projectionEnricher;
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public WarframeWorldStateItem current() {
        WorldState state = worldStateService.getCurrentState();
        if (ObjectUtil.isNull(state)) {
            return new WarframeWorldStateItem(false, null, null, null, false, null, List.of());
        }

        List<SectionProjection> projections = projections(state);
        List<WorldStateCommandOutput> outputs = projectionEnricher.enrichAll(
                projections.stream().map(SectionProjection::output).toList());
        List<WarframeWorldStateSection> sections = new ArrayList<>(outputs.size());
        for (int index = 0; index < outputs.size(); index++) {
            add(sections, projections.get(index).key(), outputs.get(index));
        }
        sections.addAll(rawSections(worldStateService.getCurrentSnapshot()));

        WorldStateCommandOutput metadata = WarframeCommandData.alerts(state);
        Map<String, Object> data = metadata.data();
        return new WarframeWorldStateItem(
                true,
                stringValue(data, "source"),
                stringValue(data, "fetchedAt"),
                stringValue(data, "providerTimestamp"),
                Boolean.TRUE.equals(data.get("stale")),
                stringValue(data, "updatedAt"),
                List.copyOf(sections));
    }

    /**
     * WorldState 的顶层字段会随官方版本增加。已建模字段走稳定业务投影，
     * 同时保留原始响应中的每个顶层字段，确保新增字段、技术字段和解析失败
     * 的字段仍可在管理端查看。动态世界数据只存在当前 JVM 快照中。
     */
    private List<WarframeWorldStateSection> rawSections(WorldStateRuntimeSnapshot snapshot) {
        if (snapshot == null || StrUtil.isBlank(snapshot.rawJson())) {
            return List.of();
        }
        try {
            JsonNode root = objectMapper.readTree(snapshot.rawJson());
            if (root == null || !root.isObject()) {
                return List.of();
            }
            List<WarframeWorldStateSection> sections = new ArrayList<>();
            root.fields().forEachRemaining(entry -> {
                String normalized = entry.getKey().toLowerCase(Locale.ROOT);
                JsonNode value = entry.getValue();
                List<Map<String, Object>> items = rawItems(value);
                sections.add(new WarframeWorldStateSection(
                        "raw-" + normalized,
                        RAW_SECTION_TITLES.getOrDefault(normalized, "原始：" + entry.getKey()),
                        value != null && !value.isNull() && !items.isEmpty(),
                        items.size(),
                        items));
            });
            return List.copyOf(sections);
        } catch (Exception exception) {
            // 原始投影只是管理端补充数据，解析失败时不能影响已完成的业务投影。
            return List.of();
        }
    }

    private List<Map<String, Object>> rawItems(JsonNode value) {
        List<Map<String, Object>> items = new ArrayList<>();
        if (value.isArray()) {
            value.forEach(item -> items.add(rawItem(item)));
        } else {
            items.add(rawItem(value));
        }
        return List.copyOf(items);
    }

    private Map<String, Object> rawItem(JsonNode value) {
        Object converted = objectMapper.convertValue(value, Object.class);
        if (!(converted instanceof Map<?, ?> source)) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("value", converted);
            return Collections.unmodifiableMap(item);
        }
        if (source.isEmpty()) {
            return Map.of("value", Map.of());
        }
        Map<String, Object> item = new LinkedHashMap<>();
        source.forEach((key, entryValue) -> item.put(String.valueOf(key), entryValue));
        return Collections.unmodifiableMap(item);
    }

    @Override
    public WarframeWorldStateRawItem raw() {
        WorldStateRuntimeSnapshot snapshot = worldStateService.getCurrentSnapshot();
        if (snapshot == null) {
            return new WarframeWorldStateRawItem(
                    false, null, null, null, List.of(), null);
        }
        return new WarframeWorldStateRawItem(
                snapshot.rawJson() != null,
                snapshot.source(),
                snapshot.fetchedAt() == null ? null : snapshot.fetchedAt().toString(),
                snapshot.contentHash(),
                snapshot.topLevelFields(),
                snapshot.rawJson());
    }

    private static void add(
            List<WarframeWorldStateSection> sections,
            String key,
            WorldStateCommandOutput output) {
        List<Map<String, Object>> items = items(output.data().get("items"));
        sections.add(new WarframeWorldStateSection(
                key,
                stringValue(output.data(), "title"),
                !items.isEmpty(),
                items.size(),
                items));
    }

    private static List<SectionProjection> projections(WorldState state) {
        return List.of(
                new SectionProjection("alerts", WarframeCommandData.alerts(state)),
                new SectionProjection("sorties", WarframeCommandData.sorties(state, false)),
                new SectionProjection("archon-hunts", WarframeCommandData.sorties(state, true)),
                new SectionProjection("void-trader", WarframeCommandData.voidTrader(state)),
                new SectionProjection("arbitration", WarframeCommandData.arbitration(state, false)),
                new SectionProjection("daily-deals", WarframeCommandData.dailyDeals(state)),
                new SectionProjection("invasions", WarframeCommandData.invasions(state)),
                new SectionProjection("fissures", WarframeCommandData.fissures(
                        state, mission -> !mission.hard() && !mission.voidStorm(), "裂隙")),
                new SectionProjection("steel-fissures", WarframeCommandData.fissures(
                        state, mission -> mission.hard() && !mission.voidStorm(), "钢铁裂隙")),
                new SectionProjection("void-storms", WarframeCommandData.fissures(
                        state, WorldState.ActiveMission::voidStorm, "九重天裂隙")),
                new SectionProjection("steel-path", WarframeCommandData.steelPath(state)),
                new SectionProjection("cycles", WarframeCommandData.cycles(state)),
                new SectionProjection("nightwave", WarframeCommandData.nightwave(state)),
                new SectionProjection("events", WarframeCommandData.events(state)),
                new SectionProjection("news", WarframeCommandData.news(state)),
                new SectionProjection("ostrons", WarframeCommandData.syndicate(
                        state, "希图斯", contains("ostrons", "cetus", "希图斯"))),
                new SectionProjection("solaris", WarframeCommandData.syndicate(
                        state, "索拉里斯", contains("solaris", "vallis", "索拉里斯"))),
                new SectionProjection("entrati", WarframeCommandData.syndicate(
                        state, "英择谛", contains("entrati", "deimos", "英择谛"))));
    }

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

    private static String stringValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value == null ? null : String.valueOf(value);
    }

    private static Predicate<String> contains(String... candidates) {
        return value -> {
            String normalized = value == null ? "" : value.toLowerCase(java.util.Locale.ROOT);
            for (String candidate : candidates) {
                if (normalized.contains(candidate.toLowerCase(java.util.Locale.ROOT))) {
                    return true;
                }
            }
            return false;
        };
    }

    private record SectionProjection(String key, WorldStateCommandOutput output) {
    }
}
