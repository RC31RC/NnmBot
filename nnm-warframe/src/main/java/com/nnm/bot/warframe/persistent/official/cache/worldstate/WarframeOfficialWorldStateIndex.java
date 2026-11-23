package com.nnm.bot.warframe.persistent.official.cache.worldstate;

import cn.hutool.core.text.CharSequenceUtil;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.runtime.cache.dataset.WarframeDatasetCache;
import com.nnm.bot.warframe.constant.WarframeDatasetCacheNames;
import com.nnm.bot.warframe.persistent.official.config.worldstate.WarframeWorldStateDataProperties;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceResolutionStatus;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceType;
import com.nnm.bot.warframe.persistent.official.pojo.dto.progression.OfficialChallengeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.OfficialLanguageStringDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialBountyDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.OfficialRegionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.OfficialBossDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.WarframeBountyPoolRewardDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.OfficialFactionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.OfficialFissureModifierDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.OfficialMissionTypeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.OfficialSyndicateDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.OfficialUpgradeTypeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.WarframeWorldStateCodebookEntryDTO;
import com.nnm.bot.warframe.persistent.official.pojo.query.worldstate.WarframeBountyReferenceQuery;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialLookupService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import com.nnm.bot.warframe.normalization.WarframeNameNormalizer;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

/** 通过统一类型化查询构建不可变 WorldState 静态代码簿。 */
@Component
public class WarframeOfficialWorldStateIndex {
    private static final Pattern BOUNTY_LEVEL = Pattern.compile("^Level\\s+(\\d{1,3})\\s*-\\s*\\d{1,3}\\b");

    @Resource
    private WarframeOfficialRecordReadService catalog;
    @Resource
    private WarframeOfficialLookupService lookup;
    @Resource
    private WarframeDatasetCache cache;
    @Resource
    private WarframeWorldStateDataProperties codebookProperties;

    /**
     * 只组合本次赏金需要的独立静态投影，不常驻一份跨表合并索引。
     *
     * @param query 本轮验证后的节点、挑战和静态候选池引用。
     * @return 请求范围内的星图、挑战和候选奖励池。
     */
    public Snapshot current(WarframeBountyReferenceQuery query) {
        Map<String,
            Reference> nodes = references(WarframeOfficialDataDomain.REGION, query.nodes(), OfficialRegionDTO.class,
                (key, value) -> new Reference(key, first(value.getZhName(), value.getName(), value.getNameEn()),
                    first(value.getNameEn(), value.getName()), null,
                    localized(value.getMissionTypeNameZh(), value.getMissionTypeName(), value.getMissionType()),
                    localized(value.getZhSystemName(), value.getSystemName(), null), value.getMissionType()));
        Map<String, Reference> challengeReferences = references(WarframeOfficialDataDomain.CHALLENGE,
            query.challenges(), OfficialChallengeDTO.class, (key, value) -> {
                String description = first(value.getDescriptionZh(), value.getDescriptionEn(), value.getDescription());
                if (description != null && value.getRequiredCount() != null) {
                    description =
                        description.replace("|COUNT|", value.getRequiredCount().stripTrailingZeros().toPlainString());
                }
                return new Reference(key, first(value.getNameZh(), value.getName()),
                    first(value.getNameEn(), value.getName()), description, null, null, null);
            });

        return new Snapshot(nodes, challengeReferences, readPools(query.poolsBySyndicate()));
    }

    private <T extends OfficialRecordDTO> Map<String, Reference> references(WarframeOfficialDataDomain domain,
        Collection<String> keys, Class<T> type, RecordMapper<T, Reference> mapper) {
        if (keys.isEmpty())
            return Map.of();
        var records = lookup.records(domain, keys);
        Map<String, Reference> result = new LinkedHashMap<>();
        for (String key : keys) {
            var record = records.get(normalize(key));
            if (record != null)
                result.put(key, mapper.map(record.bkey(), type.cast(record.data())));
        }
        return Map.copyOf(result);
    }

    /**
     * 查询一个小代码表的独立快照，不加载其他代码表或语言字典。
     *
     * @param type 动态引用类别。
     * @return 该类别的键与名称入口。
     */
    public Map<String, WarframeWorldStateCodebookEntryDTO> codebook(WarframeReferenceType type) {
        String batchId = "";
        Map<WarframeReferenceType, Map<String, WarframeWorldStateCodebookEntryDTO>> codebooks =
            new EnumMap<>(WarframeReferenceType.class);
        addCodebook(type, codebooks, WarframeReferenceType.MISSION_TYPE, WarframeOfficialDataDomain.MISSION_TYPE,
            OfficialMissionTypeDTO.class, OfficialMissionTypeDTO::getNameKey, OfficialMissionTypeDTO::getName,
            OfficialMissionTypeDTO::getNameEn, OfficialMissionTypeDTO::getNameZh, value -> null, batchId);
        addCodebook(type, codebooks, WarframeReferenceType.FACTION, WarframeOfficialDataDomain.FACTION,
            OfficialFactionDTO.class, OfficialFactionDTO::getNameKey, OfficialFactionDTO::getName,
            OfficialFactionDTO::getNameEn, OfficialFactionDTO::getNameZh, value -> null, batchId);
        addCodebook(type, codebooks, WarframeReferenceType.FISSURE_TIER, WarframeOfficialDataDomain.FISSURE_MODIFIER,
            OfficialFissureModifierDTO.class, value -> null, OfficialFissureModifierDTO::getName,
            OfficialFissureModifierDTO::getNameEn, OfficialFissureModifierDTO::getNameZh, value -> null, batchId);
        addCodebook(type, codebooks, WarframeReferenceType.SORTIE_BOSS, WarframeOfficialDataDomain.BOSS,
            OfficialBossDTO.class, OfficialBossDTO::getNameKey, OfficialBossDTO::getName, OfficialBossDTO::getNameEn,
            OfficialBossDTO::getNameZh, value -> null, batchId);
        addCodebook(type, codebooks, WarframeReferenceType.SYNDICATE, WarframeOfficialDataDomain.SYNDICATE,
            OfficialSyndicateDTO.class, OfficialSyndicateDTO::getNameKey, OfficialSyndicateDTO::getName,
            OfficialSyndicateDTO::getNameEn, OfficialSyndicateDTO::getNameZh, value -> null, batchId);
        addCodebook(type, codebooks, WarframeReferenceType.UPGRADE_TYPE, WarframeOfficialDataDomain.UPGRADE_TYPE,
            OfficialUpgradeTypeDTO.class, value -> null, OfficialUpgradeTypeDTO::getName,
            OfficialUpgradeTypeDTO::getNameEn, OfficialUpgradeTypeDTO::getNameZh, value -> null, batchId);

        if (type == WarframeReferenceType.NODE)
            return cache.snapshot(WarframeDatasetCacheNames.official(WarframeOfficialDataDomain.REGION), "codebook",
                this::nodeCodebook);
        return codebooks.getOrDefault(type, Map.of());
    }

    /**
     * 保留星图原始任务与阵营引用，使仲裁可按各自代码表重新投影。
     *
     * @return 星图节点的轻量展示入口。
     */
    private Map<String, WarframeWorldStateCodebookEntryDTO> nodeCodebook() {
        Map<String, WarframeWorldStateCodebookEntryDTO> entries = new LinkedHashMap<>();
        readUncached(WarframeOfficialDataDomain.REGION, OfficialRegionDTO.class, (key, value) -> {
            String name = Stream
                .of(localized(value.getZhName(), value.getNameEn(), value.getName()), value.getNameEn(),
                    value.getName())
                .filter(WarframeNameNormalizer::isReadableDisplayName).filter(candidate -> !candidate.equals(key))
                .findFirst().orElse(null);
            if (name == null && CharSequenceUtil.isBlank(value.getNameKey()))
                return null;
            String system = localized(value.getZhSystemName(), value.getSystemName(), null);
            if (!WarframeNameNormalizer.isReadableDisplayName(system))
                system = null;
            var entry = new WarframeWorldStateCodebookEntryDTO(key,
                name == null ? key : name + (system == null ? "" : "（" + system + "）"), "", value.getMissionType(),
                "dataset:REGION",
                name == null ? WarframeReferenceResolutionStatus.UNRESOLVED
                    : WarframeReferenceResolutionStatus.RESOLVED,
                value.getFaction(), false, value.getNameKey(), List.of());
            entries.put(normalize(key), entry);
            return entry;
        });
        return Map.copyOf(entries);
    }

    /**
     * 语言字典只缓存被引用的键，缺失键通过同一次真实批量查询补全。
     *
     * @param keys 本次使用的语言键。
     * @return 已找到的语言文本。
     */
    public Map<String, String> language(Collection<String> keys) {
        Map<String, String> result = new LinkedHashMap<>();
        lookup.records(WarframeOfficialDataDomain.LANGUAGE_STRING, keys).forEach((key, row) -> {
            OfficialLanguageStringDTO value = (OfficialLanguageStringDTO)row.data();
            String text = english() ? value.getText() : first(value.getZhText(), value.getText());
            if (CharSequenceUtil.isNotBlank(text))
                result.put(key, text);
        });
        return Map.copyOf(result);
    }

    /**
     * 直接读取赏金文件的已启用投影并递归展开候选奖励池。
     *
     * @param poolsBySyndicate 集团到候选池业务键的受管映射。
     * @return 以集团代码组织的候选池。
     */
    private Map<String, List<RewardPool>> readPools(Map<String, String> poolsBySyndicate) {
        if (poolsBySyndicate.isEmpty())
            return Map.of();
        Map<String, List<RewardPool>> result = new LinkedHashMap<>();
        List<String> keys = poolsBySyndicate.values().stream().distinct().toList();
        Map<String, OfficialBountyDTO> byKey = new LinkedHashMap<>();
        lookup.records(WarframeOfficialDataDomain.BOUNTY, keys)
            .forEach((key, row) -> byKey.put(key, (OfficialBountyDTO)row.data()));
        for (Map.Entry<String, String> category : poolsBySyndicate.entrySet()) {
            OfficialBountyDTO bounty = byKey.get(normalize(category.getValue()));
            List<RewardPool> values = new ArrayList<>();
            if (bounty != null && bounty.getStages() != null) {
                for (var stage : bounty.getStages()) {
                    var source = stage.rewardPool();
                    if (source == null)
                        continue;
                    String name = localized(source.getSourceNameZh(), source.getSourceNameEn(), source.getSourceName());
                    List<WarframeBountyPoolRewardDTO> rewards = new ArrayList<>();
                    if (source.getGroups() != null)
                        for (var group : source.getGroups())
                            collectRewards(group, rewards);
                    values.add(new RewardPool(source.getPoolId(), name, List.copyOf(rewards),
                        minimumLevel(source.getSourceNameEn())));
                }
            }
            values.sort(Comparator.comparing(RewardPool::minLevel, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(RewardPool::id, Comparator.nullsLast(String::compareTo)));
            result.put(category.getKey(), List.copyOf(values));
        }
        return result;
    }

    /**
     * 展开当前层级的固定奖励字段及其已建模子分组。
     *
     * @param group 分组。
     * @param result 当前正在组装的业务结果。
     */
    private void collectRewards(OfficialBountyDTO.StagesGroupsGroupsGroupsGroupsDTO group,
        List<WarframeBountyPoolRewardDTO> result) {
        if (group.getRewards() != null)
            for (var item : group.getRewards()) {
                String name = localized(item.getItemNameZh(), item.getItemNameEn(), item.getItemName());
                if (name != null || item.getItemUniqueName() != null)
                    result.add(new WarframeBountyPoolRewardDTO(item.getItemUniqueName(), name, item.getItemCount(),
                        item.getProbability(), item.getItemIconUrl()));
            }
    }

    /**
     * 展开当前层级的固定奖励字段及其已建模子分组。
     *
     * @param group 分组。
     * @param result 当前正在组装的业务结果。
     */
    private void collectRewards(OfficialBountyDTO.StagesGroupsGroupsGroupsDTO group,
        List<WarframeBountyPoolRewardDTO> result) {
        if (group.getGroups() != null)
            for (var child : group.getGroups())
                collectRewards(child, result);
        if (group.getRewards() != null)
            for (var item : group.getRewards()) {
                String name = localized(item.getItemNameZh(), item.getItemNameEn(), item.getItemDisplayName());
                if (name != null || item.getItemUniqueName() != null)
                    result.add(new WarframeBountyPoolRewardDTO(item.getItemUniqueName(), name, item.getItemCount(),
                        item.getProbability(), item.getItemIconUrl()));
            }
    }

    /**
     * 展开当前层级的固定奖励字段及其已建模子分组。
     *
     * @param group 分组。
     * @param result 当前正在组装的业务结果。
     */
    private void collectRewards(OfficialBountyDTO.StagesGroupsGroupsDTO group,
        List<WarframeBountyPoolRewardDTO> result) {
        if (group.getGroups() != null)
            for (var child : group.getGroups())
                collectRewards(child, result);
        if (group.getRewards() != null)
            for (var item : group.getRewards()) {
                String name = localized(item.getItemNameZh(), item.getItemNameEn(), item.getItemDisplayName());
                if (name != null || item.getItemUniqueName() != null)
                    result.add(new WarframeBountyPoolRewardDTO(item.getItemUniqueName(), name, item.getItemCount(),
                        item.getProbability(), item.getItemIconUrl()));
            }
    }

    /**
     * 展开当前层级的固定奖励字段及其已建模子分组。
     *
     * @param group 分组。
     * @param result 当前正在组装的业务结果。
     */
    private void collectRewards(OfficialBountyDTO.StagesGroupsDTO group, List<WarframeBountyPoolRewardDTO> result) {
        if (group.getGroups() != null)
            for (var child : group.getGroups())
                collectRewards(child, result);
        if (group.getRewards() != null)
            for (var item : group.getRewards()) {
                String name = localized(item.getItemNameZh(), item.getItemNameEn(), item.getItemDisplayName());
                if (name != null || item.getItemUniqueName() != null)
                    result.add(new WarframeBountyPoolRewardDTO(item.getItemUniqueName(), name, item.getItemCount(),
                        item.getProbability(), item.getItemIconUrl()));
            }
    }

    /**
     * 从具体官方 DTO 建立代码和名称别名索引。
     *
     * @param codebooks 代码簿。
     * @param type 目标业务类型。
     * @param domain 官方文件领域。
     * @param dtoType 传输对象类型。
     * @param nameKey 名称键。
     * @param name 当前业务对象的名称。
     * @param nameEn 名称英文。
     * @param nameZh 名称中文。
     * @param description 说明。
     * @param batchId 当前冻结导入批次的标识。
     */
    private <T extends OfficialRecordDTO> void addCodebook(WarframeReferenceType requested,
        Map<WarframeReferenceType, Map<String, WarframeWorldStateCodebookEntryDTO>> codebooks,
        WarframeReferenceType type, WarframeOfficialDataDomain domain, Class<T> dtoType, Function<T, String> nameKey,
        Function<T, String> name, Function<T, String> nameEn, Function<T, String> nameZh,
        Function<T, String> description, String batchId) {
        if (requested != type)
            return;
        Map<String, WarframeWorldStateCodebookEntryDTO> entries =
            cache.snapshot(WarframeDatasetCacheNames.official(domain), "codebook",
                () -> buildCodebook(domain, dtoType, nameKey, name, nameEn, nameZh, description, batchId));
        codebooks.put(type, entries);
    }

    /**
     * 将当前小代码表转换为名称入口，不保留原始 DTO。
     *
     * @param domain 独立领域。
     * @param dtoType DTO 类型。
     * @param nameKey 语言键提取器。
     * @param name 名称提取器。
     * @param nameEn 英文名称提取器。
     * @param nameZh 中文名称提取器。
     * @param description 描述提取器。
     * @param batchId 资料来源标识。
     * @return 不可变名称入口。
     * @param <T> 领域 DTO 类型。
     */
    private <T extends OfficialRecordDTO> Map<String, WarframeWorldStateCodebookEntryDTO> buildCodebook(
        WarframeOfficialDataDomain domain, Class<T> dtoType, Function<T, String> nameKey, Function<T, String> name,
        Function<T, String> nameEn, Function<T, String> nameZh, Function<T, String> description, String batchId) {
        Map<String, WarframeWorldStateCodebookEntryDTO> entries = new LinkedHashMap<>();
        Map<String, Set<String>> aliases = new LinkedHashMap<>();
        String source = "dataset:" + domain.domainCode();
        readUncached(domain, dtoType, (key, value) -> {
            String localizedName = Stream
                .of(localized(nameZh.apply(value), nameEn.apply(value), name.apply(value)), nameEn.apply(value),
                    name.apply(value), nameZh.apply(value))
                .filter(WarframeNameNormalizer::isReadableDisplayName).findFirst().orElse(null);
            String languageKey = nameKey.apply(value);
            // 保留无译名的精确身份，防止它被另一条记录的别名覆盖。
            WarframeWorldStateCodebookEntryDTO entry =
                new WarframeWorldStateCodebookEntryDTO(key, localizedName == null ? key : localizedName,
                    Objects.toString(description.apply(value), ""), "", source, localizedName == null
                        ? WarframeReferenceResolutionStatus.UNRESOLVED : WarframeReferenceResolutionStatus.RESOLVED,
                    "", false, languageKey, List.of());
            entries.put(normalize(key), entry);
            for (String alias : Stream.of(name.apply(value), nameEn.apply(value), nameKey.apply(value)).toList()) {
                if (CharSequenceUtil.isNotBlank(alias))
                    aliases.computeIfAbsent(normalize(alias), ignored -> new LinkedHashSet<>()).add(key);
            }
            return entry;
        });
        // 先登记完整业务键，再处理别名，确保同名和查询顺序不会改变确切身份。
        aliases.forEach((key, candidates) -> {
            if (candidates.size() == 1) {
                entries.putIfAbsent(key, entries.get(normalize(candidates.iterator().next())));
            } else {
                entries.putIfAbsent(key, new WarframeWorldStateCodebookEntryDTO(key, key, "", "", source,
                    WarframeReferenceResolutionStatus.AMBIGUOUS, "", false, "", candidates.stream().sorted().toList()));
            }
        });
        return Map.copyOf(entries);
    }

    /**
     * 在独立数据集内构建轻量投影，完成后释放完整 DTO 树。
     *
     * @param domain 独立领域。
     * @param dtoType DTO 类型。
     * @param mapper 投影转换器。
     * @return 不可变投影。
     * @param <T> 领域 DTO 类型。
     * @param <R> 投影类型。
     */
    private <T extends OfficialRecordDTO, R> Map<String, R> readUncached(WarframeOfficialDataDomain domain,
        Class<T> dtoType, RecordMapper<T, R> mapper) {
        Map<String, R> result = new LinkedHashMap<>();
        for (var row : catalog.records(domain)) {
            T value = dtoType.cast(row.data());
            R mapped = mapper.map(row.bkey(), value);
            if (mapped != null)
                result.put(row.bkey(), mapped);
        }
        return Map.copyOf(result);
    }

    /**
     * 根据配置选择中文、英文及缺省文本。
     *
     * @param zh 中文。
     * @param en 英文。
     * @param fallback 缺省。
     * @return 本地化对应的文本。
     */
    private String localized(String zh, String en, String fallback) {
        return english() ? first(en, fallback, zh) : first(zh, en, fallback);
    }

    /**
     * 返回首个非空文本。
     *
     * @param values 待处理的值集合。
     * @return 按约定优先顺序取得的首个有效文本；均缺省时为空。
     */
    private static String first(String... values) {
        for (String value : values)
            if (CharSequenceUtil.isNotBlank(value))
                return value;
        return null;
    }

    /**
     * 将引用代码规范化为可比对的索引键。
     *
     * @param value 待校验或规范化的文本。
     * @return 规范化后的文本，供比较或查找使用。
     */
    private static String normalize(String value) {
        return CharSequenceUtil.trim(value).toLowerCase(Locale.ROOT);
    }

    /**
     * 从奖励池标识末尾提取排序号。
     *
     * @param value 待校验或规范化的文本。
     * @return 奖励池标识末尾的排序号；未提供有效排序号时返回最大整数。
     */
    private static Integer minimumLevel(String value) {
        if (value == null)
            return null;
        var match = BOUNTY_LEVEL.matcher(value);
        return match.find() ? Integer.valueOf(match.group(1)) : null;
    }

    /**
     * 判断当前代码簿是否使用英文。
     *
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    private boolean english() {
        return "en".equalsIgnoreCase(codebookProperties.getLocale());
    }

    /** 将具体文件 DTO 映射为代码簿条目的内部函数。 */
    @FunctionalInterface
    private interface RecordMapper<T, R> {
        /**
         * 根据记录身份和具体字段构建条目。
         *
         * @param key 当前记录或字段的查找键。
         * @param value 待处理的字段值。
         * @return 响应对象。
         */
        R map(String key, T value);
    }

    /**
     * 已采纳静态资料的不可变代码簿快照。
     *
     * @param nodes 星图节点索引
     * @param challenges 挑战定义索引
     * @param pools 辛迪加候选奖励池
     */
    public record Snapshot(Map<String, Reference> nodes, Map<String, Reference> challenges,
        Map<String, List<RewardPool>> pools) {
        /**
         * 检查星图和挑战是否足以支撑动态数据补全。
         *
         * @return 满足本方法说明的业务条件时返回真，否则返回假。
         */
        public boolean available() {
            return CollUtil.isNotEmpty(nodes) && CollUtil.isNotEmpty(challenges);
        }
    }

    /**
     * 解析动态引用所需的官方静态字段。
     *
     * @param key 原始引用键
     * @param nameZh 中文名称
     * @param nameEn 英文名称
     * @param descriptionZh 中文说明
     * @param missionType 已翻译任务类型
     * @param systemName 所属星球或区域
     * @param missionTypeKey 任务类型原始代码
     */
    public record Reference(String key, String nameZh, String nameEn, String descriptionZh, String missionType,
        String systemName, String missionTypeKey) {
        /**
         * 优先返回中文名称，缺少时使用英文名称。
         *
         * @return 展示名称对应的文本。
         */
        public String displayName() {
            return CharSequenceUtil.isBlank(nameZh) ? nameEn : nameZh;
        }
    }

    /**
     * 一个静态候选奖励池，不代表本轮必然奖励。
     *
     * @param id 奖励池标识
     * @param name 奖励池名称
     * @param rewards 候选奖励名称
     * @param minLevel 静态定义的最低敌人等级；未知时为空
     */
    public record RewardPool(String id, String name, List<WarframeBountyPoolRewardDTO> rewards, Integer minLevel) {
    }
}
