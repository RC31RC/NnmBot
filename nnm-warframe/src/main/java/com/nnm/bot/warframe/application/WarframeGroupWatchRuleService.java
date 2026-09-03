package com.nnm.bot.warframe.application;

import com.nnm.bot.warframe.domain.entity.WarframeGroupWatchRule;

import java.util.List;

public interface WarframeGroupWatchRuleService {

    List<WarframeGroupWatchRule> findEnabledRules();

    List<WarframeGroupWatchRule> findEnabledRules(long groupId);

    boolean updateLastMatchKey(long ruleId, String matchKey);
}
