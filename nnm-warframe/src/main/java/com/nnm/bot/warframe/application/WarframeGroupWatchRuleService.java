package com.nnm.bot.warframe.application;

import com.nnm.bot.warframe.model.entity.WarframeGroupWatchRule;

import java.util.List;

public interface WarframeGroupWatchRuleService {

    WarframeGroupWatchRule create(long groupId, long memberId, String watchType, String watchKey, String watchValue);

    boolean disable(long groupId, long memberId, String watchType, String watchKey, String watchValue);

    List<WarframeGroupWatchRule> findEnabledRules();

    List<WarframeGroupWatchRule> findEnabledRules(long groupId);

    List<WarframeGroupWatchRule> findEnabledRules(long groupId, long memberId);

    boolean updateLastMatchKey(long ruleId, String matchKey);
}
