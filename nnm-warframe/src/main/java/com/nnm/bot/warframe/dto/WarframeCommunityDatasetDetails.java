package com.nnm.bot.warframe.dto;

/** 社区数据集记录按来源 JSON 区分的结构化字段。 */
public sealed interface WarframeCommunityDatasetDetails
        permits WarframeCommunityTranslationDetails,
        WarframeCommunityAliasDetails,
        WarframeCommunityNodeDetails,
        WarframeCommunityRewardPoolDetails,
        WarframeCommunityStateTranslationDetails,
        WarframeCommunityMarketElementDetails,
        WarframeCommunityMarketRivenTionDetails,
        WarframeCommunityMarketRivenTionAliasDetails,
        WarframeCommunityRivenAnalyseTrendDetails,
        WarframeCommunityRivenTrendDetails {
}
