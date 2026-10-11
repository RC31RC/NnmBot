package com.nnm.bot.warframe.service;

import com.nnm.bot.warframe.constant.WarframeReferenceResolutionStatus;
import com.nnm.bot.warframe.application.WarframeReferenceResolution;
import java.util.Objects;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.warframe.service.catalog.WarframeCommunityCatalogService;
import com.nnm.bot.warframe.service.WarframeWorldStateRuleService;
import com.nnm.bot.warframe.constant.WarframeCommunityRecordSource;
import com.nnm.bot.warframe.pojo.WarframeCommunityDataRecord;
import com.nnm.bot.warframe.pojo.WarframeCommunityStateTranslationDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityRewardPoolDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityRewardItem;
import com.nnm.bot.warframe.pojo.WarframeCommunityArbitrationEssenceDetails;
import com.nnm.bot.warframe.pojo.WarframeWorldStateDisplay;
import com.nnm.bot.warframe.plugin.AbstractWorldStateCommandPlugin.WorldStateCommandOutput;
import com.nnm.bot.warframe.constant.WarframeReferenceType;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class WarframeReferenceProjectionEnrichmentService {

    private static final Set<String> REFERENCE_KEYS = Set.of(
            "item", "reward", "rewarditems",
            "attackerreward", "defenderreward",
            "attackerrewards", "defenderrewards",
            "node", "nodes", "location", "boss", "type", "mission", "missiontype", "modifier", "syndicate",
            "faction", "upgradetype", "operationtype", "agenttype", "jobtype",
            "locationtag", "lastdiscoveredlocation", "title", "tier", "description", "challenge");

    @Resource
    private WarframeReferenceResolverService referenceResolver;

    @Resource
    private WarframeCommunityCatalogService communityCatalogService;

    @Resource
    private WarframeBountyDefinitionService bountyDefinitionService;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private WarframeWorldStateRuleService worldStateRuleService;

    public WorldStateCommandOutput enrich(WorldStateCommandOutput output) {
        return enrich(output, null);
    }

    public WorldStateCommandOutput enrich(
            WorldStateCommandOutput output, String commandCode) {
        if (output == null) {
            return null;
        }
        return enrichAll(List.of(output), commandCode).getFirst();
    }

    /**
     * 批量增强同一次 WorldState 生成的多个投影，避免每个分区重复加载静态数据索引。
     */
    public List<WorldStateCommandOutput> enrichAll(
            Collection<WorldStateCommandOutput> outputs) {
        return enrichAll(outputs, null);
    }

    /**
     * 批量增强同一次 WorldState 生成的多个投影。commandCode 只在所有输出属于同一
     * 命令时传入；监听器等混合投影调用会按输出标题推导命令名。
     */
    public List<WorldStateCommandOutput> enrichAll(
            Collection<WorldStateCommandOutput> outputs, String commandCode) {
        if (outputs == null || outputs.isEmpty()) {
            return List.of();
        }
        List<WorldStateCommandOutput> safeOutputs = outputs.stream()
                .filter(Objects::nonNull)
                .toList();
        if (safeOutputs.isEmpty()) {
            return List.of();
        }
        if (safeOutputs.stream().anyMatch(output -> "conclave".equals(commandCode(commandCode, output)))) {
            Map<String, JsonNode> rules = worldStateRuleService.sections("conclaveData");
            safeOutputs = safeOutputs.stream().map(output ->
                    "conclave".equals(commandCode(commandCode, output))
                            ? prepareConclave(output, rules) : output).toList();
        }
        boolean containsBounties = safeOutputs.stream().anyMatch(output -> containsJobs(output.data()));
        boolean containsNightwave = safeOutputs.stream()
                .anyMatch(output -> "nightwave".equals(commandCode(commandCode, output)));
        boolean containsCalendar = safeOutputs.stream()
                .anyMatch(output -> "calendar".equals(commandCode(commandCode, output)));
        List<WarframeCommunityDataRecord<WarframeCommunityStateTranslationDetails>>
                loadedTranslations = containsBounties || containsNightwave
                ? communityCatalogService.stateTranslations()
                : List.of();
        final List<WarframeCommunityDataRecord<WarframeCommunityStateTranslationDetails>>
                translations = loadedTranslations == null ? List.of() : loadedTranslations;
        final List<WarframeCommunityStateTranslationDetails> calendarRules = containsCalendar
                ? safeRules(worldStateRuleService.stateTranslations())
                : List.of();
        boolean containsArbitration = safeOutputs.stream().anyMatch(output ->
                "arbitration".equals(commandCode(commandCode, output))
                        || "arbitration-table".equals(commandCode(commandCode, output)));
        if (containsArbitration) {
            var essence = communityCatalogService.arbitrationEssences();
            safeOutputs = safeOutputs.stream().map(output -> new WorldStateCommandOutput(
                    output.text(), enrichArbitration(output.data(), essence),
                    output.unresolvedReferences())).toList();
        }
        if (containsBounties) {
            var pools = communityCatalogService.rewardPools();
            safeOutputs = safeOutputs.stream().map(output -> new WorldStateCommandOutput(
                    output.text(), prepareBounties(output.data(), translations, pools),
                    output.unresolvedReferences())).toList();
        }
        Map<String, String> nightwaveDescriptions = containsNightwave
                ? stateDescriptions(translations) : Map.of();
        Map<String, WarframeReferenceType> references = new LinkedHashMap<>();
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
        List<WarframeReferenceResolution> unresolvedReplacements = resolutions.values().stream()
                .filter(resolution -> !resolution.resolved())
                .sorted(Comparator.comparingInt(
                        (WarframeReferenceResolution resolution) ->
                                resolution.reference().length()).reversed())
                .toList();
        return safeOutputs.stream()
                .map(output -> enrichOutput(output, resolutions, textReplacements,
                        unresolvedReplacements, commandCode(commandCode, output),
                        nightwaveDescriptions, calendarRules))
                .toList();
    }

    private static List<WarframeCommunityStateTranslationDetails> safeRules(
            List<WarframeCommunityStateTranslationDetails> rules) {
        return rules == null ? List.of() : rules;
    }

    private WorldStateCommandOutput prepareConclave(
            WorldStateCommandOutput output, Map<String, JsonNode> rules) {
        Map<String, Object> data = new LinkedHashMap<>(output.data());
        if (data.get("items") instanceof List<?> items) {
            data.put("items", items.stream().map(item -> {
                if (!(item instanceof Map<?, ?> source)) return item;
                Map<String, Object> row = new LinkedHashMap<>();
                source.forEach((key, value) -> row.put(key.toString(), value));
                applyRule(row, rules.get("modes"), "mode", "mode", "value");
                applyRule(row, rules.get("categories"), "categoryKey", "category", "value");
                Object key = row.get("title");
                JsonNode challenges = rules.get("challenges");
                // 参考 utilities.conclaveChallenge 按资源路径末段查 conclaveData。
                String ruleKey = key == null ? "" : key.toString().substring(key.toString().lastIndexOf('/') + 1);
                JsonNode challenge = challenges == null ? null : challenges.get(ruleKey);
                if (challenge == null && key != null && rules.get("affectors") != null) {
                    challenge = rules.get("affectors").get(ruleKey);
                }
                if (challenge != null) {
                    for (String field : List.of("title", "description", "standing")) {
                        JsonNode value = challenge.get(field);
                        if (value != null && !value.isNull() && !value.asText().isBlank()) {
                            row.put(field, value.isNumber() ? value.numberValue() : value.asText());
                        }
                    }
                }
                return row;
            }).toList());
        }
        return new WorldStateCommandOutput(output.text(), data, output.unresolvedReferences());
    }

    private static void applyRule(Map<String, Object> row, JsonNode rules,
                                  String source, String target, String property) {
        Object key = row.get(source);
        if (rules == null || key == null) return;
        JsonNode value = rules.path(key.toString()).path(property);
        if (value.isTextual() && !value.asText().isBlank()) row.put(target, value.asText());
    }

    private Map<String, Object> enrichArbitration(
            Map<String, Object> source,
            List<WarframeCommunityDataRecord<WarframeCommunityArbitrationEssenceDetails>> records) {
        Map<String, Object> result = new LinkedHashMap<>(source);
        if (source.get("items") instanceof List<?> items) {
            result.put("items", items.stream().map(item -> {
                if (!(item instanceof Map<?, ?> map)) return item;
                Map<String, Object> row = new LinkedHashMap<>();
                map.forEach((key, value) -> row.put(String.valueOf(key), value));
                String node = String.valueOf(row.getOrDefault("node", ""));
                records.stream().map(WarframeCommunityDataRecord::details)
                        .filter(details -> sameNode(node, details.nodeName()))
                        .findFirst().ifPresent(details -> {
                            int amount = details.essenceAmount();
                            row.put("essence", amount + "（" + essenceQuality(amount) + "级）");
                        });
                return row;
            }).toList());
        }
        return result;
    }

    private static boolean sameNode(String left, String right) {
        if (StrUtil.isBlank(left) || StrUtil.isBlank(right)) return false;
        // 保留来源的全部节点键；不把不同星球的同名节点模糊合并。
        return left.equals(right);
    }

    private static String essenceQuality(int amount) {
        if (amount >= 400) return "S";
        if (amount >= 350) return "B";
        if (amount >= 300) return "C";
        return "D";
    }

    private static Map<String, String> stateDescriptions(
            List<WarframeCommunityDataRecord<WarframeCommunityStateTranslationDetails>> translations) {
        Map<String, String> descriptions = new LinkedHashMap<>();
        translations.stream()
                .filter(record -> record.details() != null)
                .filter(record -> StrUtil.isNotBlank(record.details().uniqueName()))
                .filter(record -> StrUtil.isNotBlank(record.details().description()))
                .sorted(Comparator.comparing(record ->
                        record.recordSource() != WarframeCommunityRecordSource.SELF_MAINTAINED))
                .forEach(record -> descriptions.putIfAbsent(
                        record.details().uniqueName(), record.details().description()));
        return Map.copyOf(descriptions);
    }

    private static boolean containsJobs(Object value) {
        if (value instanceof Map<?, ?> map) {
            return map.containsKey("jobs") || map.values().stream()
                    .anyMatch(WarframeReferenceProjectionEnrichmentService::containsJobs);
        }
        return value instanceof List<?> list && list.stream()
                .anyMatch(WarframeReferenceProjectionEnrichmentService::containsJobs);
    }

    /** 赏金动态参数关联数据库里的名称、说明和奖励池，再交给统一引用解析。 */
    private Map<String, Object> prepareBounties(
            Map<?, ?> source,
            List<WarframeCommunityDataRecord<WarframeCommunityStateTranslationDetails>> translations,
            List<WarframeCommunityDataRecord<WarframeCommunityRewardPoolDetails>> pools) {
        Map<String, Object> result = new LinkedHashMap<>();
        source.forEach((key, value) -> {
            Object prepared = value;
            if (value instanceof Map<?, ?> map) {
                prepared = prepareBounties(map, translations, pools);
            } else if (value instanceof List<?> list) {
                prepared = list.stream().map(entry -> entry instanceof Map<?, ?> map
                        ? prepareBounties(map, translations, pools) : entry).toList();
            }
            result.put(String.valueOf(key), prepared);
        });
        if (!result.containsKey("jobType")) {
            return result;
        }
        String jobType = String.valueOf(result.remove("jobType"));
        bountyDefinitionService.find(jobType).ifPresent(definition -> {
            // 仅补静态定义，不替换动态实例、时间、状态和奖励池引用。
            // 引用解析只遍历 Map/List；不要把包含内部词条路径的 DTO 直接交给命令。
            Map<String, Object> displayDefinition = new LinkedHashMap<>();
            if (StrUtil.isNotBlank(definition.nameKey())) {
                displayDefinition.put("title", definition.nameKey());
                Object type = result.get("type");
                if (type == null || type.toString().isBlank() || jobType.equals(type)) {
                    result.put("type", definition.nameKey());
                }
            }
            if (StrUtil.isNotBlank(definition.descriptionKey())) {
                displayDefinition.put("description", definition.descriptionKey());
                result.putIfAbsent("description", definition.descriptionKey());
            }
            displayDefinition.put("stages", definition.stages().stream()
                    .map(option -> Map.<String, Object>of(
                            "stage", option.stage(), "option", option.option(),
                            "mission", option.jobReference()))
                    .toList());
            result.put("definition", displayDefinition);
        });
        translations.stream()
                .filter(record -> jobType.equals(record.details().uniqueName()))
                .sorted(Comparator.comparing(record ->
                        record.recordSource() != WarframeCommunityRecordSource.SELF_MAINTAINED))
                .findFirst().ifPresent(record -> {
                    if (StrUtil.isNotBlank(record.details().displayName())) {
                        result.put("type", record.details().displayName());
                    }
                    if (StrUtil.isNotBlank(record.details().description())) {
                        result.put("description", record.details().description());
                    }
        });
        Object poolValue = result.remove("rewardPoolReference");
        String poolReference = poolValue == null ? "" : poolValue.toString();
        String displayType = result.get("type") == null ? null : result.get("type").toString();
        if (StrUtil.isBlank(displayType)
                && isVaultBountyPool(poolReference)) {
            result.put("type", "隔离库赏金");
        }
        if (result.get("rewards") instanceof List<?> rewards && rewards.isEmpty()) {
            var pool = pools.stream()
                    .filter(record -> poolReference.equals(record.details().poolKey())).findFirst();
            if (pool.isPresent()) {
                result.put("rewards", bountyRewards(pool.get().details().rewards()));
            } else {
                result.put("rewardNotice", "奖励池尚未收录");
            }
        }
        return result;
    }

    private static boolean isVaultBountyPool(String poolReference) {
        return poolReference.contains("VaultBounty");
    }

    private static List<Map<String, Object>> bountyRewards(List<WarframeCommunityRewardItem> items) {
        return items.stream().map(item -> {
            Map<String, Object> reward = new LinkedHashMap<>();
            reward.put("stage", item.stage());
            reward.put("itemKey", item.itemKey());
            reward.put("displayName", item.displayName());
            reward.put("count", item.count());
            reward.put("probability", item.probability());
            reward.put("rarity", item.rarity());
            return reward;
        }).toList();
    }

    private static WorldStateCommandOutput enrichOutput(
            WorldStateCommandOutput output,
            Map<String, WarframeReferenceResolution> resolutions,
            List<WarframeReferenceResolution> textReplacements,
            List<WarframeReferenceResolution> unresolvedReplacements,
            String commandCode,
            Map<String, String> nightwaveDescriptions,
            List<WarframeCommunityStateTranslationDetails> calendarRules) {
        List<WarframeWorldStateDisplay.UnresolvedReference> unresolved = new ArrayList<>(
                output.unresolvedReferences());
        Map<String, Object> data = enrichMap(
                output.data(), "", resolutions, commandCode, unresolved,
                nightwaveDescriptions, calendarRules);
        String text = output.text();
        for (WarframeReferenceResolution resolution : textReplacements) {
            text = text.replace(resolution.reference(), resolution.displayName());
        }
        for (WarframeReferenceResolution resolution : unresolvedReplacements) {
            text = text.replace(resolution.reference(), "未收录数据");
        }
        return new WorldStateCommandOutput(text, data, distinct(unresolved));
    }

    private static void collect(
            Object value,
            String fieldName,
            Map<String, WarframeReferenceType> references) {
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
                && !isCompositeRewardSummary(fieldName, reference)
                && StrUtil.isNotBlank(reference)) {
            references.putIfAbsent(reference, typeHint(fieldName));
        }
    }

    private static Map<String, Object> enrichMap(
            Map<?, ?> source,
            String path,
            Map<String, WarframeReferenceResolution> resolutions,
            String commandCode,
            List<WarframeWorldStateDisplay.UnresolvedReference> unresolved,
            Map<String, String> nightwaveDescriptions,
            List<WarframeCommunityStateTranslationDetails> calendarRules) {
        Map<String, Object> result = new LinkedHashMap<>();
        source.forEach((keyValue, value) -> {
            String key = String.valueOf(keyValue);
            String childPath = path.isBlank() ? key : path + "." + key;
            EnrichedValue enriched = enrichValue(
                    value, key, childPath, resolutions, commandCode, unresolved,
                    nightwaveDescriptions, calendarRules);
            result.put(key, enriched.value());
        });
        enrichRewardSummary(source, result, "reward", "rewardItems");
        enrichRewardSummary(source, result, "attackerReward", "attackerRewards");
        enrichRewardSummary(source, result, "defenderReward", "defenderRewards");
        if ("nightwave".equals(commandCode) && result.get("challenge") instanceof String challenge
                && (result.get("title") == null || result.get("title").toString().isBlank())) {
            result.put("title", challenge);
        }
        if ("nightwave".equals(commandCode)
                && (result.get("description") == null || result.get("description").toString().isBlank())
                && source.get("challenge") instanceof String challenge) {
            String description = nightwaveDescriptions.get(challenge);
            if (StrUtil.isNotBlank(description)) {
                // 引用解析完成后再补中文说明，避免 description 被当作待解析内部标识。
                result.put("description", description);
            }
        }
        if ("calendar".equals(commandCode)) {
            applyCalendarRule(source, result, calendarRules);
        }
        return result;
    }

    private static void applyCalendarRule(
            Map<?, ?> source,
            Map<String, Object> result,
            List<WarframeCommunityStateTranslationDetails> rules) {
        String key = firstText(source, "uniqueName", "title", "challenge");
        if (StrUtil.isBlank(key)) {
            return;
        }
        rules.stream()
                .filter(rule -> StrUtil.equals(rule.uniqueName(), key))
                .findFirst()
                .ifPresent(rule -> {
                    if (StrUtil.isNotBlank(rule.displayName())
                            && (result.get("title") == null
                            || result.get("title").toString().isBlank()
                            || StrUtil.equals(String.valueOf(result.get("title")), key))) {
                        result.put("title", rule.displayName());
                    }
                    if (StrUtil.isNotBlank(rule.description())
                            && (result.get("description") == null
                            || result.get("description").toString().isBlank()
                            || StrUtil.equals(String.valueOf(result.get("description")), key))) {
                        result.put("description", rule.description());
                    }
                });
    }

    private static String firstText(Map<?, ?> source, String... keys) {
        for (String key : keys) {
            Object value = source.get(key);
            if (value instanceof String text && StrUtil.isNotBlank(text)) {
                return text;
            }
        }
        return null;
    }

    /** 奖励摘要是物品与数量拼成的文本，不能把整句当成一个物品名称查询。 */
    private static void enrichRewardSummary(Map<?, ?> source, Map<String, Object> result,
                                            String summaryKey, String itemsKey) {
        if (!(result.get(summaryKey) instanceof String summary)) {
            return;
        }
        if (source.get(itemsKey) instanceof List<?> references
                && result.get(itemsKey) instanceof List<?> names
                && references.size() == names.size()) {
            Map<String, String> replacements = new LinkedHashMap<>();
            for (int index = 0; index < references.size(); index++) {
                Object raw = references.get(index) instanceof Map<?, ?> entry
                        ? entry.get("item") : references.get(index);
                Object localized = names.get(index) instanceof Map<?, ?> entry
                        ? entry.get("item") : names.get(index);
                if (raw instanceof String reference && !reference.isBlank()
                        && localized instanceof String name && !name.isBlank()
                        && !reference.equals(name)) {
                    replacements.put(reference, name);
                }
            }
            // 长路径优先，避免一个物品标识恰好是另一个的前缀时替换错误。
            for (String reference : replacements.keySet().stream()
                    .sorted(Comparator.comparingInt(String::length).reversed()).toList()) {
                summary = summary.replace(reference, replacements.get(reference));
            }
        }
        result.put(summaryKey, summary.replaceAll("(?i)\\b(\\d+) credits\\b", "$1 现金"));
    }

    private static EnrichedValue enrichValue(
            Object value,
            String fieldName,
            String path,
            Map<String, WarframeReferenceResolution> resolutions,
            String commandCode,
            List<WarframeWorldStateDisplay.UnresolvedReference> unresolved,
            Map<String, String> nightwaveDescriptions,
            List<WarframeCommunityStateTranslationDetails> calendarRules) {
        if (value instanceof Map<?, ?> map) {
            return new EnrichedValue(
                    enrichMap(map, path, resolutions, commandCode, unresolved,
                            nightwaveDescriptions, calendarRules),
                    value, false, false, List.of());
        }
        if (value instanceof List<?> list) {
            List<Object> values = new ArrayList<>(list.size());
            boolean changed = false;
            Set<String> statuses = new LinkedHashSet<>();
            for (int index = 0; index < list.size(); index++) {
                EnrichedValue enriched = enrichValue(
                        list.get(index), fieldName, path + "[" + index + "]", resolutions,
                        commandCode, unresolved, nightwaveDescriptions, calendarRules);
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
            if (isCompositeRewardSummary(fieldName, reference)) {
                return EnrichedValue.unchanged(value);
            }
            WarframeReferenceResolution resolution = resolutions.get(reference);
            if (resolution == null) {
                return EnrichedValue.unchanged(value);
            }
            if (resolution != null && resolution.resolved()) {
                boolean changed = !StrUtil.equals(reference, resolution.displayName());
                return new EnrichedValue(
                        resolution.displayName(), reference, changed, false, List.of());
            }
            unresolved.add(new WarframeWorldStateDisplay.UnresolvedReference(
                    commandCode,
                    path,
                    reference,
                    unresolvedStatus(resolution.status()),
                    resolution.candidates()));
            return new EnrichedValue(
                    unresolvedDisplay(fieldName),
                    reference,
                    true,
                    true,
                    List.of(resolution.status().name()));
        }
        return EnrichedValue.unchanged(value);
    }

    private static boolean isCompositeRewardSummary(String fieldName, String value) {
        if (fieldName == null || value == null) {
            return false;
        }
        String field = fieldName.toLowerCase(Locale.ROOT);
        return (field.equals("reward") || field.endsWith("reward"))
                && (value.contains("credits") || value.contains("现金")
                || value.contains(",") || value.contains("，")
                || value.matches("(?s).*\\s[x×]\\s*\\d+.*"));
    }

    private static boolean isReferenceField(String fieldName) {
        return fieldName != null
                && REFERENCE_KEYS.contains(fieldName.toLowerCase(Locale.ROOT));
    }

    private static WarframeReferenceType typeHint(String fieldName) {
        String normalized = StrUtil.emptyIfNull(fieldName).toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "node", "nodes", "location", "lastdiscoveredlocation" -> WarframeReferenceType.NODE;
            case "type", "mission", "missiontype" -> WarframeReferenceType.MISSION_TYPE;
            case "jobtype", "locationtag", "title" -> WarframeReferenceType.ITEM;
            case "modifier" -> WarframeReferenceType.MODIFIER;
            case "tier" -> WarframeReferenceType.FISSURE_TIER;
            case "boss" -> WarframeReferenceType.SORTIE_BOSS;
            case "syndicate" -> WarframeReferenceType.SYNDICATE;
            case "faction" -> WarframeReferenceType.FACTION;
            case "upgradetype" -> WarframeReferenceType.UPGRADE_TYPE;
            case "operationtype" -> WarframeReferenceType.OPERATION_TYPE;
            case "agenttype" -> WarframeReferenceType.PERSISTENT_ENEMY;
            case "challenge" -> WarframeReferenceType.ITEM;
            default -> WarframeReferenceType.ITEM;
        };
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

    private static List<WarframeWorldStateDisplay.UnresolvedReference> distinct(
            List<WarframeWorldStateDisplay.UnresolvedReference> references) {
        return references.stream().distinct().toList();
    }

    private static String commandCode(String requested, WorldStateCommandOutput output) {
        if (StrUtil.isNotBlank(requested)) {
            return requested;
        }
        String title = output.data().get("title") == null
                ? "" : String.valueOf(output.data().get("title"));
        return switch (title) {
            case "警报" -> "alerts";
            case "突击" -> "sorties";
            case "执刑官猎杀" -> "archon-hunts";
            case "仲裁", "仲裁表" -> "arbitration";
            case "裂隙" -> "fissures";
            case "钢铁裂隙" -> "steel-fissures";
            case "九重天裂隙" -> "void-storms";
            case "平原" -> "cycles";
            case "奸商" -> "void-trader";
            case "每日特惠" -> "daily-deals";
            case "入侵" -> "invasions";
            case "钢铁奖励" -> "steel-path";
            case "电波" -> "nightwave";
            case "希图斯" -> "ostrons";
            case "索拉里斯" -> "solaris";
            case "英择谛" -> "entrati";
            case "竞技场" -> "conclave";
            case "新闻" -> "news";
            case "促销" -> "flash-sales";
            case "小小黑" -> "persistent-enemies";
            case "集团任务" -> "syndicate-missions";
            case "实时日历" -> "calendar";
            default -> title;
        };
    }

    private static String unresolvedStatus(
            WarframeReferenceResolutionStatus status) {
        return status == WarframeReferenceResolutionStatus.AMBIGUOUS
                ? "有歧义" : "未解析";
    }

    private static String unresolvedDisplay(String fieldName) {
        String field = StrUtil.emptyIfNull(fieldName).toLowerCase(Locale.ROOT);
        if (field.contains("reward") || field.equals("item")) {
            return "未收录奖励";
        }
        if (field.contains("description")) {
            return "未收录说明";
        }
        if (field.contains("location") || field.equals("node") || field.equals("nodes")) {
            return "未收录地点";
        }
        if (field.contains("mission") || field.equals("type") || field.equals("jobtype")) {
            return "未收录任务";
        }
        if (field.contains("faction")) {
            return "未收录阵营";
        }
        if (field.contains("modifier")) {
            return "未收录条件";
        }
        if (field.equals("tier")) {
            return "未收录纪元";
        }
        if (field.contains("syndicate")) {
            return "未收录集团";
        }
        if (field.equals("boss")) {
            return "未收录目标";
        }
        return "未收录数据";
    }
}
