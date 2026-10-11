package com.nnm.bot.warframe.service.impl;

import com.nnm.bot.warframe.application.WarframeGroupWatchRuleService;
import com.nnm.bot.warframe.model.entity.WarframeGroupWatchRule;
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
    @Transactional
    public WarframeGroupWatchRule create(long groupId, long memberId, String watchType,
                                         String watchKey, String watchValue) {
        String normalizedType = watchType == null ? "" : watchType.trim();
        String normalizedKey = watchKey == null ? "" : watchKey.trim();
        String normalizedValue = watchValue == null ? "" : watchValue.trim();
        return repository
                .findFirstByGroupIdAndMemberIdAndWatchTypeAndWatchKeyAndWatchValue(
                        groupId, memberId, normalizedType, normalizedKey, normalizedValue)
                .map(this::reenable)
                .orElseGet(() -> {
                    WarframeGroupWatchRule rule = new WarframeGroupWatchRule();
                    rule.setGroupId(groupId);
                    rule.setMemberId(memberId);
                    rule.setWatchType(normalizedType);
                    rule.setWatchKey(normalizedKey);
                    rule.setWatchValue(normalizedValue);
                    return repository.save(rule);
                });
    }

    private WarframeGroupWatchRule reenable(
            WarframeGroupWatchRule rule) {
        if (!rule.isEnabled()) {
            rule.setEnabled(true);
            rule.setNotifyEnabled(true);
            // A reactivated rule must be eligible for the next matching snapshot.
            rule.setLastMatchKey(null);
            return repository.save(rule);
        }
        return rule;
    }

    @Override
    @Transactional
    public boolean disable(long groupId, long memberId, String watchType,
                           String watchKey, String watchValue) {
        String normalizedType = watchType == null ? "" : watchType.trim();
        String normalizedKey = watchKey == null ? "*" : watchKey.trim();
        String normalizedValue = watchValue == null ? "" : watchValue.trim();
        List<WarframeGroupWatchRule> rules = "*".equals(normalizedKey)
                ? repository.findAllByGroupIdAndMemberIdAndWatchTypeAndWatchValueAndEnabledTrue(
                        groupId, memberId, normalizedType, normalizedValue)
                : repository
                .findAllByGroupIdAndMemberIdAndWatchTypeAndWatchKeyAndWatchValueAndEnabledTrue(
                        groupId, memberId, normalizedType, normalizedKey, normalizedValue);
        if (rules.isEmpty()) {
            return false;
        }
        rules.forEach(rule -> {
                    rule.setEnabled(false);
        });
        repository.saveAll(rules);
        return true;
    }

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
    @Transactional(readOnly = true)
    public List<WarframeGroupWatchRule> findEnabledRules(long groupId, long memberId) {
        return repository.findAllByGroupIdAndMemberIdAndEnabledTrue(groupId, memberId);
    }

    @Override
    @Transactional
    public boolean updateLastMatchKey(long ruleId, String matchKey) {
        return repository.updateLastMatchKeyIfChanged(ruleId, matchKey) > 0;
    }
}
