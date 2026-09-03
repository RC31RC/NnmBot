package com.nnm.bot.warframe.constant;

import com.nnm.bot.warframe.domain.entity.WarframeCommunityAliasDataset;
import com.nnm.bot.warframe.domain.entity.WarframeCommunityDatasetRecord;
import com.nnm.bot.warframe.domain.entity.WarframeCommunityMarketElementDataset;
import com.nnm.bot.warframe.domain.entity.WarframeCommunityMarketRivenTionAliasDataset;
import com.nnm.bot.warframe.domain.entity.WarframeCommunityMarketRivenTionDataset;
import com.nnm.bot.warframe.domain.entity.WarframeCommunityNodesDataset;
import com.nnm.bot.warframe.domain.entity.WarframeCommunityRewardPoolDataset;
import com.nnm.bot.warframe.domain.entity.WarframeCommunityRivenAnalyseTrendDataset;
import com.nnm.bot.warframe.domain.entity.WarframeCommunityRivenTrendDataset;
import com.nnm.bot.warframe.domain.entity.WarframeCommunityStateTranslationDataset;
import com.nnm.bot.warframe.domain.entity.WarframeCommunityTranslationDataset;

import java.util.Locale;

/** 社区仓库中可独立刷新和展示的 JSON 数据集。 */
public enum WarframeCommunityDataset {
    TRANSLATION("translation", "translation.json", "社区翻译",
            WarframeCommunityTranslationDataset.class),
    ALIAS("alias", "alias.json", "社区别名", WarframeCommunityAliasDataset.class),
    NODES("nodes", "nodes.json", "节点", WarframeCommunityNodesDataset.class),
    REWARD_POOL("reward_pool", "reward_pool.json", "奖励池",
            WarframeCommunityRewardPoolDataset.class),
    STATE_TRANSLATION("state_translation", "state_translation.json", "状态翻译",
            WarframeCommunityStateTranslationDataset.class),
    MARKET_ELEMENT("market_element", "market_element.json", "Market 物品",
            WarframeCommunityMarketElementDataset.class),
    MARKET_RIVEN_TION("market_riven_tion", "market_riven_tion.json", "Riven 词条",
            WarframeCommunityMarketRivenTionDataset.class),
    MARKET_RIVEN_TION_ALIAS("market_riven_tion_alias", "market_riven_tion_alias.json",
            "Riven 词条别名", WarframeCommunityMarketRivenTionAliasDataset.class),
    RIVEN_ANALYSE_TREND("riven_analyse_trend", "riven_analyse_trend.json", "Riven 分析趋势",
            WarframeCommunityRivenAnalyseTrendDataset.class),
    RIVEN_TREND("riven_trend", "riven_trend.json", "Riven 趋势",
            WarframeCommunityRivenTrendDataset.class);

    private final String code;
    private final String fileName;
    private final String label;
    private final Class<? extends WarframeCommunityDatasetRecord> entityType;

    WarframeCommunityDataset(
            String code,
            String fileName,
            String label,
            Class<? extends WarframeCommunityDatasetRecord> entityType) {
        this.code = code;
        this.fileName = fileName;
        this.label = label;
        this.entityType = entityType;
    }

    public String code() {
        return code;
    }

    public String fileName() {
        return fileName;
    }

    public String label() {
        return label;
    }

    public Class<? extends WarframeCommunityDatasetRecord> entityType() {
        return entityType;
    }

    public static WarframeCommunityDataset parse(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("社区数据集不能为空");
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        for (WarframeCommunityDataset dataset : values()) {
            if (dataset.code.equals(normalized) || dataset.fileName.equals(normalized)
                    || dataset.name().toLowerCase(Locale.ROOT).equals(normalized)) {
                return dataset;
            }
        }
        throw new IllegalArgumentException("不支持的社区数据集：" + value);
    }
}
