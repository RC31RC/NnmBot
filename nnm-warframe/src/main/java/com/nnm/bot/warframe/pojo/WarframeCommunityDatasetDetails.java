package com.nnm.bot.warframe.pojo;

/** 社区数据集记录按来源 JSON 区分的结构化字段。 */
public sealed interface WarframeCommunityDatasetDetails
        permits WarframeBountyDefinitionDetails,
        WarframeCommunityTranslationDetails,
        WarframeCommunityAliasDetails,
        WarframeCommunityNodeDetails,
        WarframeCommunityRewardPoolDetails,
        WarframeCommunityStateTranslationDetails,
        WarframeCommunityMarketElementDetails,
        WarframeCommunityMarketComponentDetails,
        WarframeCommunityMarketRivenTionDetails,
        WarframeCommunityMarketRivenTionAliasDetails,
        WarframeCommunityRivenAnalyseTrendDetails,
        WarframeCommunityRivenTrendDetails,
        WarframeCommunityTermDetails,
        WarframeCommunityArbitrationEssenceDetails,
        WarframeCommunityFishGuideDetails,
        WarframeCommunityMapFeatureDetails,
        WarframeCommunityRelicDetails,
        WarframeCommunityRelicRewardDetails,
        WarframeCommunityRelicDropDetails,
        WarframeCommunityRivenUnrollableDetails,
        WarframeCommunityWorldStateRuleDetails,
        WarframeWorldStateCodebookDetails,
        WarframeCommunityGenericDetails {
}
