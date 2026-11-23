package com.nnm.bot.warframe.runtime.service.worldstate.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.persistent.curated.constant.WarframeCommunityMappingScope;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeCommunityCatalogService;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceResolutionStatus;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceType;
import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.WarframeReferenceResolutionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.WarframeStateTranslationDetailsDTO;
import com.nnm.bot.warframe.persistent.official.pojo.query.reference.WarframeReferenceQuery;
import com.nnm.bot.warframe.persistent.official.service.reference.WarframeReferenceResolverService;
import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeWorldStateRuleService;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeRewardSummaryFormatter;
import com.nnm.bot.warframe.runtime.constant.WarframeWorldStateResolutionStatus;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateCommandVO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WarframeWorldStateUnresolvedReferenceDTO;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeReferenceProjectionEnrichmentService;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeWorldStateRecordEnrichmentService;
import com.nnm.bot.warframe.normalization.WarframeNameNormalizer;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 为管理端、机器人命令和蹲任务统一增强 WorldStateDTO 展示投影。
 */
@Service
@Slf4j
public class WarframeReferenceProjectionEnrichmentServiceImpl implements WarframeReferenceProjectionEnrichmentService {

    private static final Set<String> REFERENCE_KEYS = Set.of("item", "reward", "rewarditems", "attackerreward",
        "defenderreward", "attackerrewards", "defenderrewards", "node", "nodes", "location", "boss", "type", "mission",
        "missiontype", "modifier", "syndicate", "faction", "upgradetype", "operationtype", "agenttype", "jobtype",
        "locationtag", "lastdiscoveredlocation", "mode", "title", "tier", "description", "challenge", "risks",
        "personalmodifiers", "deviation", "attackermissions", "defendermissions", "attackerfaction", "defenderfaction");
    private static final Set<String> TEXT_REFERENCE_KEYS =
        Set.of("description", "challengedescription", "rewardnotice");
    private static final String LANGUAGE_KEY_PREFIX = "/Lotus/Language/";
    private static final Pattern INTERNAL_REFERENCE_CODE =
        Pattern.compile("(?:MT_|SORTIE_|DT_|CT_|VoidT|OSTRONS_|SOLARIS_|ENTRATI_)[A-Za-z0-9_]+");
    private static final Set<String> RESEARCH_CODES = Set.of(WarframeCommandCodes.ARCHIMEDEAS,
        WarframeCommandCodes.DEEP_ARCHIMEDEAS, WarframeCommandCodes.HEX_ARCHIMEDEAS);

    @Resource
    private WarframeReferenceResolverService referenceResolver;

    @Resource
    private WarframeCommunityCatalogService communityCatalog;

    /** 按当轮引用关联赏金静态定义与经核对的奖励阶段。 */
    @Resource
    private WarframeOpenWorldBountyProjection bountyProjection;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private WarframeWorldStateRuleService worldStateRuleService;

    /** 为单条科研命令补齐与管理端相同的精确活动定义。 */
    @Resource
    private WarframeWorldStateRecordEnrichmentService recordEnricher;

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
    public WarframeWorldStateCommandVO enrich(WarframeWorldStateCommandVO output, String commandCode) {
        if (output == null) {
            return null;
        }
        String code = commandCode(commandCode, output);
        var displays = enrichAll(List.of(output), code);
        return RESEARCH_CODES.contains(code) || WarframeCommandCodes.CIRCUIT_HARD.equals(code)
            || WarframeCommandCodes.SYNDICATE_MISSIONS.equals(code) || WarframeCommandCodes.DESCENTS.equals(code)
            || WarframeCommandCodes.SENTIENT_OUTPOSTS.equals(code) || WarframeCommandCodes.CALENDAR.equals(code)
            || WarframeOpenWorldBountyProjection.hasJobs(output)
                ? recordEnricher.enrich(List.of(output), displays).getFirst() : displays.getFirst();
    }

    /**
     * 批量收集展示引用后统一解析，并将本地化结果补入各个命令投影。
     *
     * @param outputs 输出集合。
     * @return 当前命令的标题、状态及展示条目组成的列表。
     */
    public List<WarframeWorldStateCommandVO> enrichAll(Collection<WarframeWorldStateCommandVO> outputs) {
        return enrichAll(outputs, null);
    }

    /**
     * 批量收集展示引用后统一解析，并将本地化结果补入各个命令投影。 命令时传入；监听器等混合投影调用会按输出标题推导命令名。
     *
     * @param outputs 输出集合。
     * @param commandCode 命令代码。
     * @return 当前命令的标题、状态及展示条目组成的列表。
     */
    public List<WarframeWorldStateCommandVO> enrichAll(Collection<WarframeWorldStateCommandVO> outputs,
        String commandCode) {
        if (CollUtil.isEmpty(outputs)) {
            return List.of();
        }
        List<WarframeWorldStateCommandVO> safeOutputs = outputs.stream().filter(Objects::nonNull).toList();
        if (CollUtil.isEmpty(safeOutputs)) {
            return List.of();
        }
        if (safeOutputs.stream()
            .anyMatch(output -> WarframeCommandCodes.CIRCUIT_HARD.equals(commandCode(commandCode, output)))) {
            var mappings = communityCatalog.mappingTargets();
            safeOutputs = safeOutputs.stream()
                .map(output -> WarframeCommandCodes.CIRCUIT_HARD.equals(commandCode(commandCode, output))
                    ? circuitIdentities(output, mappings) : output)
                .toList();
        }
        boolean containsBounties = safeOutputs.stream().anyMatch(WarframeOpenWorldBountyProjection::hasJobs);
        boolean containsNightwave =
            safeOutputs.stream().anyMatch(output -> "nightwave".equals(commandCode(commandCode, output)));
        boolean containsCalendar = safeOutputs.stream()
            .anyMatch(output -> WarframeCommandCodes.CALENDAR.equals(commandCode(commandCode, output)));
        boolean containsResearch =
            safeOutputs.stream().anyMatch(output -> RESEARCH_CODES.contains(commandCode(commandCode, output)));
        Set<WarframeReferenceQuery> ruleReferences = new LinkedHashSet<>();
        if (containsBounties || containsNightwave || containsCalendar || containsResearch)
            safeOutputs.forEach(output -> collect(output.data(), null, ruleReferences));
        List<WarframeStateTranslationDetailsDTO> translations =
            ruleReferences.isEmpty() ? List.of() : safeRules(worldStateRuleService
                .findTranslations(ruleReferences.stream().map(WarframeReferenceQuery::reference).distinct().toList()));
        final List<WarframeStateTranslationDetailsDTO> calendarRules = containsCalendar ? translations : List.of();
        boolean containsArbitration =
            safeOutputs.stream().anyMatch(output -> "arbitration".equals(commandCode(commandCode, output))
                || "arbitration-table".equals(commandCode(commandCode, output)));
        if (containsArbitration) {
            var essence = communityCatalog.arbitrationEssences();
            safeOutputs = safeOutputs.stream().map(output -> new WarframeWorldStateCommandVO(output.text(),
                enrichArbitration(output.data(), essence), output.unresolvedReferences())).toList();
        }
        if (containsBounties)
            safeOutputs = bountyProjection.enrich(safeOutputs, translations);
        Map<String, String> nightwaveDescriptions = containsNightwave ? stateDescriptions(translations) : Map.of();
        Map<String, String> researchDescriptions = containsResearch ? stateDescriptions(translations) : Map.of();
        Set<WarframeReferenceQuery> references = new LinkedHashSet<>();
        safeOutputs.forEach(output -> collect(output.data(), null, references));
        if (CollUtil.isEmpty(references)) {
            return safeOutputs;
        }
        var resolutions = referenceResolver.resolveAll(references);
        return safeOutputs.stream().map(output -> {
            String code = commandCode(commandCode, output);
            var scoped = scopedResolutions(output, code, resolutions);
            return enrichOutput(output, scoped, textReplacements(scoped.values()), code, nightwaveDescriptions,
                calendarRules, researchDescriptions);
        }).toList();
    }

    private static WarframeWorldStateCommandVO circuitIdentities(WarframeWorldStateCommandVO output,
        Map<WarframeCommunityMappingScope, Map<String, List<String>>> mappings) {
        if (mappings.isEmpty() || !(output.data().get("items") instanceof List<?> items))
            return output;
        Map<String, Object> data = new LinkedHashMap<>(output.data());
        data.put("items", items.stream().map(value -> {
            if (!(value instanceof Map<?, ?> row) || !(row.get("item") instanceof String name) || name.startsWith("/"))
                return value;
            var target = WarframeCommunityMappingScope.CIRCUIT.target(mappings, name)
                .filter(path -> path.startsWith("/Lotus/Weapons/"));
            if (target.isEmpty())
                return value;
            Map<String, Object> item = stringKeys(row);
            item.put("item", target.get());
            item.put("itemKey", target.get());
            return item;
        }).toList());
        return new WarframeWorldStateCommandVO(output.text(), data, output.unresolvedReferences());
    }

    /**
     * 只对当前科研或赏金投影补充整词译名，保持官方结果、歧义及其他命令互不影响。
     *
     * @param output 当前展示投影。
     * @param code 当前命令代码。
     * @param official 已完成的官方及自维护业务引用解析。
     * @return 当前投影独有的解析结果。
     */
    private Map<WarframeReferenceQuery, WarframeReferenceResolutionDTO> scopedResolutions(
        WarframeWorldStateCommandVO output, String code,
        Map<WarframeReferenceQuery, WarframeReferenceResolutionDTO> official) {
        Set<WarframeReferenceQuery> references = new LinkedHashSet<>();
        collect(output.data(), null, references);
        Map<WarframeReferenceQuery, WarframeReferenceResolutionDTO> result = new LinkedHashMap<>();
        for (var query : references) {
            var resolution = official.get(query);
            if (resolution != null)
                result.put(query, resolution);
        }
        boolean research = RESEARCH_CODES.contains(code);
        boolean sortie = WarframeCommandCodes.SORTIES.equals(code);
        if (!research && !sortie && !WarframeOpenWorldBountyProjection.hasJobs(output))
            return result;
        var group = research ? WarframeCommunityMappingScope.ARCHIMEDEA
            : sortie ? WarframeCommunityMappingScope.SORTIE_MODIFIER : WarframeCommunityMappingScope.BOUNTY_STAGE;
        var terms = group.targets(communityCatalog.mappingTargets());
        if (CollUtil.isEmpty(terms))
            return result;
        references.forEach(query -> {
            String key = query.reference();
            var previous = result.get(query);
            if (((research || sortie) && query.type() != WarframeReferenceType.MODIFIER)
                || (previous != null && previous.resolved()))
                return;
            var candidates = terms.getOrDefault(group.normalize(key), List.of());
            if (candidates.isEmpty() && group == WarframeCommunityMappingScope.BOUNTY_STAGE
                && terms.values().stream().anyMatch(values -> values.contains(key)))
                candidates = List.of(key);
            if (candidates.isEmpty())
                return;
            var status = candidates.size() == 1 ? WarframeReferenceResolutionStatus.RESOLVED
                : WarframeReferenceResolutionStatus.AMBIGUOUS;
            result.put(query,
                new WarframeReferenceResolutionDTO(key, candidates.size() == 1 ? candidates.getFirst() : key, key,
                    "community:" + group.code(), status, candidates));
        });
        return result;
    }

    /** 摘要文本没有字段类型，只有各字段解析到同一名称时才替换对应原文。 */
    private static List<WarframeReferenceResolutionDTO>
        textReplacements(Collection<WarframeReferenceResolutionDTO> resolutions) {
        return resolutions.stream().collect(Collectors.groupingBy(WarframeReferenceResolutionDTO::reference)).values()
            .stream()
            .filter(matches -> matches.stream().allMatch(
                match -> match.resolved() && StrUtil.equals(match.displayName(), matches.getFirst().displayName())))
            .map(List::getFirst).filter(match -> !StrUtil.equals(match.reference(), match.displayName()))
            .sorted(Comparator.comparingInt((WarframeReferenceResolutionDTO match) -> match.reference().length())
                .reversed().thenComparing(WarframeReferenceResolutionDTO::reference))
            .toList();
    }

    /**
     * 读取本次本地化使用的状态规则，缺省规则按空集合处理。
     *
     * @param rules 规则集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<WarframeStateTranslationDetailsDTO> safeRules(List<WarframeStateTranslationDetailsDTO> rules) {
        return rules == null ? List.of() : rules;
    }

    /**
     * 按节点匹配社区仲裁精华资料，并将品质和数量补入仲裁展示字段。
     *
     * @param source 已经组装的原始展示字段映射。
     * @param records 待批量处理的业务记录。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private Map<String, Object> enrichArbitration(Map<String, Object> source,
        List<WarframeCommunityRecordDTO<WarframeCommunityRecordDTO.EssenceDTO>> records) {
        Map<String, Object> result = new LinkedHashMap<>(source);
        if (source.get("items") instanceof List<?> items) {
            result.put("items", items.stream().map(item -> {
                if (!(item instanceof Map<?, ?> map))
                    return item;
                Map<String, Object> row = new LinkedHashMap<>();
                map.forEach((key, value) -> row.put(String.valueOf(key), value));
                String node = String.valueOf(row.getOrDefault("node", ""));
                records.stream()
                    .filter(record -> sameNode(node,
                        record.details().nodeKey() == null ? record.recordKey() : record.details().nodeKey()))
                    .map(WarframeCommunityRecordDTO::details).findFirst().ifPresent(details -> {
                        Integer amount = details.essenceAmount();
                        String grade =
                            details.efficiencyGrade() == null ? "" : "（" + details.efficiencyGrade().label() + "）";
                        if (amount != null) {
                            row.put("essence", amount + grade);
                            row.put("essenceAmount", amount);
                        }
                        if (details.efficiencyGrade() != null)
                            row.put("efficiencyGrade", details.efficiencyGrade().code());
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
        if (CharSequenceUtil.isBlank(left) || CharSequenceUtil.isBlank(right))
            return false;
        // 保留来源的全部节点键；不把不同星球的同名节点模糊合并。
        return left.equals(right);
    }

    /**
     * 将已采纳状态译名组织为描述查找表。
     *
     * @param translations 译名集合。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private static Map<String, String> stateDescriptions(List<WarframeStateTranslationDetailsDTO> translations) {
        Map<String, String> descriptions = new LinkedHashMap<>();
        translations.stream().filter(record -> CharSequenceUtil.isNotBlank(record.uniqueName()))
            .filter(record -> CharSequenceUtil.isNotBlank(record.description()))
            .forEach(record -> descriptions.putIfAbsent(record.uniqueName(), record.description()));
        return Map.copyOf(descriptions);
    }

    /**
     * 将本轮批量解析结果、状态说明和命令规则补入单个展示载荷。
     *
     * @param output 需要补全或转换的输出数据。
     * @param resolutions 解析结果。
     * @param textReplacements 文本替换数据集合。
     * @param commandCode 命令代码。
     * @param nightwaveDescriptions 夜波说明。
     * @param calendarRules 日历规则集合。
     * @param researchDescriptions 科研相关已采纳规则的说明。
     * @return 当前命令的标题、状态及展示条目。
     */
    private static WarframeWorldStateCommandVO enrichOutput(WarframeWorldStateCommandVO output,
        Map<WarframeReferenceQuery, WarframeReferenceResolutionDTO> resolutions,
        List<WarframeReferenceResolutionDTO> textReplacements, String commandCode,
        Map<String, String> nightwaveDescriptions, List<WarframeStateTranslationDetailsDTO> calendarRules,
        Map<String, String> researchDescriptions) {
        List<WarframeWorldStateUnresolvedReferenceDTO> unresolved = new ArrayList<>(output.unresolvedReferences());
        Map<String, Object> data =
            enrichMap(output.data(), "", resolutions, commandCode, unresolved, nightwaveDescriptions, calendarRules);
        if (RESEARCH_CODES.contains(commandCode) && !researchDescriptions.isEmpty()) {
            data = researchDescriptions(output.data(), data, researchDescriptions);
        }
        String text = output.text();
        for (WarframeReferenceResolutionDTO resolution : textReplacements) {
            text = text.replace(resolution.reference(), resolution.displayName());
        }
        return new WarframeWorldStateCommandVO(text, data, distinct(unresolved));
    }

    /**
     * 按原始科研条件附加已采纳的任务说明；展示名已翻译时仍按原始键匹配。
     *
     * @param original 翻译前的科研字段。
     * @param display 已翻译的展示字段。
     * @param descriptions 由官方与自维护数据读取的任务说明。
     * @return 保留原有字段、附带说明的展示字段。
     */
    private static Map<String, Object> researchDescriptions(Map<String, Object> original, Map<String, Object> display,
        Map<String, String> descriptions) {
        Map<String, Object> result = new LinkedHashMap<>();
        display.forEach((key, value) -> {
            Object raw = original.get(key);
            if (raw instanceof Map<?, ?> source && value instanceof Map<?, ?> child) {
                result.put(key, researchDescriptions(stringKeys(source), stringKeys(child), descriptions));
            } else if (raw instanceof List<?> originals && value instanceof List<?> displays) {
                List<Object> enriched = new ArrayList<>(displays.size());
                for (int index = 0; index < displays.size(); index++) {
                    Object entry = displays.get(index);
                    Object source = index < originals.size() ? originals.get(index) : null;
                    enriched.add(source instanceof Map<?, ?> rawRow && entry instanceof Map<?, ?> displayRow
                        ? researchDescriptions(stringKeys(rawRow), stringKeys(displayRow), descriptions) : entry);
                }
                result.put(key, enriched);
            } else {
                result.put(key, value);
            }
        });
        for (String key : List.of("missionType", "deviation")) {
            if (original.get(key) instanceof String reference) {
                String description = descriptions.get(reference);
                if (CharSequenceUtil.isNotBlank(description) && (result.get("description") == null
                    || CharSequenceUtil.isBlank(String.valueOf(result.get("description"))))) {
                    result.put("description", description);
                }
            }
        }
        for (String key : List.of("risks", "personalModifiers")) {
            if (original.get(key) instanceof List<?> entries) {
                Map<String, String> explanations = new LinkedHashMap<>();
                entries.stream().filter(String.class::isInstance).map(String.class::cast).forEach(reference -> {
                    if (CharSequenceUtil.isNotBlank(descriptions.get(reference))) {
                        explanations.put(reference, descriptions.get(reference));
                    }
                });
                if (!explanations.isEmpty()) {
                    result.put(key + "Descriptions", explanations);
                }
            }
        }
        return result;
    }

    /**
     * 将嵌套的展示字段转换为文本键供规则补全读取。
     *
     * @param source 已解析的映射。
     * @return 保留原值的字段映射。
     */
    private static Map<String, Object> stringKeys(Map<?, ?> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        source.forEach((key, value) -> result.put(String.valueOf(key), value));
        return result;
    }

    /**
     * 收集当前查询或关系树需要的业务数据。
     *
     * @param value 待处理的字段值。
     * @param fieldName 字段名称。
     * @param references 引用。
     */
    private static void collect(Object value, String fieldName, Set<WarframeReferenceQuery> references) {
        if (value instanceof Map<?, ?> map) {
            map.forEach((key, entry) -> {
                String field = String.valueOf(key);
                if (!hasResolvedReferenceKey(map, field, entry))
                    collect(entry, field, references);
            });
            return;
        }
        if (value instanceof List<?> list) {
            for (Object entry : list) {
                collect(entry, fieldName, references);
            }
            return;
        }
        if (value instanceof String reference && isResolvableReference(fieldName, reference)
            && !isCompositeRewardSummary(fieldName, reference)) {
            references.add(new WarframeReferenceQuery(reference, typeHint(fieldName, reference)));
        }
    }

    /**
     * 递归补全已经组装的展示字段，保留原始引用与未解析状态。
     *
     * @param source 已经组装的原始展示字段映射。
     * @param path 当前文件或业务资源的路径。
     * @param resolutions 解析结果。
     * @param commandCode 命令代码。
     * @param unresolved 未解析集合。
     * @param nightwaveDescriptions 夜波说明。
     * @param calendarRules 日历规则集合。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private static Map<String, Object> enrichMap(Map<?, ?> source, String path,
        Map<WarframeReferenceQuery, WarframeReferenceResolutionDTO> resolutions, String commandCode,
        List<WarframeWorldStateUnresolvedReferenceDTO> unresolved, Map<String, String> nightwaveDescriptions,
        List<WarframeStateTranslationDetailsDTO> calendarRules) {
        Map<String, Object> result = new LinkedHashMap<>();
        source.forEach((keyValue, value) -> {
            String key = String.valueOf(keyValue);
            String childPath = CharSequenceUtil.isBlank(path) ? key : path + "." + key;
            if (hasResolvedReferenceKey(source, key, value)) {
                result.put(key, value);
                return;
            }
            result.put(key, enrichValue(value, key, childPath, resolutions, commandCode, unresolved,
                nightwaveDescriptions, calendarRules));
        });
        enrichRewardSummary(source, result, "reward", "rewardItems");
        enrichRewardSummary(source, result, "attackerReward", "attackerRewards");
        enrichRewardSummary(source, result, "defenderReward", "defenderRewards");
        if ("nightwave".equals(commandCode) && result.get("challenge") instanceof String challenge
            && (result.get("title") == null || CharSequenceUtil.isBlank(result.get("title").toString()))) {
            result.put("title", challenge);
        }
        if ("nightwave".equals(commandCode)
            && (result.get("description") == null || CharSequenceUtil.isBlank(result.get("description").toString()))
            && source.get("challenge") instanceof String challenge) {
            String description = nightwaveDescriptions.get(challenge);
            if (CharSequenceUtil.isNotBlank(description)) {
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
    private static void applyCalendarRule(Map<?, ?> source, Map<String, Object> result,
        List<WarframeStateTranslationDetailsDTO> rules) {
        String key = firstText(source, "uniqueName", "title", "challenge");
        if (CharSequenceUtil.isBlank(key)) {
            return;
        }
        rules.stream().filter(rule -> CharSequenceUtil.equals(rule.uniqueName(), key)).findFirst().ifPresent(rule -> {
            if (CharSequenceUtil.isNotBlank(rule.displayName())
                && (result.get("title") == null || CharSequenceUtil.isBlank(result.get("title").toString())
                    || CharSequenceUtil.equals(String.valueOf(result.get("title")), key))) {
                result.put("title", rule.displayName());
            }
            if (CharSequenceUtil.isNotBlank(rule.description())
                && (result.get("description") == null || CharSequenceUtil.isBlank(result.get("description").toString())
                    || CharSequenceUtil.equals(String.valueOf(result.get("description")), key))) {
                result.put("description", rule.description());
            }
        });
    }

    /**
     * 按约定字段顺序从展示映射取得首个有效文本。
     *
     * @param source 已经组装的原始展示字段映射。
     * @param keys 待处理的查找键集合。
     * @return 首个文本对应的文本。
     */
    private static String firstText(Map<?, ?> source, String... keys) {
        for (String key : keys) {
            Object value = source.get(key);
            if (value instanceof String text && CharSequenceUtil.isNotBlank(text)) {
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
    private static void enrichRewardSummary(Map<?, ?> source, Map<String, Object> result, String summaryKey,
        String itemsKey) {
        if (!(result.get(summaryKey) instanceof String summary)) {
            return;
        }
        if (source.get(itemsKey) instanceof List<?> references && result.get(itemsKey) instanceof List<?> names
            && references.size() == names.size()) {
            Map<String, String> replacements = new LinkedHashMap<>();
            for (int index = 0; index < references.size(); index++) {
                Object raw =
                    references.get(index) instanceof Map<?, ?> entry ? entry.get("item") : references.get(index);
                Object localized = names.get(index) instanceof Map<?, ?> entry ? entry.get("item") : names.get(index);
                if (raw instanceof String reference && CharSequenceUtil.isNotBlank(reference)
                    && localized instanceof String name && CharSequenceUtil.isNotBlank(name)
                    && !reference.equals(name)) {
                    replacements.put(reference, name);
                }
            }
            summary = WarframeRewardSummaryFormatter.replaceKnownReferences(summary, replacements);
        }
        if (result.get(itemsKey) instanceof List<?> localized) {
            List<String> names = new ArrayList<>();
            for (Object value : localized) {
                Object item = value instanceof Map<?, ?> row ? row.get("item") : value;
                if (!(item instanceof String name) || name.startsWith("/") || CharSequenceUtil.isBlank(name))
                    break;
                names.add(name);
            }
            // 多奖励只能按上面的原始身份逐项替换；顺序兜底仅适用于单物品摘要。
            if (names.size() == 1 && localized.size() == 1)
                summary = WarframeRewardSummaryFormatter.replaceInternalReferences(summary, names);
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
    private static Object enrichValue(Object value, String fieldName, String path,
        Map<WarframeReferenceQuery, WarframeReferenceResolutionDTO> resolutions, String commandCode,
        List<WarframeWorldStateUnresolvedReferenceDTO> unresolved, Map<String, String> nightwaveDescriptions,
        List<WarframeStateTranslationDetailsDTO> calendarRules) {
        if (value instanceof Map<?, ?> map) {
            return enrichMap(map, path, resolutions, commandCode, unresolved, nightwaveDescriptions, calendarRules);
        }
        if (value instanceof List<?> list) {
            List<Object> values = new ArrayList<>(list.size());
            for (int index = 0; index < list.size(); index++) {
                values.add(enrichValue(list.get(index), fieldName, path + "[" + index + "]", resolutions, commandCode,
                    unresolved, nightwaveDescriptions, calendarRules));
            }
            return Collections.unmodifiableList(values);
        }
        if (value instanceof String reference && isResolvableReference(fieldName, reference)
            && !isCompositeRewardSummary(fieldName, reference)) {
            var resolution = resolutions.get(new WarframeReferenceQuery(reference, typeHint(fieldName, reference)));
            if (resolution == null) {
                return value;
            }
            if (resolution.resolved())
                return resolution.displayName();
            unresolved.add(new WarframeWorldStateUnresolvedReferenceDTO(commandCode, path, reference,
                unresolvedStatus(resolution.status()), resolution.candidates()));
        }
        return value;
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
            && (value.contains("credits") || value.contains("现金") || value.contains(",") || value.contains("，")
                || value.matches("(?s).*\\s[x×]\\s*\\d+.*"));
    }

    /**
     * 判断字段是否承载需要解析的业务引用。
     *
     * @param fieldName 字段名称。
     * @return 满足字段是否承载需要解析的业务引用时为真，否则为假。
     */
    private static boolean isReferenceField(String fieldName) {
        return fieldName != null && REFERENCE_KEYS.contains(fieldName.toLowerCase(Locale.ROOT));
    }

    /** Human-readable titles are already display values; only internal identifiers need static lookup. */
    private static boolean isResolvableReference(String fieldName, String reference) {
        if (!isReferenceField(fieldName) || CharSequenceUtil.isBlank(reference))
            return false;
        if (TEXT_REFERENCE_KEYS.contains(fieldName.toLowerCase(Locale.ROOT)))
            return isInternalReference(reference);
        if (!"title".equalsIgnoreCase(fieldName))
            return true;
        return reference.startsWith("/") || INTERNAL_REFERENCE_CODE.matcher(reference).matches();
    }

    private static boolean hasResolvedReferenceKey(Map<?, ?> row, String field, Object value) {
        if (!(value instanceof String display) || CharSequenceUtil.isBlank(display) || isInternalReference(display))
            return false;
        Object reference = row.get(field + "Key");
        if (!(reference instanceof String key) || CharSequenceUtil.isBlank(key))
            return false;
        Object status = row.get(field + "ResolutionStatus");
        if (status instanceof String code
            && WarframeWorldStateResolutionStatus.fromCode(code) == WarframeWorldStateResolutionStatus.DIRECT)
            return true;
        if (!isInternalReference(key))
            return false;
        return !(status instanceof String code && !CharSequenceUtil.isBlank(code)
            && !WarframeWorldStateResolutionStatus.isResolvedReference(code));
    }

    private static boolean isInternalReference(String value) {
        return value.startsWith("/") || INTERNAL_REFERENCE_CODE.matcher(value).matches();
    }

    /**
     * 根据展示字段的业务用途选择静态引用解析类型。
     *
     * @param fieldName 字段名称。
     * @param reference 展示字段保留的原始键。
     * @return 游戏资料引用类型对象。
     */
    private static WarframeReferenceType typeHint(String fieldName, String reference) {
        if (StrUtil.startWithIgnoreCase(reference, LANGUAGE_KEY_PREFIX))
            return WarframeReferenceType.LANGUAGE_STRING;
        String normalized = CharSequenceUtil.emptyIfNull(fieldName).toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "node", "nodes", "location", "lastdiscoveredlocation" -> WarframeReferenceType.NODE;
            case "type", "mission", "missiontype", "attackermissions", "defendermissions" ->
                WarframeReferenceType.MISSION_TYPE;
            case "jobtype", "locationtag", "title" -> WarframeReferenceType.ITEM;
            case "mode" -> WarframeReferenceType.LANGUAGE_STRING;
            case "modifier", "risks", "personalmodifiers", "deviation" -> WarframeReferenceType.MODIFIER;
            case "tier" -> WarframeReferenceType.FISSURE_TIER;
            case "boss" -> WarframeReferenceType.SORTIE_BOSS;
            case "syndicate" -> WarframeReferenceType.SYNDICATE;
            case "faction", "attackerfaction", "defenderfaction" -> WarframeReferenceType.FACTION;
            case "upgradetype" -> WarframeReferenceType.UPGRADE_TYPE;
            case "operationtype" -> WarframeReferenceType.OPERATION_TYPE;
            case "agenttype" -> WarframeReferenceType.PERSISTENT_ENEMY;
            case "challenge" -> WarframeReferenceType.ITEM;
            default -> WarframeReferenceType.ITEM;
        };
    }

    /**
     * 按业务身份去重并保留稳定顺序。
     *
     * @param references 引用集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<WarframeWorldStateUnresolvedReferenceDTO>
        distinct(List<WarframeWorldStateUnresolvedReferenceDTO> references) {
        return references.stream().distinct().toList();
    }

    /**
     * 优先使用显式命令代码，否则根据当前展示标题确定匹配的规则代码。
     *
     * @param requested 请求的。
     * @param output 需要补全或转换的输出数据。
     * @return 用于匹配官方状态规则的命令代码。
     */
    private static String commandCode(String requested, WarframeWorldStateCommandVO output) {
        if (CharSequenceUtil.isNotBlank(requested)) {
            return requested;
        }
        String title = output.data().get("title") == null ? "" : String.valueOf(output.data().get("title"));
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
            case "电波", "午夜电波" -> "nightwave";
            case "希图斯" -> "ostrons";
            case "索拉里斯" -> "solaris";
            case "英择谛" -> "entrati";
            case "新闻" -> "news";
            case "促销" -> "flash-sales";
            case "小小黑" -> "persistent-enemies";
            case "集团任务" -> "syndicate-missions";
            case "实时日历", "1999 日历赛季" -> WarframeCommandCodes.CALENDAR;
            case "科研", "科研任务" -> WarframeCommandCodes.ARCHIMEDEAS;
            case "深层科研" -> WarframeCommandCodes.DEEP_ARCHIMEDEAS;
            case "时光科研" -> WarframeCommandCodes.HEX_ARCHIMEDEAS;
            case "灵化轮换" -> WarframeCommandCodes.CIRCUIT_HARD;
            default -> title;
        };
    }

    /**
     * 将未解析或歧义状态转换为中文展示提示。
     *
     * @param status 状态。
     * @return “有歧义”或“未解析”等明确状态提示。
     */
    private static WarframeWorldStateResolutionStatus unresolvedStatus(WarframeReferenceResolutionStatus status) {
        return status == WarframeReferenceResolutionStatus.AMBIGUOUS ? WarframeWorldStateResolutionStatus.AMBIGUOUS
            : WarframeWorldStateResolutionStatus.UNRESOLVED;
    }

}
