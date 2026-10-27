package com.nnm.bot.warframe.persistent.curated.service.watch.impl;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.persistent.curated.cache.watch.WarframeSubscriptionEventMemory.Claim;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.watch.WarframeGroupWatchRule;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.watch.WarframeGroupWatchRuleDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.watch.WarframeWatchMatchDTO;
import com.nnm.bot.warframe.persistent.curated.repository.watch.WarframeGroupWatchRuleRepository;
import com.nnm.bot.warframe.persistent.curated.service.watch.WarframeGroupWatchRuleService;
import com.nnm.bot.warframe.persistent.curated.cache.watch.WarframeSubscriptionEventMemory;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;

@Service
public class WarframeGroupWatchRuleServiceImpl implements WarframeGroupWatchRuleService {

    @Resource
    private WarframeGroupWatchRuleRepository repository;
    @Resource
    private WarframeSubscriptionEventMemory eventMemory;

    /**
     * 创建新的业务记录。
     *
     * @param groupId 订阅所属群组的本地主键。
     * @param memberId 订阅所属群成员的本地主键。
     * @param watchType 订阅监控类型。
     * @param watchKey 订阅监控键。
     * @param watchValue 订阅监控值。
     * @return 游戏资料分组订阅监控规则对象。
     */
    @Override
    @Transactional
    public WarframeGroupWatchRuleDTO create(long groupId, long memberId, String watchType,
                                         String watchKey, String watchValue) {
        String normalizedType = watchType == null ? "" : watchType.trim();
        String normalizedKey = watchKey == null ? "" : watchKey.trim();
        String normalizedValue = watchValue == null ? "" : watchValue.trim();
        return WarframeGroupWatchRuleDTO.from(repository
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
                }));
    }

    /**
     * 重新启用已有订阅规则，并显式保存当前状态。
     * @param rule 规则。
     * @return 游戏资料分组订阅监控规则对象。
     */
    private WarframeGroupWatchRule reenable(
            WarframeGroupWatchRule rule) {
        if (!rule.isEnabled()) {
            rule.setEnabled(true);
            rule.setNotifyEnabled(true);
            // A reactivated rule must be eligible for the next matching snapshot.
            rule.setMatchedEventsJson(null);
            return repository.save(rule);
        }
        return rule;
    }

    /**
     * 判断是否满足停用条件。
     *
     * @param groupId 订阅所属群组的本地主键。
     * @param memberId 订阅所属群成员的本地主键。
     * @param watchType 订阅监控类型。
     * @param watchKey 订阅监控键。
     * @param watchValue 订阅监控值。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
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
        if (CollUtil.isEmpty(rules)) {
            return false;
        }
        rules.forEach(rule -> rule.setEnabled(false));
        repository.saveAll(rules);
        return true;
    }

    /**
     * 查询当前启用的订阅规则。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    @Transactional(readOnly = true)
    public List<WarframeGroupWatchRuleDTO> findEnabledRules() {
        return repository.findAllProjectedByEnabledTrue();
    }

    /**
     * 查询当前启用的订阅规则。
     *
     * @param groupId 订阅所属群组的本地主键。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    @Transactional(readOnly = true)
    public List<WarframeGroupWatchRuleDTO> findEnabledRules(long groupId) {
        return repository.findAllProjectedByGroupIdAndEnabledTrue(groupId);
    }

    /**
     * 查询当前启用的订阅规则。
     *
     * @param groupId 订阅所属群组的本地主键。
     * @param memberId 订阅所属群成员的本地主键。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    @Transactional(readOnly = true)
    public List<WarframeGroupWatchRuleDTO> findEnabledRules(long groupId, long memberId) {
        return repository.findAllProjectedByGroupIdAndMemberIdAndEnabledTrue(groupId, memberId);
    }

    /**
     * 在订阅规则锁内认领本轮尚未发送的事件身份，避免重复通知。
     * @param ruleId 规则标识。
     * @param matches 匹配集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    @Transactional
    public Set<String> claimMatches(long ruleId, List<WarframeWatchMatchDTO> matches) {
        if (CollUtil.isEmpty(matches)) return Set.of();
        WarframeGroupWatchRule rule = repository.findFirstById(ruleId).orElse(null);
        if (rule == null || !rule.isEnabled()) return Set.of();
        Claim claim = eventMemory.claim(rule.getMatchedEventsJson(), matches);
        if (CollUtil.isEmpty(claim.keys())) return Set.of();
        rule.setMatchedEventsJson(claim.json());
        repository.save(rule);
        return claim.keys();
    }
}
