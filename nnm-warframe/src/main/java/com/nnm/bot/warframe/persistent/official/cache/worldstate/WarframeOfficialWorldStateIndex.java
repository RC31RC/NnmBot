package com.nnm.bot.warframe.persistent.official.cache.worldstate;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import com.nnm.bot.warframe.persistent.official.config.worldstate.WarframeWorldStateDataProperties;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceResolutionStatus;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceType;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.MaintenanceType;
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
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.concurrent.locks.ReentrantLock;

/** 通过统一类型化查询构建不可变 WorldState 静态代码簿。 */
@Component
public class WarframeOfficialWorldStateIndex {
    private final ReentrantLock reloadLock = new ReentrantLock();

    /** 需要动态补全的辛迪加及官方静态奖励分区。 */
    private static final Map<String, String> REWARD_CATEGORIES = Map.of(
            "HexSyndicate", "Hex Bounty Rewards",
            "ZarimanSyndicate", "Zariman Bounty Rewards",
            "EntratiLabSyndicate", "Albrecht's Laboratories Bounty Rewards");

    /** 统一读取自维护优先的类型化官方记录。 */
    @Resource
    private WarframeOfficialRecordReadService catalog;

    /** 代码簿语言和刷新参数。 */
    @Resource
    private WarframeWorldStateDataProperties codebookProperties;

    /** 最近一次完整构建的不可变代码簿。 */
    private volatile Snapshot snapshot = Snapshot.empty();

    /** 当前实例是否已完成首次代码簿加载。 */
    private volatile boolean initialized;

    /**
     * 读取当前代码簿，首次使用时延迟加载，已初始化后仅访问本地快照。
     *
     * @return 快照对象。
     */
    public Snapshot snapshot() {
        return initialized ? snapshot : current();
    }

    /**
     * 检查当前官方批次并按需重建；首次加载也读取尚无官方批次的自维护数据。
     *
     * @return 当前完整的不可变代码簿。
     */
    public Snapshot current() {
        reloadLock.lock();
        try {
            String batchId = catalog.currentBatchId().orElse("");
            if (!initialized || !batchId.equals(snapshot.batchId())) {
                snapshot = load(batchId);
                initialized = true;
            }
            return snapshot;
        } finally {
            reloadLock.unlock();
        }
    }

    /** 自维护记录可在批次不变时修改；主动重建代码簿以反映最新维护结果。 */
    public void reload() {
        reloadLock.lock();
        try {
            snapshot = load(catalog.currentBatchId().orElse(""));
            initialized = true;
        } finally {
            reloadLock.unlock();
        }
    }

    /**
     * 加载星图、挑战、赏金、语言和官方代码簿并一次生成新快照。
     *
     * @param batchId 当前冻结导入批次的标识。
     * @return 快照对象。
     */
    private Snapshot load(String batchId) {
        Map<String, Reference> nodes = read(WarframeOfficialDataDomain.REGION, OfficialRegionDTO.class,
                (key, value) -> new Reference(key, first(value.getZhName(), value.getName(), value.getNameEn()),
                        first(value.getNameEn(), value.getName()), null,
                        localized(value.getMissionTypeNameZh(), value.getMissionTypeName(), value.getMissionType()),
                        localized(value.getZhSystemName(), value.getSystemName(), null), value.getMissionType()));
        Map<String, Reference> challengeReferences = read(WarframeOfficialDataDomain.CHALLENGE, OfficialChallengeDTO.class,
                (key, value) -> {
                    String description = first(value.getDescriptionZh(), value.getDescriptionEn(), value.getDescription());
                    if (description != null && value.getRequiredCount() != null) {
                        description = description.replace("|COUNT|", value.getRequiredCount().stripTrailingZeros().toPlainString());
                    }
                    return new Reference(key, first(value.getNameZh(), value.getName()),
                            first(value.getNameEn(), value.getName()), description, null, null, null);
                });

        Map<String, List<RewardPool>> pools = readPools();
        Map<WarframeReferenceType, Map<String, WarframeWorldStateCodebookEntryDTO>> codebooks = new EnumMap<>(WarframeReferenceType.class);
        addCodebook(codebooks, WarframeReferenceType.MISSION_TYPE, WarframeOfficialDataDomain.MISSION_TYPE, OfficialMissionTypeDTO.class,
                OfficialMissionTypeDTO::getNameKey, OfficialMissionTypeDTO::getName,
                OfficialMissionTypeDTO::getNameEn, OfficialMissionTypeDTO::getNameZh, value -> null, batchId);
        addCodebook(codebooks, WarframeReferenceType.FACTION, WarframeOfficialDataDomain.FACTION, OfficialFactionDTO.class,
                OfficialFactionDTO::getNameKey, OfficialFactionDTO::getName,
                OfficialFactionDTO::getNameEn, OfficialFactionDTO::getNameZh, value -> null, batchId);
        addCodebook(codebooks, WarframeReferenceType.FISSURE_TIER, WarframeOfficialDataDomain.FISSURE_MODIFIER, OfficialFissureModifierDTO.class,
                value -> null, OfficialFissureModifierDTO::getName,
                OfficialFissureModifierDTO::getNameEn, OfficialFissureModifierDTO::getNameZh, value -> null, batchId);
        addCodebook(codebooks, WarframeReferenceType.SORTIE_BOSS, WarframeOfficialDataDomain.BOSS, OfficialBossDTO.class,
                OfficialBossDTO::getNameKey, OfficialBossDTO::getName,
                OfficialBossDTO::getNameEn, OfficialBossDTO::getNameZh, value -> null, batchId);
        addCodebook(codebooks, WarframeReferenceType.SYNDICATE, WarframeOfficialDataDomain.SYNDICATE, OfficialSyndicateDTO.class,
                value -> null, OfficialSyndicateDTO::getName,
                OfficialSyndicateDTO::getNameEn, OfficialSyndicateDTO::getNameZh, value -> null, batchId);
        addCodebook(codebooks, WarframeReferenceType.UPGRADE_TYPE, WarframeOfficialDataDomain.UPGRADE_TYPE, OfficialUpgradeTypeDTO.class,
                value -> null, OfficialUpgradeTypeDTO::getName,
                OfficialUpgradeTypeDTO::getNameEn, OfficialUpgradeTypeDTO::getNameZh, value -> null, batchId);

        Map<String, WarframeWorldStateCodebookEntryDTO> nodeEntries = new LinkedHashMap<>();
        nodes.forEach((key, node) -> nodeEntries.put(normalize(key), new WarframeWorldStateCodebookEntryDTO(
                key, localized(node.nameZh(), node.nameEn(), node.nameEn())
                + (node.systemName() == null ? "" : "（" + node.systemName() + "）"), "", node.missionType(),
                referenceSource(batchId), WarframeReferenceResolutionStatus.RESOLVED, "", false)));
        codebooks.put(WarframeReferenceType.NODE, Map.copyOf(nodeEntries));

        Map<String, String> language = read(WarframeOfficialDataDomain.LANGUAGE_STRING, OfficialLanguageStringDTO.class,
                (key, value) -> english() ? value.getText() : first(value.getZhText(), value.getText()))
                .entrySet().stream().filter(entry -> entry.getValue() != null)
                .collect(LinkedHashMap::new, (map, entry) -> map.put(normalize(entry.getKey()), entry.getValue()), Map::putAll);
        return new Snapshot(batchId, Map.copyOf(nodes), Map.copyOf(challengeReferences), Map.copyOf(pools),
                Map.copyOf(codebooks), Map.copyOf(language));
    }

    /**
     * 直接读取赏金文件的已启用投影并递归展开候选奖励池。
     *
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private Map<String, List<RewardPool>> readPools() {
        Map<String, List<RewardPool>> result = new LinkedHashMap<>();
        Map<String, OfficialBountyDTO> byKey = read(WarframeOfficialDataDomain.BOUNTY, OfficialBountyDTO.class,
                (key, value) -> value);
        for (Map.Entry<String, String> category : REWARD_CATEGORIES.entrySet()) {
            OfficialBountyDTO bounty = byKey.get("rewardPools/" + category.getValue());
            List<RewardPool> values = new ArrayList<>();
            if (bounty != null && bounty.getStages() != null) {
                for (var stage : bounty.getStages()) {
                    var source = stage.rewardPool();
                    if (source == null) continue;
                    String name = localized(source.getSourceNameZh(), source.getSourceNameEn(), source.getSourceName());
                    List<String> rewards = new ArrayList<>();
                    if (source.getGroups() != null) for (var group : source.getGroups()) collectRewards(group, rewards);
                    values.add(new RewardPool(source.getPoolId(), name, List.copyOf(rewards), trailingOrder(source.getPoolId())));
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
        if (group.getRewards() != null) for (var item : group.getRewards()) {
            String name = localized(item.getItemNameZh(), item.getItemNameEn(), item.getItemName());
            if (name != null) result.add(name);
        }
    }

    /**
     * 展开当前层级的固定奖励字段及其已建模子分组。
     *
     * @param group 分组。
     * @param result 当前正在组装的业务结果。
     */
    private void collectRewards(OfficialBountyDTO.StagesGroupsGroupsGroupsDTO group, List<String> result) {
        if (group.getGroups() != null) for (var child : group.getGroups()) collectRewards(child, result);
        if (group.getRewards() != null) for (var item : group.getRewards()) {
            String name = localized(item.getItemNameZh(), item.getItemNameEn(), item.getItemName());
            if (name != null) result.add(name);
        }
    }

    /**
     * 展开当前层级的固定奖励字段及其已建模子分组。
     *
     * @param group 分组。
     * @param result 当前正在组装的业务结果。
     */
    private void collectRewards(OfficialBountyDTO.StagesGroupsGroupsDTO group, List<String> result) {
        if (group.getGroups() != null) for (var child : group.getGroups()) collectRewards(child, result);
        if (group.getRewards() != null) for (var item : group.getRewards()) {
            String name = localized(item.getItemNameZh(), item.getItemNameEn(), item.getItemName());
            if (name != null) result.add(name);
        }
    }

    /**
     * 展开当前层级的固定奖励字段及其已建模子分组。
     *
     * @param group 分组。
     * @param result 当前正在组装的业务结果。
     */
    private void collectRewards(OfficialBountyDTO.StagesGroupsDTO group, List<String> result) {
        if (group.getGroups() != null) for (var child : group.getGroups()) collectRewards(child, result);
        if (group.getRewards() != null) for (var item : group.getRewards()) {
            String name = localized(item.getItemNameZh(), item.getItemNameEn(), item.getItemName());
            if (name != null) result.add(name);
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
    private <T extends OfficialRecordDTO> void addCodebook(
            Map<WarframeReferenceType, Map<String, WarframeWorldStateCodebookEntryDTO>> codebooks,
            WarframeReferenceType type, WarframeOfficialDataDomain domain, Class<T> dtoType,
            Function<T, String> nameKey, Function<T, String> name,
            Function<T, String> nameEn, Function<T, String> nameZh,
            Function<T, String> description, String batchId) {
        Map<String, WarframeWorldStateCodebookEntryDTO> entries = new LinkedHashMap<>();
        read(domain, dtoType, (key, value) -> {
            String localizedName = localized(nameZh.apply(value), nameEn.apply(value), name.apply(value));
            if (StrUtil.isBlank(localizedName) || localizedName.startsWith("/")) return null;
            WarframeWorldStateCodebookEntryDTO entry = new WarframeWorldStateCodebookEntryDTO(
                    key, localizedName, Objects.toString(description.apply(value), ""), "", referenceSource(batchId),
                    WarframeReferenceResolutionStatus.RESOLVED, "", false);
            entries.put(normalize(key), entry);
            for (String alias : Stream.of(
                    name.apply(value), nameEn.apply(value), nameKey.apply(value)).toList()) {
                if (StrUtil.isNotBlank(alias)) entries.putIfAbsent(normalize(alias), entry);
            }
            return entry;
        });
        codebooks.put(type, Map.copyOf(entries));
    }

    /**
     * 消费具体 Repository 的非受管投影，优先采用同身份的自维护记录。
     *
     * @param domain 官方文件领域。
     * @param dtoType 传输对象类型。
     * @param mapper 组件已经配置的 Jackson 映射器。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private <T extends OfficialRecordDTO, R> Map<String, R> read(
            WarframeOfficialDataDomain domain, Class<T> dtoType, RecordMapper<T, R> mapper) {
        Map<String, R> result = new LinkedHashMap<>();
        for (var row : catalog.records(domain)) {
            T value = dtoType.cast(row.data());
            R mapped = mapper.map(row.bkey(), value);
            if (mapped != null) result.put(row.bkey(), mapped);
        }
        return result;
    }

    /** 无官方批次时，引用资料仍可完全由自维护记录提供。 */
    public static String referenceSource(String batchId) {
        return StrUtil.isBlank(batchId) ? MaintenanceType.SELF_MAINTAINED.name() : "dataset:" + batchId;
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
        for (String value : values) if (StrUtil.isNotBlank(value)) return value;
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
        if (value == null) return Integer.MAX_VALUE;
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
     * @param batchId 官方批次
     * @param nodes 星图节点索引
     * @param challenges 挑战定义索引
     * @param pools 辛迪加候选奖励池
     * @param codebooks 各类别的官方引用代码簿
     * @param language 官方语言键和值
     */
    public record Snapshot(String batchId, Map<String, Reference> nodes,
                           Map<String, Reference> challenges, Map<String, List<RewardPool>> pools,
                           Map<WarframeReferenceType, Map<String, WarframeWorldStateCodebookEntryDTO>> codebooks,
                           Map<String, String> language) {
        /**
         * 构造未采纳官方数据时的空代码簿。
         *
         * @return 快照对象。
         */
        public static Snapshot empty() {
            return new Snapshot("", Map.of(), Map.of(), Map.of(), Map.of(), Map.of());
        }

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
     * @param key 原始引用键
     * @param nameZh 中文名称
     * @param nameEn 英文名称
     * @param descriptionZh 中文说明
     * @param missionType 已翻译任务类型
     * @param systemName 所属星球或区域
     * @param missionTypeKey 任务类型原始代码
     */
    public record Reference(String key, String nameZh, String nameEn, String descriptionZh,
                            String missionType, String systemName, String missionTypeKey) {
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
     * @param id 奖励池标识
     * @param name 奖励池名称
     * @param rewards 候选奖励名称
     * @param order 排序号
     */
    public record RewardPool(String id, String name, List<String> rewards, int order) {
    }
}
