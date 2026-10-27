package com.nnm.bot.warframe.persistent.official.service.rewards.impl;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.OFFICIAL_SOURCE_CODE;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialRelicDTO;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.WarframeRelicDetailsDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.WarframeRelicDetailsDTO.*;
import com.nnm.bot.warframe.persistent.official.service.rewards.WarframeRelicService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialSelectionService;
import com.nnm.bot.warframe.persistent.official.repository.rewards.OfficialRelicRepository;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;

@Service
public class WarframeRelicServiceImpl implements WarframeRelicService {
    @Resource
    private WarframeOfficialSelectionService selection;

    @Resource
    private WarframeOfficialRecordReadService catalog;

    @Resource
    private OfficialRelicRepository relics;

    /**
     * 按名称或奖励关键词查询遗物及各精炼等级的奖励详情。
     *
     * @param keyword 筛选使用的关键词。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public List<WarframeRelicDetailsDTO> findDetails(String keyword) {
        String query = keyword == null ? "" : keyword.trim();
        List<WarframeRelicDetailsDTO> result = new ArrayList<>();
        List<Long> candidateIds = relics.findPreferredRelicIds(query);
        for (var root : catalog.recordsByIds(WarframeOfficialDataDomain.RELIC, candidateIds)) {
            var source = (OfficialRelicDTO) root.data();
            if (source.getStates() == null) continue;
            source.getStates().forEach((quality, value) -> {
                List<RewardDTO> rewards = new ArrayList<>();
                List<DropDTO> drops = new ArrayList<>();
                for (var reward : values(value.getRewards())) {
                    rewards.add(new RewardDTO(first(reward.getItemNameZh(), reward.getItemNameEn(), reward.getItemName(), reward.getRewardName()),
                            first(reward.getRarity()), probability(reward.getDropChancePercent(), reward.getProbability())));
                }
                for (var drop : values(source.getLocations())) {
                    drops.add(new DropDTO(first(drop.getSourceZh(), drop.getSource()), first(drop.getRarity()),
                            probability(drop.getDropChancePercent(), drop.getProbability())));
                }
                String label = name(root);
                RelicDTO relic = new RelicDTO(root.bkey(), label, value.getVaultedAt() == null ? null : true,
                        quality, first(value.getIcon()), first(value.getIconUrl()), OFFICIAL_SOURCE_CODE);
                if (selection.matches(root, query) || rewards.stream().anyMatch(row -> StrUtil.containsIgnoreCase(row.rewardName(), query))
                        || drops.stream().anyMatch(row -> StrUtil.containsIgnoreCase(row.locationText(), query))) {
                    result.add(new WarframeRelicDetailsDTO(relic, rewards, drops));
                }
            });
        }
        return result;
    }

    /**
     * 显式百分数优先，否则将概率比例转换为百分数。
     *
     * @param percent 百分比。
     * @param ratio 比值。
     * @return 当前操作计算或读取的数值。
     */
    private static BigDecimal probability(BigDecimal percent, BigDecimal ratio) {
        return percent != null ? percent : ratio == null ? null : ratio.multiply(BigDecimal.valueOf(100));
    }
}
