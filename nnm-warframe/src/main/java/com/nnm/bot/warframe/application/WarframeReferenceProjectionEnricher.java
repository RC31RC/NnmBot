package com.nnm.bot.warframe.application;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.application.plugin.AbstractWorldStateCommandPlugin.WorldStateCommandOutput;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 为管理端、机器人命令和蹲任务统一增强 WorldState 展示投影。
 */
@Service
public class WarframeReferenceProjectionEnricher {

    private static final Set<String> REFERENCE_KEYS = Set.of(
            "item", "reward", "rewarditems",
            "attackerreward", "defenderreward",
            "attackerrewards", "defenderrewards",
            "node", "location", "boss", "missiontype", "modifier", "syndicate");

    @Resource
    private WarframeReferenceResolver referenceResolver;

    public WorldStateCommandOutput enrich(WorldStateCommandOutput output) {
        if (output == null) {
            return null;
        }
        return enrichAll(List.of(output)).getFirst();
    }

    /**
     * 批量增强同一次 WorldState 生成的多个投影，避免每个分区重复加载静态数据索引。
     */
    public List<WorldStateCommandOutput> enrichAll(
            Collection<WorldStateCommandOutput> outputs) {
        if (outputs == null || outputs.isEmpty()) {
            return List.of();
        }
        List<WorldStateCommandOutput> safeOutputs = outputs.stream()
                .filter(java.util.Objects::nonNull)
                .toList();
        if (safeOutputs.isEmpty()) {
            return List.of();
        }
        Map<String, String> references = new LinkedHashMap<>();
        safeOutputs.forEach(output -> collect(output.data(), null, references));
        if (references.isEmpty()) {
            return safeOutputs;
        }
        Map<String, WarframeReferenceResolution> resolutions =
                referenceResolver.resolveAll(references);
        List<WarframeReferenceResolution> textReplacements = resolutions.values().stream()
                .filter(WarframeReferenceResolution::resolved)
                .filter(resolution ->
                        !StrUtil.equals(resolution.reference(), resolution.displayName()))
                .sorted(Comparator.comparingInt(
                        (WarframeReferenceResolution resolution) ->
                                resolution.reference().length()).reversed())
                .toList();
        return safeOutputs.stream()
                .map(output -> enrichOutput(output, resolutions, textReplacements))
                .toList();
    }

    private static WorldStateCommandOutput enrichOutput(
            WorldStateCommandOutput output,
            Map<String, WarframeReferenceResolution> resolutions,
            List<WarframeReferenceResolution> textReplacements) {
        Map<String, Object> data = enrichMap(output.data(), resolutions);
        String text = output.text();
        for (WarframeReferenceResolution resolution : textReplacements) {
            text = text.replace(resolution.reference(), resolution.displayName());
        }
        return new WorldStateCommandOutput(text, data);
    }

    private static void collect(
            Object value, String fieldName, Map<String, String> references) {
        if (value instanceof Map<?, ?> map) {
            map.forEach((key, entry) -> collect(entry, String.valueOf(key), references));
            return;
        }
        if (value instanceof List<?> list) {
            for (Object entry : list) {
                collect(entry, fieldName, references);
            }
            return;
        }
        if (value instanceof String reference && isReferenceField(fieldName)
                && StrUtil.isNotBlank(reference)) {
            references.putIfAbsent(reference, typeHint(fieldName));
        }
    }

    private static Map<String, Object> enrichMap(
            Map<?, ?> source,
            Map<String, WarframeReferenceResolution> resolutions) {
        Map<String, Object> result = new LinkedHashMap<>();
        source.forEach((keyValue, value) -> {
            String key = String.valueOf(keyValue);
            EnrichedValue enriched = enrichValue(value, key, resolutions);
            result.put(key, enriched.value());
            if (enriched.changed()) {
                result.put(key + "Reference", enriched.original());
            }
            if (enriched.hasUnresolved()) {
                result.put(key + "ResolutionStatus", enriched.statuses());
            }
        });
        return result;
    }

    private static EnrichedValue enrichValue(
            Object value,
            String fieldName,
            Map<String, WarframeReferenceResolution> resolutions) {
        if (value instanceof Map<?, ?> map) {
            return new EnrichedValue(enrichMap(map, resolutions), value, false, false, List.of());
        }
        if (value instanceof List<?> list) {
            List<Object> values = new ArrayList<>(list.size());
            boolean changed = false;
            Set<String> statuses = new LinkedHashSet<>();
            for (Object entry : list) {
                EnrichedValue enriched = enrichValue(entry, fieldName, resolutions);
                values.add(enriched.value());
                changed |= enriched.changed();
                statuses.addAll(enriched.statuses());
            }
            return new EnrichedValue(
                    List.copyOf(values),
                    value,
                    changed,
                    !statuses.isEmpty(),
                    List.copyOf(statuses));
        }
        if (value instanceof String reference && isReferenceField(fieldName)) {
            WarframeReferenceResolution resolution = resolutions.get(reference);
            if (resolution == null) {
                return EnrichedValue.unchanged(value);
            }
            if (resolution.resolved()) {
                boolean changed = !StrUtil.equals(reference, resolution.displayName());
                return new EnrichedValue(
                        resolution.displayName(), reference, changed, false, List.of());
            }
            return new EnrichedValue(
                    reference,
                    reference,
                    false,
                    true,
                    List.of(resolution.status().name()));
        }
        return EnrichedValue.unchanged(value);
    }

    private static boolean isReferenceField(String fieldName) {
        return fieldName != null
                && REFERENCE_KEYS.contains(fieldName.toLowerCase(Locale.ROOT));
    }

    private static String typeHint(String fieldName) {
        String normalized = StrUtil.emptyIfNull(fieldName).toLowerCase(Locale.ROOT);
        return "node".equals(normalized) || "location".equals(normalized)
                ? "NODE"
                : "ITEM";
    }

    private record EnrichedValue(
            Object value,
            Object original,
            boolean changed,
            boolean hasUnresolved,
            List<String> statuses) {

        private static EnrichedValue unchanged(Object value) {
            return new EnrichedValue(value, value, false, false, List.of());
        }
    }
}
