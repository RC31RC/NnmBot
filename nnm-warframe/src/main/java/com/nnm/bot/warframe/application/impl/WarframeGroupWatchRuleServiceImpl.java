package com.nnm.bot.warframe.application.impl;

import com.nnm.bot.warframe.application.WarframeGroupWatchRuleService;
import com.nnm.bot.warframe.domain.entity.WarframeGroupWatchRule;
import com.nnm.bot.warframe.repository.WarframeGroupWatchRuleRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WarframeGroupWatchRuleServiceImpl implements WarframeGroupWatchRuleService {

    @Resource
    private WarframeGroupWatchRuleRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<WarframeGroupWatchRule> findEnabledRules() {
        return repository.findAllByEnabledTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarframeGroupWatchRule> findEnabledRules(long groupId) {
        return repository.findAllByGroupIdAndEnabledTrue(groupId);
    }

    @Override
    @Transactional
    public boolean updateLastMatchKey(long ruleId, String matchKey) {
        return repository.updateLastMatchKeyIfChanged(ruleId, matchKey) > 0;
    }
}
