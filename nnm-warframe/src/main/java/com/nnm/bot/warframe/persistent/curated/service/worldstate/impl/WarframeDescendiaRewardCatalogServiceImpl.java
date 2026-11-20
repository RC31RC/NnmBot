package com.nnm.bot.warframe.persistent.curated.service.worldstate.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.collection.CollUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.worldstate.DescendiaRewardGroupDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.worldstate.DescendiaRewardGroupDTO.RewardOptionDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.worldstate.DescendiaRewardRowDTO;
import com.nnm.bot.warframe.persistent.curated.repository.worldstate.WarframeCommunityDescendiaRewardRepository;
import com.nnm.bot.warframe.persistent.curated.service.worldstate.WarframeDescendiaRewardCatalogService;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialDropTableDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialDropTableDTO.RewardsDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.OfficialArcaneDTO;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialLookupService;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

/** 仅保存人工审核的楼层映射；奖励、概率、数量及图片优先取已采纳的官方 DROP_TABLE。 */
@Service
public class WarframeDescendiaRewardCatalogServiceImpl implements WarframeDescendiaRewardCatalogService {
    @Resource
    private WarframeCommunityDescendiaRewardRepository repository;
    @Resource
    private WarframeOfficialLookupService officialLookup;
    @Resource
    private ObjectMapper mapper;

    /** @return 楼层对应的官方奖池或官方表缺失时经审核的补充候选；非本周抽中结果。 */
    @Override
    public List<DescendiaRewardGroupDTO> findAll() {
        List<DescendiaRewardRowDTO> rows = repository.findAllProjectedByOrderByFloorNumberAsc();
        if (rows.isEmpty())
            return List.of();
        Map<String, List<String>> supplemental = new LinkedHashMap<>();
        Set<String> poolKeys = new LinkedHashSet<>();
        for (DescendiaRewardRowDTO row : rows) {
            if (row.getOfficialPoolKey() != null)
                poolKeys.add(row.getOfficialPoolKey());
            if (row.getOptionsJson() != null) {
                try {
                    supplemental.put(row.getRecordKey(),
                        mapper.readValue(row.getOptionsJson(), new TypeReference<List<String>>() {}));
                } catch (JsonProcessingException error) {
                    throw new IllegalStateException("深层下降补充奖励选项不是字符串列表：" + row.getRecordKey(), error);
                }
            }
        }
        Map<String, WarframeOfficialRecordReadDTO> official =
            poolKeys.isEmpty() ? Map.of() : officialLookup.records(WarframeOfficialDataDomain.DROP_TABLE, poolKeys);
        Map<String, RewardOptionDTO> supplementalNames = resolveSupplementalNames(supplemental);
        List<DescendiaRewardGroupDTO> result = new ArrayList<>();
        for (DescendiaRewardRowDTO row : rows) {
            List<RewardOptionDTO> options;
            if (row.getOfficialPoolKey() != null) {
                WarframeOfficialRecordReadDTO record = official.get(row.getOfficialPoolKey().toLowerCase(Locale.ROOT));
                if (record == null || !(record.data() instanceof OfficialDropTableDTO pool)
                    || !row.getExpectedPoolName().equals(pool.getSourceName()) || pool.getRewards() == null
                    || pool.getRewards().isEmpty())
                    continue;
                options = pool.getRewards().stream().map(reward -> officialOption(reward, row.getItemCountOverride()))
                    .toList();
            } else {
                String source = row.getSourceRewardKey() == null ? row.getRecordKey() : row.getSourceRewardKey();
                List<String> names = supplemental.get(source);
                if (names == null)
                    throw new IllegalStateException("深层下降未找到已入库的补充奖励：" + source);
                options = names.stream()
                    .map(name -> supplementalNames.getOrDefault(name, new RewardOptionDTO(name, null, null, null)))
                    .toList();
            }
            result.add(new DescendiaRewardGroupDTO(row.getFloorNumber(), row.getDifficulty(), row.getLabel(), options,
                row.getNote(), row.isGuaranteed(), row.getSourceUrl()));
        }
        return List.copyOf(result);
    }

    /** 官方奖励使用已采纳的中文、图片、数量和概率，不以人工文本覆盖数据源。 */
    private static RewardOptionDTO officialOption(RewardsDTO reward, Integer countOverride) {
        String name = first(reward.getItemNameZh(), reward.getItemNameEn(), reward.getItemName());
        String icon = https(reward.getItemIconUrl());
        if (icon == null && CollUtil.isNotEmpty(reward.getItemCandidates())) {
            // 身份多解时只接受所有候选均相同的图片，不随意指定一种裂罅或蓝图。
            Set<String> candidates = new LinkedHashSet<>();
            reward.getItemCandidates().forEach(candidate -> candidates.add(https(candidate.getIconUrl())));
            if (candidates.size() == 1)
                icon = candidates.iterator().next();
        }
        BigDecimal count = countOverride == null ? reward.getItemCount() : BigDecimal.valueOf(countOverride);
        return new RewardOptionDTO(name == null ? "奖励名称未收录" : name.replaceAll("<[^>]+>", ""), icon, count,
            reward.getDropChancePercent());
    }

    /** 仅对官方表缺失的补充文本尝试从已采纳的赋能表匹配中文与图片。 */
    private Map<String, RewardOptionDTO> resolveSupplementalNames(Map<String, List<String>> supplemental) {
        var index = officialLookup.nameIndex(WarframeOfficialDataDomain.ARCANE);
        if (index == null)
            return Map.of();
        Map<String, Long> matched = new LinkedHashMap<>();
        Set<Long> ids = new LinkedHashSet<>();
        for (List<String> options : supplemental.values())
            for (String name : options) {
                var matches = index.matches(WarframeOfficialDataDomain.ARCANE, name);
                if (matches.size() != 1)
                    continue;
                var entry = index.items().get(matches.getFirst());
                if (entry == null)
                    continue;
                matched.put(name, entry.databaseId());
                ids.add(entry.databaseId());
            }
        Map<Long, WarframeOfficialRecordReadDTO> details =
            ids.isEmpty() ? Map.of() : officialLookup.recordsByIds(WarframeOfficialDataDomain.ARCANE, ids);
        Map<String, RewardOptionDTO> resolved = new LinkedHashMap<>();
        matched.forEach((name, id) -> {
            var detail = details.get(id);
            if (detail == null)
                return;
            String image = detail.data() instanceof OfficialArcaneDTO arcane ? https(arcane.getIconUrl()) : null;
            resolved.put(name, new RewardOptionDTO(first(detail.localizedName(), name), image, null, null));
        });
        return resolved;
    }

    private static String first(String... values) {
        for (String value : values)
            if (value != null && !CharSequenceUtil.isBlank(value))
                return value;
        return null;
    }

    private static String https(String url) {
        return url != null && url.startsWith("https://") ? url : null;
    }
}
