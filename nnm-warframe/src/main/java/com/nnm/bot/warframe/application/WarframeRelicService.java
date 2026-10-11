package com.nnm.bot.warframe.application;

import com.nnm.bot.warframe.model.entity.WarframeCommunityRelic;
import com.nnm.bot.warframe.model.entity.WarframeCommunityRelicDrop;
import com.nnm.bot.warframe.model.entity.WarframeCommunityRelicReward;
import java.util.List;

public interface WarframeRelicService {
    List<WarframeCommunityRelic> findRelics(String keyword);
    List<WarframeCommunityRelicReward> findRewards(String relicKey, String rarity);
    List<WarframeCommunityRelicDrop> findDrops(String relicKey);
}
