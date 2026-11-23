package com.nnm.bot.warframe.runtime.service.worldstate.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.normalization.WarframeNameNormalizer;
import com.nnm.bot.warframe.persistent.curated.constant.WarframeCommunityMappingScope;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeCommunityCatalogService;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.WarframeBountyDefinitionDetailsDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.WarframeStateTranslationDetailsDTO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.rewards.WarframeRewardPoolVO.EntryVO;
import com.nnm.bot.warframe.persistent.official.service.rewards.WarframeBountyDefinitionService;
import com.nnm.bot.warframe.persistent.official.service.rewards.WarframeRewardPoolService;
import com.nnm.bot.warframe.runtime.constant.WarframeBountyRewardPhase;
import com.nnm.bot.warframe.runtime.constant.WarframeIsolationVaultPhase;
import com.nnm.bot.warframe.runtime.constant.WarframeWorldStateResolutionStatus;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateCommandVO;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

import static com.nnm.bot.warframe.runtime.service.worldstate.impl.WarframeWorldStateProjectionFields.first;
import static com.nnm.bot.warframe.runtime.service.worldstate.impl.WarframeWorldStateProjectionFields.normalize;
import static com.nnm.bot.warframe.runtime.service.worldstate.impl.WarframeWorldStateProjectionFields.text;
import static com.nnm.bot.warframe.runtime.service.worldstate.impl.WarframeWorldStateProjectionFields.toStringKeys;

/** 用当轮赏金的协议身份关联静态定义、阶段候选和奖励池，保留三者各自的确定性。 */
@Component
final class WarframeOpenWorldBountyProjection {
    /** 按完整任务路径批量取得已采纳的赏金和阶段定义。 */
    @Resource
    private WarframeBountyDefinitionService bountyDefinitionService;

    /** 按当前轮换给出的完整奖池路径读取带分组的官方奖励。 */
    @Resource
    private WarframeRewardPoolService rewardPoolService;

    /** 提供隔离库阶段名称、场所和核对过的完成奖池别名。 */
    @Resource
    private WarframeCommunityCatalogService communityCatalog;

    /**
     * 判断投影是否包含赏金行，供调用方决定是否查询静态资料。
     *
     * @param output 当前命令的投影。
     * @return 含有实际赏金行时为真。
     */
    static boolean hasJobs(WarframeWorldStateCommandVO output) {
        return rows(output.data().get("items")).stream().anyMatch(region -> !rows(region.get("jobs")).isEmpty());
    }

    /**
     * 按本批动态任务和奖励池引用补全展示；静态候选及不能归属到阶段的分组独立保留。
     *
     * @param outputs 当前快照的展示投影。
     * @param translations 调用方已经批量查到的状态解释。
     * @return 保持输入顺序的展示结果，无赏金时保留原投影。
     */
    List<WarframeWorldStateCommandVO> enrich(List<WarframeWorldStateCommandVO> outputs,
        List<WarframeStateTranslationDetailsDTO> translations) {
        var jobs = outputs.stream().flatMap(output -> rows(output.data().get("items")).stream())
            .flatMap(region -> rows(region.get("jobs")).stream()).toList();
        var jobKeys =
            jobs.stream().map(job -> text(job.get("jobType"))).filter(StrUtil::isNotBlank).distinct().toList();
        var poolKeys = jobs.stream().flatMap(job -> poolReferences(job).stream()).distinct().toList();
        var definitions = new LinkedHashMap<>(bountyDefinitionService.findAll(jobKeys));
        var stageKeys = definitions.values().stream().flatMap(definition -> definition.stages().stream())
            .map(WarframeBountyDefinitionDetailsDTO.StageOptionDTO::jobReference)
            .filter(key -> !definitions.containsKey(key)).distinct().toList();
        if (!stageKeys.isEmpty())
            definitions.putAll(bountyDefinitionService.findAll(stageKeys));
        var pools = rewardPoolService.findAll(poolKeys).stream()
            .collect(Collectors.groupingBy(entry -> normalize(entry.poolKey())));
        Map<String, WarframeStateTranslationDetailsDTO> rules = new LinkedHashMap<>();
        translations.forEach(rule -> rules.putIfAbsent(normalize(rule.uniqueName()), rule));
        var vaults = jobs.stream().anyMatch(job -> Boolean.TRUE.equals(job.get("isVault")))
            ? communityCatalog.mappingTargets() : Map.<WarframeCommunityMappingScope, Map<String, List<String>>>of();
        return outputs.stream().map(output -> {
            if (!hasJobs(output))
                return output;
            Map<String, Object> display = toStringKeys(output.data());
            display.put("items", rows(display.get("items")).stream().map(region -> {
                var copy = toStringKeys(region);
                if (region.containsKey("jobs"))
                    copy.put("jobs", rows(region.get("jobs")).stream()
                        .map(job -> projectJob(job, definitions, rules, pools, vaults)).toList());
                return copy;
            }).toList());
            return new WarframeWorldStateCommandVO(output.text(), display, output.unresolvedReferences());
        }).toList();
    }

    private static Map<String, Object> projectJob(Map<?, ?> input,
        Map<String, WarframeBountyDefinitionDetailsDTO> definitions,
        Map<String, WarframeStateTranslationDetailsDTO> rules, Map<String, List<EntryVO>> pools,
        Map<WarframeCommunityMappingScope, Map<String, List<String>>> vaults) {
        var job = toStringKeys(input);
        String jobKey = text(job.remove("jobType"));
        var definition = jobKey == null ? null : definitions.get(jobKey);
        if (jobKey != null) {
            job.putIfAbsent("typeKey", jobKey);
            putDefinition(job, jobKey, definition, rules.get(normalize(jobKey)));
        }
        var poolKeys = poolReferences(job);
        var missingPools = poolKeys.stream().filter(key -> !pools.containsKey(normalize(key))).toList();
        var directRewards = rows(job.get("rewards"));
        List<Map<String, Object>> groups = rewardGroups(poolKeys, pools);
        var rewards = directRewards.isEmpty()
            ? groups.stream().flatMap(group -> rows(group.get("rewards")).stream()).toList() : directRewards;
        job.put("rewards", rewards);
        if (!missingPools.isEmpty() && directRewards.isEmpty())
            job.put("rewardNotice", "以下奖池尚未收录：" + String.join("、", missingPools));
        Map<Integer, List<Map<String, Object>>> buckets = new LinkedHashMap<>();
        // 仅有一个确切的入库奖池时，分组序号才可用于分配阶段。
        if (poolKeys.size() == 1 && directRewards.isEmpty()) {
            for (var group : groups)
                buckets.put((Integer)group.get("group") - 1, rows(group.get("rewards")));
        }
        String poolKey = poolKeys.size() == 1 ? poolKeys.getFirst() : null;
        if (Boolean.TRUE.equals(job.get("isVault"))) {
            projectVault(job, poolKey, buckets, vaults);
            return job;
        }
        if (poolKeys.isEmpty() && directRewards.isEmpty())
            job.putIfAbsent("rewardNotice", "上游未提供本轮奖池");
        WarframeBountyRewardPhase.rotation(poolKey).ifPresent(rotation -> job.put("rewardRotation", rotation));
        var standing = list(job.get("standingStages"));
        boolean phaseRewards = !Boolean.TRUE.equals(job.get("endless"))
            && WarframeBountyRewardPhase.supports(poolKey, standing.size(), buckets.keySet());
        if (!phaseRewards && !rewards.isEmpty())
            job.put("unassignedRewardGroups",
                directRewards.isEmpty() ? groups : List.of(Map.of("group", 1, "rewards", directRewards)));
        var staticStages = staticStages(definition, definitions, rules);
        // 动态阶段数与静态定义不一致时保留全部定义，包括末尾目标，避免用静态下标冒充当轮阶段。
        boolean aligned = !standing.isEmpty() && staticStages.size() == standing.size()
            && staticStages.getLast().get("stage").equals(standing.size());
        if (!aligned && !staticStages.isEmpty())
            job.put("staticMissionStages", staticStages);
        List<Map<String, Object>> stages = new ArrayList<>(standing.size());
        for (int index = 0; index < standing.size(); index++) {
            Map<String, Object> stage = aligned ? staticStages.get(index) : new LinkedHashMap<>();
            stage.put("stage", index + 1);
            if (standing.get(index) instanceof Integer amount)
                stage.put("standing", amount);
            if (phaseRewards) {
                stage.put("rewards", buckets.get(WarframeBountyRewardPhase.bucketFor(index, standing.size())));
                if (index == standing.size() - 1)
                    stage.put("finalStage", true);
            }
            stages.add(stage);
        }
        if (!stages.isEmpty())
            job.put("stages", stages);
        return job;
    }

    private static List<Map<String, Object>> rewardGroups(List<String> poolKeys, Map<String, List<EntryVO>> pools) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (String key : poolKeys) {
            var buckets =
                pools.getOrDefault(normalize(key), List.of()).stream().collect(Collectors.groupingBy(EntryVO::stage,
                    TreeMap::new, Collectors.mapping(WarframeOpenWorldBountyProjection::reward, Collectors.toList())));
            buckets.forEach(
                (group, entries) -> result.add(Map.of("group", group + 1, "poolKey", key, "rewards", entries)));
        }
        return result;
    }

    private static Map<String, Object> reward(EntryVO item) {
        Map<String, Object> reward = new LinkedHashMap<>();
        reward.put("itemKey", item.itemKey());
        reward.put("displayName", item.itemName());
        reward.put("count", item.count());
        reward.put("probability", item.probability());
        reward.put("rarity", item.rarity());
        reward.put("imageUrl", item.imageUrl());
        return reward;
    }

    private static void putDefinition(Map<String, Object> job, String key,
        WarframeBountyDefinitionDetailsDTO definition, WarframeStateTranslationDetailsDTO rule) {
        String type = text(job.get("type"));
        String name = first(rule == null ? null : rule.displayName(), definition == null ? null : definition.nameKey());
        String description =
            first(rule == null ? null : rule.description(), definition == null ? null : definition.descriptionKey());
        if (name != null && (type == null || key.equalsIgnoreCase(type)))
            job.put("type", name);
        WarframeWorldStateProjectionFields.putIfAbsent(job, "description", description);
    }

    private static List<Map<String, Object>> staticStages(WarframeBountyDefinitionDetailsDTO definition,
        Map<String, WarframeBountyDefinitionDetailsDTO> definitions,
        Map<String, WarframeStateTranslationDetailsDTO> rules) {
        if (definition == null)
            return List.of();
        Map<Integer, LinkedHashSet<String>> candidates = new TreeMap<>();
        definition.stages().forEach(option -> candidates.computeIfAbsent(option.stage(), key -> new LinkedHashSet<>())
            .add(option.jobReference()));
        return candidates.entrySet().stream().<Map<String, Object>>map(entry -> {
            var stage = new LinkedHashMap<String, Object>();
            stage.put("stage", entry.getKey() + 1);
            stage.put("missions", entry.getValue().stream().map(key -> {
                Map<String, Object> mission = new LinkedHashMap<>();
                var candidate = definitions.get(key);
                var rule = rules.get(normalize(key));
                mission.put("missionKey", key);
                mission.put("mission", first(rule == null ? null : rule.displayName(),
                    candidate == null ? null : candidate.nameKey(), key));
                WarframeWorldStateProjectionFields.putIfAbsent(mission, "description", first(
                    rule == null ? null : rule.description(), candidate == null ? null : candidate.descriptionKey()));
                return mission;
            }).toList());
            return stage;
        }).toList();
    }

    private static void projectVault(Map<String, Object> job, String poolKey,
        Map<Integer, List<Map<String, Object>>> buckets, Map<WarframeCommunityMappingScope, Map<String, List<String>>> vaults) {
        String location = text(job.get("locationTag"));
        var names = WarframeCommunityMappingScope.BOUNTY_STAGE.targets(vaults).getOrDefault(WarframeNameNormalizer.phrase(location), List.of());
        if (names.size() == 1) {
            String name = names.getFirst();
            job.put("type", name);
            job.put("typeKey", location);
            job.put("typeResolutionStatus", WarframeWorldStateResolutionStatus.DIRECT.code());
            job.put("locationTag", name);
            job.put("locationTagKey", location);
            job.put("locationTagResolutionStatus", WarframeWorldStateResolutionStatus.DIRECT.code());
        }
        var completion = WarframeCommunityMappingScope.ISOLATION_VAULT_REWARD.target(vaults, poolKey)
            .filter(NumberUtil::isInteger).map(Integer::valueOf).map(buckets::get).orElse(List.of());
        // 命令与可视化共用已核实的完成奖池；xpAmounts 和协议原数组保留在原始快照。
        job.put("rewards", completion);
        job.remove("standingStages");
        if (completion.isEmpty())
            job.putIfAbsent("rewardNotice", "本轮隔离库完成奖励尚未核实");
        List<Map<String, Object>> stages = new ArrayList<>();
        for (var phase : WarframeIsolationVaultPhase.values()) {
            var labels = WarframeCommunityMappingScope.ISOLATION_VAULT_PHASE.targets(vaults).getOrDefault(WarframeNameNormalizer.phrase(phase.name()), List.of());
            if (labels.size() != 1)
                continue;
            Map<String, Object> stage = new LinkedHashMap<>();
            stage.put("stage", phase.ordinal() + 1);
            stage.put("stageLabel", labels.getFirst());
            if (phase == WarframeIsolationVaultPhase.GUARDIANS && !completion.isEmpty())
                stage.put("rewards", completion);
            stages.add(stage);
        }
        job.put("stages", stages);
    }

    private static List<String> poolReferences(Map<?, ?> job) {
        Map<String, String> unique = new LinkedHashMap<>();
        for (Object value : list(job.get("rewardPoolReferences"))) {
            String key = StrUtil.trim(text(value));
            if (StrUtil.isNotBlank(key))
                unique.putIfAbsent(normalize(key), key);
        }
        return List.copyOf(unique.values());
    }

    private static List<?> list(Object value) {
        return value instanceof List<?> list ? list : List.of();
    }

    private static List<Map<String, Object>> rows(Object value) {
        return list(value).stream().filter(Map.class::isInstance).map(row -> toStringKeys((Map<?, ?>)row)).toList();
    }

    /** 隔离库关联使用的一次性受管资料。 */
}
