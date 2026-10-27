package com.nnm.bot.warframe.persistent.curated.service.watch.impl;

import com.nnm.bot.warframe.persistent.curated.cache.watch.WarframeSubscriptionEventMemory;
import com.nnm.bot.core.group.port.BotGroupMemberMergeHandler;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.watch.WarframeGroupWatchRule;
import com.nnm.bot.warframe.persistent.curated.repository.watch.WarframeGroupWatchRuleRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/** 合并逻辑群成员时同步整理重复的 Warframe 订阅规则。 */
@Component
public class WarframeGroupMemberMergeHandlerImpl implements BotGroupMemberMergeHandler {

    @Resource
    private WarframeGroupWatchRuleRepository repository;
    @Resource
    private WarframeSubscriptionEventMemory eventMemory;

    /**
     * 合并相同业务引用的解析结果，保留明确的身份和来源。
     *
     * @param groupId 订阅所属群组的本地主键。
     * @param sourceMemberId 来源成员标识。
     * @param targetMemberId 目标成员标识。
     */
    @Override
    public void mergeReferences(long groupId, long sourceMemberId, long targetMemberId) {
        for (WarframeGroupWatchRule source : repository
                .findAllByGroupIdAndMemberId(groupId, sourceMemberId)) {
            WarframeGroupWatchRule target = repository
                    .findFirstByGroupIdAndMemberIdAndWatchTypeAndWatchKeyAndWatchValue(
                            groupId, targetMemberId, source.getWatchType(),
                            source.getWatchKey(), source.getWatchValue())
                    .orElse(null);
            if (target == null) {
                source.setMemberId(targetMemberId);
                repository.save(source);
                continue;
            }
            target.setEnabled(target.isEnabled() || source.isEnabled());
            target.setNotifyEnabled(target.isNotifyEnabled() || source.isNotifyEnabled());
            target.setMatchedEventsJson(eventMemory.merge(
                    target.getMatchedEventsJson(), source.getMatchedEventsJson()));
            if (target.getCreatedAt() == null
                    || isAfter(target.getCreatedAt(), source.getCreatedAt())) {
                target.setCreatedAt(source.getCreatedAt());
            }
            repository.save(target);
            repository.delete(source);
        }
    }

    /**
     * 比较两个时间点，判断前者是否晚于后者。
     *
     * @param left 参与比较的左侧数据。
     * @param right 参与比较的右侧数据。
     * @return 满足比较两个时间点，判断前者是否晚于后者时为真，否则为假。
     */
    private boolean isAfter(LocalDateTime left, LocalDateTime right) {
        return left != null && (right == null || left.isAfter(right));
    }
}
