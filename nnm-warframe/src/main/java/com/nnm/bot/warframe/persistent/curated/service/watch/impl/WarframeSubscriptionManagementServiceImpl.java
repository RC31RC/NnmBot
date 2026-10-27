package com.nnm.bot.warframe.persistent.curated.service.watch.impl;

import com.nnm.bot.core.group.domain.BotGroupMember;
import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.core.group.repository.BotGroupMemberRepository;
import com.nnm.bot.core.group.repository.BotGroupRepository;
import com.nnm.bot.core.group.service.CanonicalMemberResolver;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.watch.WarframeGroupWatchRule;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.watch.WarframeGroupWatchRuleDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.watch.WarframeMemberSubscriptionVO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.watch.WarframeSubscriptionUpdateDTO;
import com.nnm.bot.warframe.persistent.curated.repository.watch.WarframeGroupWatchRuleRepository;
import com.nnm.bot.warframe.persistent.curated.service.watch.WarframeSubscriptionManagementService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.List;

@Service
public class WarframeSubscriptionManagementServiceImpl implements WarframeSubscriptionManagementService {
    @Resource
    private BotGroupRepository groups;
    @Resource
    private BotGroupMemberRepository members;
    @Resource
    private CanonicalMemberResolver canonicalMembers;
    @Resource
    private WarframeGroupWatchRuleRepository rules;

    /**
     * 查询并返回当前条件下的业务列表。
     *
     * @param groupId 订阅所属群组的本地主键。
     * @param memberId 订阅所属群成员的本地主键。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    public List<WarframeMemberSubscriptionVO> list(long groupId, long memberId) {
        long canonicalId = requireMember(groupId, memberId, false);
        return rules.findAllProjectedByGroupIdAndMemberId(groupId, canonicalId).stream()
                .sorted(Comparator.comparing(WarframeGroupWatchRuleDTO::getId))
                .map(rule -> new WarframeMemberSubscriptionVO(rule.getId(), rule.getWatchType(), rule.getWatchKey(),
                        rule.getWatchValue(), rule.isEnabled(), rule.isNotifyEnabled(), rule.getCreatedAt(), rule.getUpdatedAt()))
                .toList();
    }

    /**
     * 校验权限与输入后更新成员订阅规则。
     *
     * @param groupId 订阅所属群组的本地主键。
     * @param memberId 订阅所属群成员的本地主键。
     * @param ruleId 规则标识。
     * @param request 本次新增、修改或执行操作使用的已接收参数。
     */
    @Override
    @Transactional
    public void update(long groupId, long memberId, long ruleId, WarframeSubscriptionUpdateDTO request) {
        long canonicalId = requireMember(groupId, memberId, true);
        WarframeGroupWatchRule rule = requireRule(groupId, canonicalId, ruleId);
        // 重新启用或恢复通知后可在下轮命中；普通开关操作不伪造一次投递成功。
        if ((!rule.isEnabled() && request.enabled()) || (!rule.isNotifyEnabled() && request.notifyEnabled())) {
            rule.setMatchedEventsJson(null);
        }
        rule.setEnabled(request.enabled());
        rule.setNotifyEnabled(request.notifyEnabled());
        rules.save(rule);
    }

    /**
     * 校验权限后删除成员订阅规则，并维护关联资源。
     *
     * @param groupId 订阅所属群组的本地主键。
     * @param memberId 订阅所属群成员的本地主键。
     * @param ruleId 规则标识。
     */
    @Override
    @Transactional
    public void delete(long groupId, long memberId, long ruleId) {
        long canonicalId = requireMember(groupId, memberId, true);
        rules.delete(requireRule(groupId, canonicalId, ruleId));
    }

    /**
     * 校验当前作用域成员并读取其业务上下文。
     *
     * @param groupId 订阅所属群组的本地主键。
     * @param memberId 订阅所属群成员的本地主键。
     * @param forWrite 对应写入。
     * @return 当前操作计算或读取的数值。
     */
    private long requireMember(long groupId, long memberId, boolean forWrite) {
        BusinessAssert.isTrue(groups.existsById(groupId), CommonErrorCode.DATA_NOT_FOUND, "群组不存在");
        long canonicalId = canonicalMembers.resolve(groupId, memberId);
        BotGroupMember member = (forWrite ? members.findByIdAndGroupId(canonicalId, groupId)
                : members.findById(canonicalId).filter(value -> value.getGroupId().equals(groupId))).orElse(null);
        BusinessAssert.notNull(member, CommonErrorCode.DATA_NOT_FOUND, "成员不存在或不属于当前群组");
        return canonicalId;
    }

    /**
     * 校验并读取当前作用域有权访问的订阅规则。
     *
     * @param groupId 订阅所属群组的本地主键。
     * @param memberId 订阅所属群成员的本地主键。
     * @param ruleId 规则标识。
     * @return 游戏资料分组订阅监控规则对象。
     */
    private WarframeGroupWatchRule requireRule(long groupId, long memberId, long ruleId) {
        WarframeGroupWatchRule rule = rules.findFirstById(ruleId)
                .filter(value -> value.getGroupId().equals(groupId) && value.getMemberId().equals(memberId)).orElse(null);
        return BusinessAssert.notNull(rule, CommonErrorCode.DATA_NOT_FOUND, "订阅不存在或不属于当前成员");
    }
}
