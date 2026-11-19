package com.nnm.bot.warframe.persistent.curated.service.reference.impl;

import com.nnm.bot.warframe.runtime.cache.dataset.WarframeDatasetCache;
import com.nnm.bot.warframe.constant.WarframeDatasetCacheNames;
import com.nnm.bot.warframe.persistent.curated.constant.WarframeCommunityDataset;
import com.nnm.bot.warframe.persistent.curated.constant.WarframeCommunityTermGroup;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityNameIndexDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO.*;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityTermIndexDTO;
import com.nnm.bot.warframe.persistent.curated.repository.reference.WarframeCommunityAliasRepository;
import com.nnm.bot.warframe.persistent.curated.repository.reference.WarframeCommunityArbitrationEssenceRepository;
import com.nnm.bot.warframe.persistent.curated.repository.reference.WarframeCommunityRivenAliasRepository;
import com.nnm.bot.warframe.persistent.curated.repository.reference.WarframeCommunityTermRepository;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeCommunityCatalogService;
import com.nnm.bot.warframe.normalization.WarframeNameNormalizer;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.stereotype.Service;

@Service
public class WarframeCommunityCatalogServiceImpl implements WarframeCommunityCatalogService {
    @Resource
    private WarframeDatasetCache cache;
    @Resource
    private WarframeCommunityAliasRepository aliases;
    @Resource
    private WarframeCommunityTermRepository terms;
    @Resource
    private WarframeCommunityArbitrationEssenceRepository essence;
    @Resource
    private WarframeCommunityRivenAliasRepository rivenAliases;

    /**
     * 读取社区人工维护的物品别名。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    public List<WarframeCommunityRecordDTO<AliasDTO>> aliases() {
        return aliasesIndex().records();
    }

    /** @return 当前社区表独立的不可变名称跳板。 */
    private WarframeCommunityNameIndexDTO<AliasDTO> aliasesIndex() {
        return cache.snapshot(WarframeDatasetCacheNames.community(WarframeCommunityDataset.ALIAS),
            WarframeDatasetCacheNames.CATALOG,
            () -> index(
                aliases.findAllProjectedByOrderByRecordKeyAsc().stream()
                    .map(row -> new WarframeCommunityRecordDTO<>(row.getId(), row.getRecordKey(),
                        new AliasDTO(row.getAliasName(), row.getTargetName())))
                    .toList(),
                row -> row.aliasName(), row -> row.targetName()));
    }

    /**
     * 读取社区人工维护的术语及其中文解释。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    public List<WarframeCommunityRecordDTO<TermDTO>> terms() {
        return termsIndex().records();
    }

    /** @return 当前社区表独立的不可变名称跳板。 */
    private WarframeCommunityTermIndexDTO termsIndex() {
        return cache.snapshot(WarframeDatasetCacheNames.community(WarframeCommunityDataset.TERM),
            WarframeDatasetCacheNames.CATALOG, () -> {
                var records = terms.findAllProjectedByOrderByRecordKeyAsc().stream()
                    .map(row -> new WarframeCommunityRecordDTO<>(row.getId(), row.getRecordKey(),
                        new TermDTO(row.getTermGroup(), row.getEnglishText(), row.getChineseText())))
                    .toList();
                var ordinary = index(records,
                    row -> WarframeCommunityTermGroup.displayOnly(row.termGroup()) ? null : row.chineseText(),
                    row -> row.englishText());
                Map<WarframeCommunityTermGroup, Map<String, List<String>>> grouped =
                    new EnumMap<>(WarframeCommunityTermGroup.class);
                for (var group : WarframeCommunityTermGroup.values()) {
                    Map<String, List<String>> names = new LinkedHashMap<>();
                    for (var row : records) {
                        var term = row.details();
                        if (!group.code().equals(term.termGroup()) || term.chineseText() == null
                            || term.chineseText().isBlank())
                            continue;
                        String key = WarframeNameNormalizer.phrase(term.englishText());
                        if (!key.isBlank())
                            names.computeIfAbsent(key, ignored -> new ArrayList<>()).add(term.chineseText());
                    }
                    Map<String, List<String>> frozen = new LinkedHashMap<>();
                    names.forEach((key, values) -> frozen.put(key, values.stream().distinct().toList()));
                    grouped.put(group, Map.copyOf(frozen));
                }
                return new WarframeCommunityTermIndexDTO(records, ordinary.targets(), Map.copyOf(grouped));
            });
    }

    /**
     * 在社区术语自身的缓存代际内建立分组精确索引，普通物品索引不包含它。
     *
     * @param group 明确业务用途的分组。
     * @return 英文整词到去重中文候选。
     */
    @Override
    public Map<String, List<String>> displayTerms(WarframeCommunityTermGroup group) {
        return termsIndex().displayTerms().getOrDefault(group, Map.of());
    }

    /**
     * 读取社区人工维护的仲裁节点精华资料。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    public List<WarframeCommunityRecordDTO<EssenceDTO>> arbitrationEssences() {
        return cache.snapshot(WarframeDatasetCacheNames.community(WarframeCommunityDataset.ARBITRATION_ESSENCE),
            WarframeDatasetCacheNames.CATALOG,
            () -> essence.findAllProjectedByOrderByRecordKeyAsc().stream()
                .map(row -> new WarframeCommunityRecordDTO<>(row.getId(), row.getRecordKey(),
                    new EssenceDTO(row.getNodeName(), row.getEssenceAmount())))
                .toList());
    }

    /**
     * 读取社区人工维护的紫卡属性及组合别名。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    public List<WarframeCommunityRecordDTO<RivenAliasDTO>> rivenAliases() {
        return cache.snapshot(WarframeDatasetCacheNames.community(WarframeCommunityDataset.RIVEN_ALIAS),
            WarframeDatasetCacheNames.CATALOG,
            () -> rivenAliases.findAllProjectedByOrderByRecordKeyAsc().stream()
                .map(row -> new WarframeCommunityRecordDTO<>(row.getId(), row.getRecordKey(),
                    new RivenAliasDTO(row.getAttributeCode(), row.getAliasName())))
                .toList());
    }

    /** @return 社区别名的名称跳板。 */
    @Override
    public Map<String, List<String>> aliasTargets() {
        return aliasesIndex().targets();
    }

    /** @return 社区术语的名称跳板。 */
    @Override
    public Map<String, List<String>> termTargets() {
        return termsIndex().targets();
    }

    /**
     * 社区名称索引只记录目标名称，不解析或复制官方对象。
     *
     * @param rows 社区表投影。
     * @param name 名称入口。
     * @param target 目标名称。
     * @return 本表不可变快照，空表也是一次成功加载。
     * @param <T> 社区详情类型。
     */
    private static <T> WarframeCommunityNameIndexDTO<T> index(List<WarframeCommunityRecordDTO<T>> rows,
        Function<T, String> name, Function<T, String> target) {
        Map<String, List<String>> targets = new LinkedHashMap<>();
        for (var row : rows) {
            String key = WarframeNameNormalizer.item(name.apply(row.details()));
            String value = target.apply(row.details());
            if (key.isBlank() || value == null || value.isBlank())
                continue;
            targets.computeIfAbsent(key, ignored -> new ArrayList<>()).add(value);
        }
        Map<String, List<String>> frozen = new LinkedHashMap<>();
        targets.forEach((key, values) -> frozen.put(key, values.stream().distinct().toList()));
        return new WarframeCommunityNameIndexDTO<>(List.copyOf(rows), Map.copyOf(frozen));
    }
}
