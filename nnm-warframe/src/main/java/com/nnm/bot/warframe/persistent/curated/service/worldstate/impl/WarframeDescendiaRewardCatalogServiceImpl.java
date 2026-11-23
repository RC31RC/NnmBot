package com.nnm.bot.warframe.persistent.curated.service.worldstate.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.collection.CollUtil;

import com.nnm.bot.warframe.persistent.curated.pojo.dto.worldstate.DescendiaRewardGroupDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.worldstate.DescendiaRewardGroupDTO.RewardOptionDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.worldstate.DescendiaRewardRowDTO;
import com.nnm.bot.warframe.persistent.curated.repository.worldstate.WarframeCommunityDescendiaRewardRepository;
import com.nnm.bot.warframe.persistent.curated.service.worldstate.WarframeDescendiaRewardCatalogService;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialDropTableDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialDropTableDTO.RewardsDTO;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialLookupService;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/** 关联已审核楼层与官方掉落模型，保留候选、数量覆盖和固定奖励的区别。 */
@Service
public class WarframeDescendiaRewardCatalogServiceImpl implements WarframeDescendiaRewardCatalogService {
    @Resource
    private WarframeCommunityDescendiaRewardRepository repository;
    @Resource
    private WarframeOfficialLookupService officialLookup;

    /** @return 资料可核对的楼层和候选池，概率仅取资料中明确的值。 */
    @Override
    public List<DescendiaRewardGroupDTO> findAll() {
        List<DescendiaRewardRowDTO> rows = repository.findAllProjectedByOrderByFloorNumberAsc();
        if (rows.isEmpty())
            return List.of();
        Set<String> poolKeys = rows.stream().map(DescendiaRewardRowDTO::getOfficialPoolKey)
            .collect(Collectors.toCollection(LinkedHashSet::new));
        Map<String, WarframeOfficialRecordReadDTO> official =
            officialLookup.records(WarframeOfficialDataDomain.DROP_TABLE, poolKeys);
        List<DescendiaRewardGroupDTO> result = new ArrayList<>();
        for (DescendiaRewardRowDTO row : rows) {
            WarframeOfficialRecordReadDTO record = official.get(row.getOfficialPoolKey().toLowerCase(Locale.ROOT));
            if (record == null || !(record.data() instanceof OfficialDropTableDTO pool)
                || !row.getExpectedPoolName().equals(pool.getSourceName()) || CollUtil.isEmpty(pool.getRewards()))
                continue;
            var options =
                pool.getRewards().stream().map(reward -> officialOption(reward, row.getItemCountOverride())).toList();
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
        String key = reward.getItemUniqueName();
        return new RewardOptionDTO(key, first(name, key), icon, count, reward.getDropChancePercent());
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
