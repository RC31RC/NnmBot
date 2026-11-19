package com.nnm.bot.warframe.runtime.service.worldstate.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.warframe.constant.WarframeCommandCodes;
import com.nnm.bot.warframe.persistent.curated.constant.WarframeCommunityTermGroup;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeCommunityCatalogService;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceResolutionStatus;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceType;
import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.WarframeReferenceResolutionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.WarframeBountyDefinitionDetailsDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.WarframeStateTranslationDetailsDTO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.rewards.WarframeRewardPoolVO;
import com.nnm.bot.warframe.persistent.official.service.reference.WarframeReferenceResolverService;
import com.nnm.bot.warframe.persistent.official.service.rewards.WarframeBountyDefinitionService;
import com.nnm.bot.warframe.persistent.official.service.rewards.WarframeRewardPoolService;
import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeWorldStateRuleService;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeRewardSummaryFormatter;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateCommandVO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WarframeWorldStateUnresolvedReferenceDTO;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeReferenceProjectionEnrichmentService;
import com.nnm.bot.warframe.normalization.WarframeNameNormalizer;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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

    private static final Set<String> RESEARCH_CODES = Set.of(WarframeCommandCodes.ARCHIMEDEAS,
        WarframeCommandCodes.DEEP_ARCHIMEDEAS, WarframeCommandCodes.HEX_ARCHIMEDEAS);

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
    public WarframeWorldStateCommandVO enrich(WarframeWorldStateCommandVO output, String commandCode) {
        if (output == null) {
            return null;
        }
        return enrichAll(List.of(output), commandCode).getFirst();
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
        boolean containsBounties = safeOutputs.stream().anyMatch(output -> containsJobs(output.data()));
        boolean containsNightwave =
            safeOutputs.stream().anyMatch(output -> "nightwave".equals(commandCode(commandCode, output)));
        boolean containsCalendar =
            safeOutputs.stream().anyMatch(output -> "calendar".equals(commandCode(commandCode, output)));
        boolean containsResearch =
            safeOutputs.stream().anyMatch(output -> RESEARCH_CODES.contains(commandCode(commandCode, output)));
        Map<String, WarframeReferenceType> ruleReferences = new LinkedHashMap<>();
        if (containsBounties || containsNightwave || containsCalendar || containsResearch)
            safeOutputs.forEach(output -> collect(output.data(), null, ruleReferences));
        List<WarframeStateTranslationDetailsDTO> translations = ruleReferences.isEmpty() ? List.of()
            : safeRules(worldStateRuleService.findTranslations(ruleReferences.keySet()));
        final List<WarframeStateTranslationDetailsDTO> calendarRules = containsCalendar ? translations : List.of();
        boolean containsArbitration =
            safeOutputs.stream().anyMatch(output -> "arbitration".equals(commandCode(commandCode, output))
                || "arbitration-table".equals(commandCode(commandCode, output)));
        if (containsArbitration) {
            var essence = communityCatalog.arbitrationEssences();
            safeOutputs = safeOutputs.stream().map(output -> new WarframeWorldStateCommandVO(output.text(),
                enrichArbitration(output.data(), essence), output.unresolvedReferences())).toList();
        }
        if (containsBounties) {
            var stageNames = communityCatalog.displayTerms(WarframeCommunityTermGroup.BOUNTY_STAGE);
            Set<String> jobTypes = new LinkedHashSet<>();
            Set<String> poolKeys = new LinkedHashSet<>();
            safeOutputs.forEach(output -> collectBountyKeys(output.data(), jobTypes, poolKeys));
            var definitions = bountyDefinitionService.findAll(jobTypes);
            var pools = rewardPoolService.findAll(poolKeys);
            safeOutputs = safeOutputs.stream()
                .map(output -> new WarframeWorldStateCommandVO(output.text(),
                    prepareBounties(output.data(), translations, pools, definitions, stageNames),
                    output.unresolvedReferences()))
                .toList();
        }
        Map<String, String> nightwaveDescriptions = containsNightwave ? stateDescriptions(translations) : Map.of();
        Map<String, String> researchDescriptions = containsResearch ? stateDescriptions(translations) : Map.of();
        Map<String, WarframeReferenceType> references = new LinkedHashMap<>();
        safeOutputs.forEach(output -> collect(output.data(), null, references));
        if (CollUtil.isEmpty(references)) {
            return safeOutputs;
        }
        Map<String, WarframeReferenceResolutionDTO> resolutions = referenceResolver.resolveAll(references);
        return safeOutputs.stream().map(output -> {
            String code = commandCode(commandCode, output);
            var scoped = scopedResolutions(output, code, resolutions);
            var textReplacements = scoped.values().stream().filter(WarframeReferenceResolutionDTO::resolved)
                .filter(resolution -> !StrUtil.equals(resolution.reference(), resolution.displayName()))
                .sorted(Comparator
                    .comparingInt((WarframeReferenceResolutionDTO resolution) -> resolution.reference().length())
                    .reversed())
                .toList();
            return enrichOutput(output, scoped, textReplacements, code, nightwaveDescriptions, calendarRules,
                researchDescriptions);
        }).toList();
    }

    /**
     * 只对当前科研或赏金投影补充整词译名，保持官方结果、歧义及其他命令互不影响。
     *
     * @param output 当前展示投影。
     * @param code 当前命令代码。
     * @param official 已完成的官方及自维护业务引用解析。
     * @return 当前投影独有的解析结果。
     */
    private Map<String, WarframeReferenceResolutionDTO> scopedResolutions(WarframeWorldStateCommandVO output,
        String code, Map<String, WarframeReferenceResolutionDTO> official) {
        boolean research = switch (code) {
            case WarframeCommandCodes.ARCHIMEDEAS, WarframeCommandCodes.DEEP_ARCHIMEDEAS,
                WarframeCommandCodes.HEX_ARCHIMEDEAS -> true;
            default -> false;
        };
        boolean sortie = WarframeCommandCodes.SORTIES.equals(code);
        if (!research && !sortie && !containsJobs(output.data()))
            return official;
        var group = research ? WarframeCommunityTermGroup.ARCHIMEDEA
            : sortie ? WarframeCommunityTermGroup.SORTIE_MODIFIER : WarframeCommunityTermGroup.BOUNTY_STAGE;
        var terms = communityCatalog.displayTerms(group);
        if (terms == null || terms.isEmpty())
            return official;
        Map<String, WarframeReferenceType> references = new LinkedHashMap<>();
        collect(output.data(), null, references);
        Map<String, WarframeReferenceResolutionDTO> result = new LinkedHashMap<>(official);
        references.forEach((key, type) -> {
            var previous = official.get(key);
            if (((research || sortie) && type != WarframeReferenceType.MODIFIER)
                || (previous != null && previous.status() != WarframeReferenceResolutionStatus.UNRESOLVED))
                return;
            var candidates = terms.getOrDefault(WarframeNameNormalizer.phrase(key), List.of());
            if (candidates.isEmpty() && group == WarframeCommunityTermGroup.BOUNTY_STAGE
                && terms.values().stream().anyMatch(values -> values.contains(key)))
                candidates = List.of(key);
            if (candidates.isEmpty())
                return;
            var status = candidates.size() == 1 ? WarframeReferenceResolutionStatus.RESOLVED
                : WarframeReferenceResolutionStatus.AMBIGUOUS;
            result.put(key,
                new WarframeReferenceResolutionDTO(key, candidates.size() == 1 ? candidates.getFirst() : key, key,
                    "community:" + group.code(), status, candidates));
        });
        return result;
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
                records.stream().map(WarframeCommunityRecordDTO::details)
                    .filter(details -> sameNode(node, details.nodeName())).findFirst().ifPresent(details -> {
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
        if (StrUtil.isBlank(left) || StrUtil.isBlank(right))
            return false;
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
        if (amount >= 400)
            return "S";
        if (amount >= 350)
            return "B";
        if (amount >= 300)
            return "C";
        return "D";
    }

    /**
     * 将已采纳状态译名组织为描述查找表。
     *
     * @param translations 译名集合。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private static Map<String, String> stateDescriptions(List<WarframeStateTranslationDetailsDTO> translations) {
        Map<String, String> descriptions = new LinkedHashMap<>();
        translations.stream().filter(record -> StrUtil.isNotBlank(record.uniqueName()))
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
            return map.containsKey("jobs")
                || map.values().stream().anyMatch(WarframeReferenceProjectionEnrichmentServiceImpl::containsJobs);
        }
        return value instanceof List<?> list
            && list.stream().anyMatch(WarframeReferenceProjectionEnrichmentServiceImpl::containsJobs);
    }

    /**
     * 递归收集本次动态赏金的静态引用，所有引用先去重再按表批量补查。
     *
     * @param value 动态展示数据。
     * @param jobs 当前请求的赏金类型。
     * @param pools 当前请求的奖励池。
     */
    private void collectBountyKeys(Object value, Set<String> jobs, Set<String> pools) {
        if (value instanceof Map<?, ?> map) {
            Object job = map.get("jobType");
            Object pool = map.get("rewardPoolReference");
            if (job != null && StrUtil.isNotBlank(job.toString()))
                jobs.add(job.toString());
            if (pool != null && StrUtil.isNotBlank(pool.toString()))
                pools.add(pool.toString());
            map.values().forEach(child -> collectBountyKeys(child, jobs, pools));
        } else if (value instanceof Collection<?> values) {
            values.forEach(child -> collectBountyKeys(child, jobs, pools));
        }
    }

    /**
     * 赏金动态参数关联数据库里的名称、说明和奖励池，再交给统一引用解析。
     *
     * @param source 已经组装的原始展示字段映射。
     * @param translations 译名集合。
     * @param pools 奖池集合。
     * @param definitions 本次命中的官方赏金定义。
     * @param stageNames 仅用于动态 locationTag 的完整阶段名称。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private Map<String, Object> prepareBounties(Map<?, ?> source, List<WarframeStateTranslationDetailsDTO> translations,
        List<WarframeRewardPoolVO.EntryVO> pools, Map<String, WarframeBountyDefinitionDetailsDTO> definitions,
        Map<String, List<String>> stageNames) {
        Map<String, Object> result = new LinkedHashMap<>();
        source.forEach((key, value) -> {
            Object prepared = value;
            if (value instanceof Map<?, ?> map) {
                prepared = prepareBounties(map, translations, pools, definitions, stageNames);
            } else if (value instanceof List<?> list) {
                prepared = list.stream().map(entry -> entry instanceof Map<?, ?> map
                    ? prepareBounties(map, translations, pools, definitions, stageNames) : entry).toList();
            }
            result.put(String.valueOf(key), prepared);
        });
        if (!result.containsKey("jobType")) {
            return result;
        }
        String jobType = String.valueOf(result.remove("jobType"));
        Optional.ofNullable(definitions.get(jobType)).ifPresent(definition -> {
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
            displayDefinition.put("stages", definition.stages().stream().map(option -> Map.<String, Object>of("stage",
                option.stage(), "option", option.option(), "mission", option.jobReference())).toList());
            result.put("definition", displayDefinition);
        });
        translations.stream().filter(record -> jobType.equals(record.uniqueName())).findFirst().ifPresent(record -> {
            if (StrUtil.isNotBlank(record.displayName()))
                result.put("type", record.displayName());
            if (StrUtil.isNotBlank(record.description()))
                result.put("description", record.description());
        });
        String stage = String.valueOf(result.getOrDefault("locationTag", ""));
        var names = stageNames.getOrDefault(WarframeNameNormalizer.phrase(stage), List.of());
        String type = String.valueOf(result.getOrDefault("type", ""));
        if (Boolean.TRUE.equals(result.get("isVault")) && names.size() == 1
            && (StrUtil.isBlank(type) || "隔离库".equals(type) || jobType.equals(type) || stage.equals(type))) {
            result.put("type", names.getFirst());
            result.put("locationTagReference", stage);
        }
        Object poolValue = result.remove("rewardPoolReference");
        String poolReference = poolValue == null ? "" : poolValue.toString();
        String displayType = result.get("type") == null ? null : result.get("type").toString();
        if (StrUtil.isBlank(displayType) && isVaultBountyPool(poolReference)) {
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
        var definition = definitions.get(jobType);
        var stages = bountyStages(definition, result, translations);
        if (!stages.isEmpty())
            result.put("stages", stages);
        return result;
    }

    /**
     * 按同一份动态赏金的阶段组织静态可选任务及其阶段奖励，绝不推断随机选择。
     *
     * @param definition 当前赏金的官方静态阶段定义，可能缺失。
     * @param job 当前赏金实例及它自己的奖励池。
     * @param translations 已采纳的阶段任务中文译名与说明。
     * @return 各阶段可选任务及本赏金奖励；均不标为当前选中结果。
     */
    private static List<Map<String, Object>> bountyStages(WarframeBountyDefinitionDetailsDTO definition,
        Map<String, Object> job, List<WarframeStateTranslationDetailsDTO> translations) {
        Map<Integer, List<String>> candidates = new java.util.TreeMap<>();
        if (definition != null) {
            for (var option : definition.stages()) {
                candidates.computeIfAbsent(option.stage(), ignored -> new ArrayList<>()).add(option.jobReference());
            }
        }
        Map<Integer, List<Map<String, Object>>> rewardsByStage = new java.util.TreeMap<>();
        if (job.get("rewards") instanceof List<?> rewards) {
            for (Object value : rewards) {
                if (value instanceof Map<?, ?> reward && reward.get("stage") instanceof Number stage) {
                    rewardsByStage.computeIfAbsent(stage.intValue(), ignored -> new ArrayList<>())
                        .add(stringKeys(reward));
                }
            }
        }
        Set<Integer> indices = new java.util.TreeSet<>(candidates.keySet());
        indices.addAll(rewardsByStage.keySet());
        List<Map<String, Object>> stages = new ArrayList<>(indices.size());
        for (int index : indices) {
            Map<String, Object> stage = new LinkedHashMap<>();
            stage.put("stage", index + 1);
            stage.put("selectionStatus", "任务仅为静态候选，本轮具体阶段任务由游戏决定");
            stage.put("missions", candidates.getOrDefault(index, List.of()).stream().map(reference -> {
                Map<String, Object> option = new LinkedHashMap<>();
                option.put("missionKey", reference);
                var rule =
                    translations.stream().filter(row -> reference.equals(row.uniqueName())).findFirst().orElse(null);
                option.put("mission",
                    rule != null && StrUtil.isNotBlank(rule.displayName()) ? rule.displayName() : "未收录阶段任务名称");
                if (rule != null && StrUtil.isNotBlank(rule.description()))
                    option.put("description", rule.description());
                return option;
            }).toList());
            stage.put("rewards", rewardsByStage.getOrDefault(index, List.of()));
            if (job.get("standingStages") instanceof List<?> standing && index < standing.size())
                stage.put("standing", standing.get(index));
            stages.add(stage);
        }
        return stages;
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
        Map<String, WarframeReferenceResolutionDTO> resolutions, List<WarframeReferenceResolutionDTO> textReplacements,
        String commandCode, Map<String, String> nightwaveDescriptions,
        List<WarframeStateTranslationDetailsDTO> calendarRules, Map<String, String> researchDescriptions) {
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
                if (StrUtil.isNotBlank(description) && (result.get("description") == null
                    || StrUtil.isBlank(String.valueOf(result.get("description"))))) {
                    result.put("description", description);
                }
            }
        }
        for (String key : List.of("risks", "personalModifiers")) {
            if (original.get(key) instanceof List<?> entries) {
                Map<String, String> explanations = new LinkedHashMap<>();
                entries.stream().filter(String.class::isInstance).map(String.class::cast).forEach(reference -> {
                    if (StrUtil.isNotBlank(descriptions.get(reference))) {
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
    private static void collect(Object value, String fieldName, Map<String, WarframeReferenceType> references) {
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
            && !isCompositeRewardSummary(fieldName, reference) && StrUtil.isNotBlank(reference)) {
            references.putIfAbsent(reference, typeHint(fieldName));
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
        Map<String, WarframeReferenceResolutionDTO> resolutions, String commandCode,
        List<WarframeWorldStateUnresolvedReferenceDTO> unresolved, Map<String, String> nightwaveDescriptions,
        List<WarframeStateTranslationDetailsDTO> calendarRules) {
        Map<String, Object> result = new LinkedHashMap<>();
        source.forEach((keyValue, value) -> {
            String key = String.valueOf(keyValue);
            String childPath = StrUtil.isBlank(path) ? key : path + "." + key;
            EnrichedValue enriched = enrichValue(value, key, childPath, resolutions, commandCode, unresolved,
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
    private static void applyCalendarRule(Map<?, ?> source, Map<String, Object> result,
        List<WarframeStateTranslationDetailsDTO> rules) {
        String key = firstText(source, "uniqueName", "title", "challenge");
        if (StrUtil.isBlank(key)) {
            return;
        }
        rules.stream().filter(rule -> StrUtil.equals(rule.uniqueName(), key)).findFirst().ifPresent(rule -> {
            if (StrUtil.isNotBlank(rule.displayName())
                && (result.get("title") == null || StrUtil.isBlank(result.get("title").toString())
                    || StrUtil.equals(String.valueOf(result.get("title")), key))) {
                result.put("title", rule.displayName());
            }
            if (StrUtil.isNotBlank(rule.description())
                && (result.get("description") == null || StrUtil.isBlank(result.get("description").toString())
                    || StrUtil.equals(String.valueOf(result.get("description")), key))) {
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
                if (raw instanceof String reference && StrUtil.isNotBlank(reference) && localized instanceof String name
                    && StrUtil.isNotBlank(name) && !reference.equals(name)) {
                    replacements.put(reference, name);
                }
            }
            summary = WarframeRewardSummaryFormatter.replaceKnownReferences(summary, replacements);
        }
        if (result.get(itemsKey) instanceof List<?> localized) {
            List<String> names = new ArrayList<>();
            for (Object value : localized) {
                Object item = value instanceof Map<?, ?> row ? row.get("item") : value;
                if (!(item instanceof String name) || name.startsWith("/") || name.isBlank())
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
    private static EnrichedValue enrichValue(Object value, String fieldName, String path,
        Map<String, WarframeReferenceResolutionDTO> resolutions, String commandCode,
        List<WarframeWorldStateUnresolvedReferenceDTO> unresolved, Map<String, String> nightwaveDescriptions,
        List<WarframeStateTranslationDetailsDTO> calendarRules) {
        if (value instanceof Map<?, ?> map) {
            return new EnrichedValue(
                enrichMap(map, path, resolutions, commandCode, unresolved, nightwaveDescriptions, calendarRules), value,
                false, false, List.of());
        }
        if (value instanceof List<?> list) {
            List<Object> values = new ArrayList<>(list.size());
            boolean changed = false;
            Set<String> statuses = new LinkedHashSet<>();
            for (int index = 0; index < list.size(); index++) {
                EnrichedValue enriched = enrichValue(list.get(index), fieldName, path + "[" + index + "]", resolutions,
                    commandCode, unresolved, nightwaveDescriptions, calendarRules);
                values.add(enriched.value());
                changed |= enriched.changed();
                statuses.addAll(enriched.statuses());
            }
            return new EnrichedValue(List.copyOf(values), value, changed, CollUtil.isNotEmpty(statuses),
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
                return new EnrichedValue(resolution.displayName(), reference, changed, false, List.of());
            }
            unresolved.add(new WarframeWorldStateUnresolvedReferenceDTO(commandCode, path, reference,
                unresolvedStatus(resolution.status()), resolution.candidates()));
            return new EnrichedValue(reference, reference, false, true, List.of(resolution.status().name()));
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

    /**
     * 根据展示字段的业务用途选择静态引用解析类型。
     *
     * @param fieldName 字段名称。
     * @return 游戏资料引用类型对象。
     */
    private static WarframeReferenceType typeHint(String fieldName) {
        String normalized = StrUtil.emptyIfNull(fieldName).toLowerCase(Locale.ROOT);
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

    private record EnrichedValue(Object value, Object original, boolean changed, boolean hasUnresolved,
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
        if (StrUtil.isNotBlank(requested)) {
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
            case "实时日历", "1999 日历赛季" -> "calendar";
            case "科研", "科研任务" -> WarframeCommandCodes.ARCHIMEDEAS;
            case "深层科研" -> WarframeCommandCodes.DEEP_ARCHIMEDEAS;
            case "时光科研" -> WarframeCommandCodes.HEX_ARCHIMEDEAS;
            default -> title;
        };
    }

    /**
     * 将未解析或歧义状态转换为中文展示提示。
     *
     * @param status 状态。
     * @return “有歧义”或“未解析”等明确状态提示。
     */
    private static String unresolvedStatus(WarframeReferenceResolutionStatus status) {
        return status == WarframeReferenceResolutionStatus.AMBIGUOUS ? "有歧义" : "未解析";
    }

}
