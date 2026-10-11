package com.nnm.bot.warframe.service.catalog;

import com.nnm.bot.warframe.constant.WarframeCommunityDataset;
import com.nnm.bot.warframe.pojo.WarframeCommunityAliasDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityDataRecord;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityMarketElementDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityMarketComponentDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityMarketRivenTionAliasDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityMarketRivenTionDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityNodeDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityRewardPoolDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityRivenAnalyseTrendDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityRivenTrendDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityStateTranslationDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityTranslationDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityTermDetails;
import com.nnm.bot.warframe.pojo.WarframeCommunityArbitrationEssenceDetails;

import java.util.List;

/**
 * 为命令插件提供十类社区数据及其中的自维护记录。
 *
 * <p>每条记录携带 recordSource，调用方可按 COMMUNITY 或 SELF_MAINTAINED
 * 选择来源，也可按优先级合并使用。</p>
 */
public interface WarframeCommunityCatalogService {

    List<? extends WarframeCommunityDataRecord<? extends WarframeCommunityDatasetDetails>>
            records(WarframeCommunityDataset dataset);

    List<WarframeCommunityDataRecord<WarframeCommunityTranslationDetails>> translations();

    List<WarframeCommunityDataRecord<WarframeCommunityAliasDetails>> aliases();

    List<WarframeCommunityDataRecord<WarframeCommunityNodeDetails>> nodes();

    List<WarframeCommunityDataRecord<WarframeCommunityRewardPoolDetails>> rewardPools();

    List<WarframeCommunityDataRecord<WarframeCommunityStateTranslationDetails>>
            stateTranslations();

    List<WarframeCommunityDataRecord<WarframeCommunityMarketElementDetails>> marketElements();

    List<WarframeCommunityDataRecord<WarframeCommunityMarketComponentDetails>> marketComponents();

    List<WarframeCommunityDataRecord<WarframeCommunityMarketRivenTionDetails>> marketRivenTions();

    List<WarframeCommunityDataRecord<WarframeCommunityMarketRivenTionAliasDetails>>
            marketRivenTionAliases();

    List<WarframeCommunityDataRecord<WarframeCommunityRivenAnalyseTrendDetails>>
            rivenAnalyseTrends();

    List<WarframeCommunityDataRecord<WarframeCommunityRivenTrendDetails>> rivenTrends();

    List<WarframeCommunityDataRecord<WarframeCommunityTermDetails>> terms();

    List<WarframeCommunityDataRecord<WarframeCommunityArbitrationEssenceDetails>>
            arbitrationEssences();
}
