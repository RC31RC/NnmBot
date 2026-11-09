package com.nnm.bot.warframe.persistent.official.cache.worldstate;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.config.cache.WarframeDatasetCache;
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
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.OfficialFactionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.OfficialFissureModifierDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.OfficialMissionTypeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.OfficialSyndicateDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.OfficialUpgradeTypeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.WarframeWorldStateCodebookEntryDTO;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialLookupService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

/** 通过统一类型化查询构建不可变 WorldState 静态代码簿。 */
@Component
public class WarframeOfficialWorldStateIndex {
    /** 需要动态补全的辛迪加及官方静态奖励分区。 */
    private static final Map<String, String> REWARD_CATEGORIES = Map.of("HexSyndicate", "Hex Bounty Rewards",
        "ZarimanSyndicate", "Zariman Bounty Rewards", "EntratiLabSyndicate", "Albrecht's Laboratories Bounty Rewards");

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
     * @param syndicates 本次动态数据中的集团引用。
     * @return 请求范围内的星图、挑战和候选奖励池。
     */
    public Snapshot current(Collection<String> syndicates) {
        return load(syndicates);
    }

    /**
     * 加载星图、挑战、赏金、语言和官方代码簿并一次生成新快照。
     *
     * @param batchId 当前冻结导入批次的标识。
     * @return 快照对象。
     */
    private Snapshot load(Collection<String> syndicates) {
        Map<String, Reference> nodes = nodes();
        Map<String, Reference> challengeReferences =
            read(WarframeOfficialDataDomain.CHALLENGE, OfficialChallengeDTO.class, (key, value) -> {
                String description = first(value.getDescriptionZh(), value.getDescriptionEn(), value.getDescription());
                if (description != null && value.getRequiredCount() != null) {
                    description =
                        description.replace("|COUNT|", value.getRequiredCount().stripTrailingZeros().toPlainString());
                }
                return new Reference(key, first(value.getNameZh(), value.getName()),
                    first(value.getNameEn(), value.getName()), description, null, null, null);
            });

        return new Snapshot(Map.copyOf(nodes), Map.copyOf(challengeReferences), readPools(syndicates));
    }

    /** @return 仅属于星图表的轻量节点快照。 */
    private Map<String, Reference> nodes() {
        return read(WarframeOfficialDataDomain.REGION, OfficialRegionDTO.class,
            (key, value) -> new Reference(key, first(value.getZhName(), value.getName(), value.getNameEn()),
                first(value.getNameEn(), value.getName()), null,
                localized(value.getMissionTypeNameZh(), value.getMissionTypeName(), value.getMissionType()),
                localized(value.getZhSystemName(), value.getSystemName(), null), value.getMissionType()));
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
            OfficialSyndicateDTO.class, value -> null, OfficialSyndicateDTO::getName, OfficialSyndicateDTO::getNameEn,
            OfficialSyndicateDTO::getNameZh, value -> null, batchId);
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
            String name = localized(value.getZhName(), value.getNameEn(), value.getName());
            String system = localized(value.getZhSystemName(), value.getSystemName(), null);
            var entry = new WarframeWorldStateCodebookEntryDTO(key, name + (system == null ? "" : "（" + system + "）"),
                "", value.getMissionType(), "dataset:REGION", WarframeReferenceResolutionStatus.RESOLVED,
                value.getFaction(), false);
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
            if (StrUtil.isNotBlank(text))
                result.put(key, text);
        });
        return Map.copyOf(result);
    }

    /**
     * 直接读取赏金文件的已启用投影并递归展开候选奖励池。
     *
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private Map<String, List<RewardPool>> readPools(Collection<String> syndicates) {
        Map<String, List<RewardPool>> result = new LinkedHashMap<>();
        List<String> keys = syndicates.stream().filter(REWARD_CATEGORIES::containsKey)
            .map(key -> "rewardPools/" + REWARD_CATEGORIES.get(key)).distinct().toList();
        Map<String, OfficialBountyDTO> byKey = new LinkedHashMap<>();
        lookup.records(WarframeOfficialDataDomain.BOUNTY, keys)
            .forEach((key, row) -> byKey.put(key, (OfficialBountyDTO)row.data()));
        for (Map.Entry<String, String> category : REWARD_CATEGORIES.entrySet()) {
            if (!syndicates.contains(category.getKey()))
                continue;
            OfficialBountyDTO bounty = byKey.get(normalize("rewardPools/" + category.getValue()));
            List<RewardPool> values = new ArrayList<>();
            if (bounty != null && bounty.getStages() != null) {
                for (var stage : bounty.getStages()) {
                    var source = stage.rewardPool();
                    if (source == null)
                        continue;
                    String name = localized(source.getSourceNameZh(), source.getSourceNameEn(), source.getSourceName());
                    List<String> rewards = new ArrayList<>();
                    if (source.getGroups() != null)
                        for (var group : source.getGroups())
                            collectRewards(group, rewards);
                    values.add(new RewardPool(source.getPoolId(), name, List.copyOf(rewards),
                        trailingOrder(source.getPoolId())));
                }
            }
            values.sort(Comparator.comparingInt(RewardPool::order).thenComparing(RewardPool::id));
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
    private void collectRewards(OfficialBountyDTO.StagesGroupsGroupsGroupsGroupsDTO group, List<String> result) {
        if (group.getRewards() != null)
            for (var item : group.getRewards()) {
                String name = localized(item.getItemNameZh(), item.getItemNameEn(), item.getItemName());
                if (name != null)
                    result.add(name);
            }
    }

    /**
     * 展开当前层级的固定奖励字段及其已建模子分组。
     *
     * @param group 分组。
     * @param result 当前正在组装的业务结果。
     */
    private void collectRewards(OfficialBountyDTO.StagesGroupsGroupsGroupsDTO group, List<String> result) {
        if (group.getGroups() != null)
            for (var child : group.getGroups())
                collectRewards(child, result);
        if (group.getRewards() != null)
            for (var item : group.getRewards()) {
                String name = localized(item.getItemNameZh(), item.getItemNameEn(), item.getItemName());
                if (name != null)
                    result.add(name);
            }
    }

    /**
     * 展开当前层级的固定奖励字段及其已建模子分组。
     *
     * @param group 分组。
     * @param result 当前正在组装的业务结果。
     */
    private void collectRewards(OfficialBountyDTO.StagesGroupsGroupsDTO group, List<String> result) {
        if (group.getGroups() != null)
            for (var child : group.getGroups())
                collectRewards(child, result);
        if (group.getRewards() != null)
            for (var item : group.getRewards()) {
                String name = localized(item.getItemNameZh(), item.getItemNameEn(), item.getItemName());
                if (name != null)
                    result.add(name);
            }
    }

    /**
     * 展开当前层级的固定奖励字段及其已建模子分组。
     *
     * @param group 分组。
     * @param result 当前正在组装的业务结果。
     */
    private void collectRewards(OfficialBountyDTO.StagesGroupsDTO group, List<String> result) {
        if (group.getGroups() != null)
            for (var child : group.getGroups())
                collectRewards(child, result);
        if (group.getRewards() != null)
            for (var item : group.getRewards()) {
                String name = localized(item.getItemNameZh(), item.getItemNameEn(), item.getItemName());
                if (name != null)
                    result.add(name);
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
        readUncached(domain, dtoType, (key, value) -> {
            String localizedName = localized(nameZh.apply(value), nameEn.apply(value), name.apply(value));
            if (StrUtil.isBlank(localizedName) || localizedName.startsWith("/"))
                return null;
            WarframeWorldStateCodebookEntryDTO entry = new WarframeWorldStateCodebookEntryDTO(key, localizedName,
                Objects.toString(description.apply(value), ""), "", "dataset:" + domain.domainCode(),
                WarframeReferenceResolutionStatus.RESOLVED, "", false);
            entries.put(normalize(key), entry);
            for (String alias : Stream.of(name.apply(value), nameEn.apply(value), nameKey.apply(value)).toList()) {
                if (StrUtil.isNotBlank(alias))
                    entries.putIfAbsent(normalize(alias), entry);
            }
            return entry;
        });
        return Map.copyOf(entries);
    }

    /**
     * 消费具体 Repository 的非受管投影，优先采用同身份的自维护记录。
     *
     * @param domain 官方文件领域。
     * @param dtoType 传输对象类型。
     * @param mapper 组件已经配置的 Jackson 映射器。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private <T extends OfficialRecordDTO, R> Map<String, R> read(WarframeOfficialDataDomain domain, Class<T> dtoType,
        RecordMapper<T, R> mapper) {
        return cache.snapshot(WarframeDatasetCacheNames.official(domain), WarframeDatasetCacheNames.REFERENCES,
            () -> readUncached(domain, dtoType, mapper));
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
            if (StrUtil.isNotBlank(value))
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
        return value.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * 从奖励池标识末尾提取排序号。
     *
     * @param value 待校验或规范化的文本。
     * @return 奖励池标识末尾的排序号；未提供有效排序号时返回最大整数。
     */
    private static int trailingOrder(String value) {
        if (value == null)
            return Integer.MAX_VALUE;
        int separator = value.lastIndexOf(':');
        try {
            return separator < 0 ? Integer.MAX_VALUE : Integer.parseInt(value.substring(separator + 1));
        } catch (NumberFormatException exception) {
            return Integer.MAX_VALUE;
        }
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
            return StrUtil.isBlank(nameZh) ? nameEn : nameZh;
        }
    }

    /**
     * 一个静态候选奖励池，不代表本轮必然奖励。
     *
     * @param id 奖励池标识
     * @param name 奖励池名称
     * @param rewards 候选奖励名称
     * @param order 排序号
     */
    public record RewardPool(String id, String name, List<String> rewards, int order) {
    }
}
