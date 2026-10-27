package com.nnm.bot.warframe.runtime.service.worldstate.impl;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeReferenceProjectionEnrichmentService;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO.UnresolvedReferenceVO;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceResolutionStatus;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceType;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.WarframeStateTranslationDetailsDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.WarframeReferenceResolutionDTO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateCommandVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateVO;
import com.nnm.bot.warframe.persistent.official.service.rewards.WarframeBountyDefinitionService;
import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeWorldStateRuleService;
import com.nnm.bot.warframe.persistent.official.service.reference.WarframeReferenceResolverService;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.rewards.WarframeRewardPoolVO;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeCommunityCatalogService;
import com.nnm.bot.warframe.persistent.official.service.rewards.WarframeRewardPoolService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 为管理端、机器人命令和蹲任务统一增强 WorldStateDTO 展示投影。
 */
@Service
@Slf4j
public class WarframeReferenceProjectionEnrichmentServiceImpl implements WarframeReferenceProjectionEnrichmentService {

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
    private WarframeCommunityCatalogService communityCatalog;

    @Resource
    private WarframeRewardPoolService rewardPoolService;

    @Resource
    private WarframeBountyDefinitionService bountyDefinitionService;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private WarframeWorldStateRuleService worldStateRuleService;

    /**
     * 结合已采纳静态资料补全运行时数据。
     *
     * @param output 需要补全或转换的输出数据。
     * @return 当前命令的标题、状态及展示条目。
     */
    public WarframeWorldStateCommandVO enrich(WarframeWorldStateCommandVO output) {
        return enrich(output, null);
    }

    /**
     * 结合已采纳静态资料补全运行时数据。
     *
     * @param output 需要补全或转换的输出数据。
     * @param commandCode 命令代码。
     * @return 当前命令的标题、状态及展示条目。
     */
    public WarframeWorldStateCommandVO enrich(
            WarframeWorldStateCommandVO output, String commandCode) {
        if (output == null) {
            return null;
        }
        return enrichAll(List.of(output), commandCode).getFirst();
    }

    /**
     * 批量收集展示引用后统一解析，并将本地化结果补入各个命令投影。
     * @param outputs 输出集合。
     * @return 当前命令的标题、状态及展示条目组成的列表。
     */
    public List<WarframeWorldStateCommandVO> enrichAll(
            Collection<WarframeWorldStateCommandVO> outputs) {
        return enrichAll(outputs, null);
    }

    /**
     * 批量收集展示引用后统一解析，并将本地化结果补入各个命令投影。
     *    命令时传入；监听器等混合投影调用会按输出标题推导命令名。
     * @param outputs 输出集合。
     * @param commandCode 命令代码。
     * @return 当前命令的标题、状态及展示条目组成的列表。
     */
    public List<WarframeWorldStateCommandVO> enrichAll(
            Collection<WarframeWorldStateCommandVO> outputs, String commandCode) {
        if (CollUtil.isEmpty(outputs)) {
            return List.of();
        }
        List<WarframeWorldStateCommandVO> safeOutputs = outputs.stream()
                .filter(Objects::nonNull)
                .toList();
        if (CollUtil.isEmpty(safeOutputs)) {
            return List.of();
        }
        boolean containsBounties = safeOutputs.stream().anyMatch(output -> containsJobs(output.data()));
        boolean containsNightwave = safeOutputs.stream()
                .anyMatch(output -> "nightwave".equals(commandCode(commandCode, output)));
        boolean containsCalendar = safeOutputs.stream()
                .anyMatch(output -> "calendar".equals(commandCode(commandCode, output)));
        List<WarframeStateTranslationDetailsDTO> translations = containsBounties || containsNightwave
                ? safeRules(worldStateRuleService.stateTranslations()) : List.of();
        final List<WarframeStateTranslationDetailsDTO> calendarRules = containsCalendar
                ? safeRules(worldStateRuleService.stateTranslations())
                : List.of();
        boolean containsArbitration = safeOutputs.stream().anyMatch(output ->
                "arbitration".equals(commandCode(commandCode, output))
                        || "arbitration-table".equals(commandCode(commandCode, output)));
        if (containsArbitration) {
            var essence = communityCatalog.arbitrationEssences();
            safeOutputs = safeOutputs.stream().map(output -> new WarframeWorldStateCommandVO(
                    output.text(), enrichArbitration(output.data(), essence),
                    output.unresolvedReferences())).toList();
        }
        if (containsBounties) {
            var pools = rewardPoolService.find("", null);
            safeOutputs = safeOutputs.stream().map(output -> new WarframeWorldStateCommandVO(
                    output.text(), prepareBounties(output.data(), translations, pools),
                    output.unresolvedReferences())).toList();
        }
        Map<String, String> nightwaveDescriptions = containsNightwave
                ? stateDescriptions(translations) : Map.of();
        Map<String, WarframeReferenceType> references = new LinkedHashMap<>();
        safeOutputs.forEach(output -> collect(output.data(), null, references));
        if (CollUtil.isEmpty(references)) {
            return safeOutputs;
        }
        Map<String, WarframeReferenceResolutionDTO> resolutions =
                referenceResolver.resolveAll(references);
        List<WarframeReferenceResolutionDTO> textReplacements = resolutions.values().stream()
                .filter(WarframeReferenceResolutionDTO::resolved)
                .filter(resolution ->
                        !StrUtil.equals(resolution.reference(), resolution.displayName()))
                .sorted(Comparator.comparingInt(
                        (WarframeReferenceResolutionDTO resolution) ->
                                resolution.reference().length()).reversed())
                .toList();
        List<WarframeReferenceResolutionDTO> unresolvedReplacements = resolutions.values().stream()
                .filter(resolution -> !resolution.resolved())
                .sorted(Comparator.comparingInt(
                        (WarframeReferenceResolutionDTO resolution) ->
                                resolution.reference().length()).reversed())
                .toList();
        return safeOutputs.stream()
                .map(output -> enrichOutput(output, resolutions, textReplacements,
                        unresolvedReplacements, commandCode(commandCode, output),
                        nightwaveDescriptions, calendarRules))
                .toList();
    }

    /**
     * 读取本次本地化使用的状态规则，缺省规则按空集合处理。
     *
     * @param rules 规则集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<WarframeStateTranslationDetailsDTO> safeRules(
            List<WarframeStateTranslationDetailsDTO> rules) {
        return rules == null ? List.of() : rules;
    }

    /**
     * 按节点匹配社区仲裁精华资料，并将品质和数量补入仲裁展示字段。
     *
     * @param source 已经组装的原始展示字段映射。
     * @param records 待批量处理的业务记录。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private Map<String, Object> enrichArbitration(
            Map<String, Object> source,
            List<WarframeCommunityRecordDTO<WarframeCommunityRecordDTO.EssenceDTO>> records) {
        Map<String, Object> result = new LinkedHashMap<>(source);
        if (source.get("items") instanceof List<?> items) {
            result.put("items", items.stream().map(item -> {
                if (!(item instanceof Map<?, ?> map)) return item;
                Map<String, Object> row = new LinkedHashMap<>();
                map.forEach((key, value) -> row.put(String.valueOf(key), value));
                String node = String.valueOf(row.getOrDefault("node", ""));
                records.stream().map(WarframeCommunityRecordDTO::details)
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

    /**
     * 判断两条数据是否指向同一游戏节点。
     *
     * @param left 参与比较的左侧数据。
     * @param right 参与比较的右侧数据。
     * @return 满足两条数据是否指向同一游戏节点时为真，否则为假。
     */
    private static boolean sameNode(String left, String right) {
        if (StrUtil.isBlank(left) || StrUtil.isBlank(right)) return false;
        // 保留来源的全部节点键；不把不同星球的同名节点模糊合并。
        return left.equals(right);
    }

    /**
     * 按仲裁精华数量划分展示用的品质等级。
     *
     * @param amount 数量。
     * @return 精华质量对应的文本。
     */
    private static String essenceQuality(int amount) {
        if (amount >= 400) return "S";
        if (amount >= 350) return "B";
        if (amount >= 300) return "C";
        return "D";
    }

    /**
     * 将已采纳状态译名组织为描述查找表。
     *
     * @param translations 译名集合。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private static Map<String, String> stateDescriptions(
            List<WarframeStateTranslationDetailsDTO> translations) {
        Map<String, String> descriptions = new LinkedHashMap<>();
        translations.stream()
                .filter(record -> StrUtil.isNotBlank(record.uniqueName()))
                .filter(record -> StrUtil.isNotBlank(record.description()))
                .forEach(record -> descriptions.putIfAbsent(record.uniqueName(), record.description()));
        return Map.copyOf(descriptions);
    }

    /**
     * 判断当前展示条目是否包含可展示的赏金任务。
     *
     * @param value 待处理的字段值。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    private static boolean containsJobs(Object value) {
        if (value instanceof Map<?, ?> map) {
            return map.containsKey("jobs") || map.values().stream()
                    .anyMatch(WarframeReferenceProjectionEnrichmentServiceImpl::containsJobs);
        }
        return value instanceof List<?> list && list.stream()
                .anyMatch(WarframeReferenceProjectionEnrichmentServiceImpl::containsJobs);
    }

    /**
     * 赏金动态参数关联数据库里的名称、说明和奖励池，再交给统一引用解析。
     *
     * @param source 已经组装的原始展示字段映射。
     * @param translations 译名集合。
     * @param pools 奖池集合。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private Map<String, Object> prepareBounties(
            Map<?, ?> source,
            List<WarframeStateTranslationDetailsDTO> translations,
            List<WarframeRewardPoolVO.EntryVO> pools) {
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
                if (type == null || StrUtil.isBlank(type.toString()) || jobType.equals(type)) {
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
        translations.stream().filter(record -> jobType.equals(record.uniqueName()))
                .findFirst().ifPresent(record -> {
                    if (StrUtil.isNotBlank(record.displayName())) result.put("type", record.displayName());
                    if (StrUtil.isNotBlank(record.description())) result.put("description", record.description());
                });
        Object poolValue = result.remove("rewardPoolReference");
        String poolReference = poolValue == null ? "" : poolValue.toString();
        String displayType = result.get("type") == null ? null : result.get("type").toString();
        if (StrUtil.isBlank(displayType)
                && isVaultBountyPool(poolReference)) {
            result.put("type", "隔离库赏金");
        }
        if (result.get("rewards") instanceof List<?> rewards && CollUtil.isEmpty(rewards)) {
            var matching = pools.stream().filter(record -> poolReference.equals(record.poolKey())).toList();
            if (CollUtil.isNotEmpty(matching)) {
                result.put("rewards", bountyRewards(matching));
            } else {
                result.put("rewardNotice", "奖励池尚未收录");
            }
        }
        return result;
    }

    /**
     * 判断当前奖励池是否属于宝库赏金。
     *
     * @param poolReference 奖池引用。
     * @return 满足当前奖励池是否属于宝库赏金时为真，否则为假。
     */
    private static boolean isVaultBountyPool(String poolReference) {
        return poolReference.contains("VaultBounty");
    }

    /**
     * 从奖励池展示条目中提取本次赏金的奖励说明。
     *
     * @param items 当前物品或成员列表。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<Map<String, Object>> bountyRewards(List<WarframeRewardPoolVO.EntryVO> items) {
        return items.stream().map(item -> {
            Map<String, Object> reward = new LinkedHashMap<>();
            reward.put("stage", item.stage());
            reward.put("itemKey", item.itemKey());
            reward.put("displayName", item.itemName());
            reward.put("count", item.count());
            reward.put("probability", item.probability());
            reward.put("rarity", item.rarity());
            return reward;
        }).toList();
    }

    /**
     * 将本轮批量解析结果、状态说明和命令规则补入单个展示载荷。
     * @param output 需要补全或转换的输出数据。
     * @param resolutions 解析结果。
     * @param textReplacements 文本替换数据集合。
     * @param unresolvedReplacements 未解析替换数据集合。
     * @param commandCode 命令代码。
     * @param nightwaveDescriptions 夜波说明。
     * @param calendarRules 日历规则集合。
     * @return 当前命令的标题、状态及展示条目。
     */
    private static WarframeWorldStateCommandVO enrichOutput(
            WarframeWorldStateCommandVO output,
            Map<String, WarframeReferenceResolutionDTO> resolutions,
            List<WarframeReferenceResolutionDTO> textReplacements,
            List<WarframeReferenceResolutionDTO> unresolvedReplacements,
            String commandCode,
            Map<String, String> nightwaveDescriptions,
            List<WarframeStateTranslationDetailsDTO> calendarRules) {
        List<UnresolvedReferenceVO> unresolved = new ArrayList<>(
                output.unresolvedReferences());
        Map<String, Object> data = enrichMap(
                output.data(), "", resolutions, commandCode, unresolved,
                nightwaveDescriptions, calendarRules);
        String text = output.text();
        for (WarframeReferenceResolutionDTO resolution : textReplacements) {
            text = text.replace(resolution.reference(), resolution.displayName());
        }
        for (WarframeReferenceResolutionDTO resolution : unresolvedReplacements) {
            text = text.replace(resolution.reference(), "未收录数据");
        }
        return new WarframeWorldStateCommandVO(text, data, distinct(unresolved));
    }

    /**
     * 收集当前查询或关系树需要的业务数据。
     *
     * @param value 待处理的字段值。
     * @param fieldName 字段名称。
     * @param references 引用。
     */
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

    /**
     * 递归补全已经组装的展示字段，保留原始引用与未解析状态。
     * @param source 已经组装的原始展示字段映射。
     * @param path 当前文件或业务资源的路径。
     * @param resolutions 解析结果。
     * @param commandCode 命令代码。
     * @param unresolved 未解析集合。
     * @param nightwaveDescriptions 夜波说明。
     * @param calendarRules 日历规则集合。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private static Map<String, Object> enrichMap(
            Map<?, ?> source,
            String path,
            Map<String, WarframeReferenceResolutionDTO> resolutions,
            String commandCode,
            List<UnresolvedReferenceVO> unresolved,
            Map<String, String> nightwaveDescriptions,
            List<WarframeStateTranslationDetailsDTO> calendarRules) {
        Map<String, Object> result = new LinkedHashMap<>();
        source.forEach((keyValue, value) -> {
            String key = String.valueOf(keyValue);
            String childPath = StrUtil.isBlank(path) ? key : path + "." + key;
            EnrichedValue enriched = enrichValue(
                    value, key, childPath, resolutions, commandCode, unresolved,
                    nightwaveDescriptions, calendarRules);
            result.put(key, enriched.value());
        });
        enrichRewardSummary(source, result, "reward", "rewardItems");
        enrichRewardSummary(source, result, "attackerReward", "attackerRewards");
        enrichRewardSummary(source, result, "defenderReward", "defenderRewards");
        if ("nightwave".equals(commandCode) && result.get("challenge") instanceof String challenge
                && (result.get("title") == null || StrUtil.isBlank(result.get("title").toString()))) {
            result.put("title", challenge);
        }
        if ("nightwave".equals(commandCode)
                && (result.get("description") == null || StrUtil.isBlank(result.get("description").toString()))
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

    /**
     * 处理采纳日历规则。
     *
     * @param source 已经组装的原始展示字段映射。
     * @param result 当前正在组装的业务结果。
     * @param rules 规则集合。
     */
    private static void applyCalendarRule(
            Map<?, ?> source,
            Map<String, Object> result,
            List<WarframeStateTranslationDetailsDTO> rules) {
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
                            || StrUtil.isBlank(result.get("title").toString())
                            || StrUtil.equals(String.valueOf(result.get("title")), key))) {
                        result.put("title", rule.displayName());
                    }
                    if (StrUtil.isNotBlank(rule.description())
                            && (result.get("description") == null
                            || StrUtil.isBlank(result.get("description").toString())
                            || StrUtil.equals(String.valueOf(result.get("description")), key))) {
                        result.put("description", rule.description());
                    }
                });
    }

    /**
     * 按约定字段顺序从展示映射取得首个有效文本。
     * @param source 已经组装的原始展示字段映射。
     * @param keys 待处理的查找键集合。
     * @return 首个文本对应的文本。
     */
    private static String firstText(Map<?, ?> source, String... keys) {
        for (String key : keys) {
            Object value = source.get(key);
            if (value instanceof String text && StrUtil.isNotBlank(text)) {
                return text;
            }
        }
        return null;
    }

    /**
     * 奖励摘要是物品与数量拼成的文本，不能把整句当成一个物品名称查询。
     *
     * @param source 已经组装的原始展示字段映射。
     * @param result 当前正在组装的业务结果。
     * @param summaryKey 摘要键。
     * @param itemsKey 物品键。
     */
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
                if (raw instanceof String reference && StrUtil.isNotBlank(reference)
                        && localized instanceof String name && StrUtil.isNotBlank(name)
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

    /**
     * 结合明确的静态引用补全一个展示值。
     *
     * @param value 待处理的字段值。
     * @param fieldName 字段名称。
     * @param path 当前文件或业务资源的路径。
     * @param resolutions 解析结果。
     * @param commandCode 命令代码。
     * @param unresolved 未解析集合。
     * @param nightwaveDescriptions 夜波说明。
     * @param calendarRules 日历规则集合。
     * @return 补全后值对象。
     */
    private static EnrichedValue enrichValue(
            Object value,
            String fieldName,
            String path,
            Map<String, WarframeReferenceResolutionDTO> resolutions,
            String commandCode,
            List<UnresolvedReferenceVO> unresolved,
            Map<String, String> nightwaveDescriptions,
            List<WarframeStateTranslationDetailsDTO> calendarRules) {
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
                    CollUtil.isNotEmpty(statuses),
                    List.copyOf(statuses));
        }
        if (value instanceof String reference && isReferenceField(fieldName)) {
            if (isCompositeRewardSummary(fieldName, reference)) {
                return EnrichedValue.unchanged(value);
            }
            WarframeReferenceResolutionDTO resolution = resolutions.get(reference);
            if (resolution == null) {
                return EnrichedValue.unchanged(value);
            }
            if (resolution != null && resolution.resolved()) {
                boolean changed = !StrUtil.equals(reference, resolution.displayName());
                return new EnrichedValue(
                        resolution.displayName(), reference, changed, false, List.of());
            }
            unresolved.add(new WarframeWorldStateVO.UnresolvedReferenceVO(
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

    /**
     * 判断输入文本是否为包含多个奖励的组合摘要。
     *
     * @param fieldName 字段名称。
     * @param value 待校验或规范化的文本。
     * @return 满足输入文本是否为包含多个奖励的组合摘要时为真，否则为假。
     */
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

    /**
     * 判断字段是否承载需要解析的业务引用。
     *
     * @param fieldName 字段名称。
     * @return 满足字段是否承载需要解析的业务引用时为真，否则为假。
     */
    private static boolean isReferenceField(String fieldName) {
        return fieldName != null
                && REFERENCE_KEYS.contains(fieldName.toLowerCase(Locale.ROOT));
    }

    /**
     * 根据展示字段的业务用途选择静态引用解析类型。
     * @param fieldName 字段名称。
     * @return 游戏资料引用类型对象。
     */
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

        /**
         * 构造内容没有变化时的处理结果。
         *
         * @param value 待处理的字段值。
         * @return 补全后值对象。
         */
        private static EnrichedValue unchanged(Object value) {
            return new EnrichedValue(value, value, false, false, List.of());
        }
    }

    /**
     * 按业务身份去重并保留稳定顺序。
     *
     * @param references 引用集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<WarframeWorldStateVO.UnresolvedReferenceVO> distinct(
            List<UnresolvedReferenceVO> references) {
        return references.stream().distinct().toList();
    }

    /**
     * 优先使用显式命令代码，否则根据当前展示标题确定匹配的规则代码。
     * @param requested 请求的。
     * @param output 需要补全或转换的输出数据。
     * @return 用于匹配官方状态规则的命令代码。
     */
    private static String commandCode(String requested, WarframeWorldStateCommandVO output) {
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

    /**
     * 将未解析或歧义状态转换为中文展示提示。
     * @param status 状态。
     * @return “有歧义”或“未解析”等明确状态提示。
     */
    private static String unresolvedStatus(
            WarframeReferenceResolutionStatus status) {
        return status == WarframeReferenceResolutionStatus.AMBIGUOUS
                ? "有歧义" : "未解析";
    }

    /**
     * 按未解析引用所属字段生成中文占位文本。
     * @param fieldName 字段名称。
     * @return 符合当前引用业务含义的中文占位文本。
     */
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
