package com.nnm.bot.core.group.service;

import com.nnm.bot.core.group.domain.BotGroupMember;
import com.nnm.bot.core.group.domain.BotGroupMemberMerge;
import com.nnm.bot.core.group.port.BotGroupMemberMergeHandler;
import com.nnm.bot.core.group.repository.BotGroupMemberMergeRepository;
import com.nnm.bot.core.group.repository.BotGroupMemberRepository;
import com.nnm.bot.core.group.repository.BotGroupPointTransactionRepository;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 合并同一逻辑群组中的两条成员记录，并在删除来源行前重定向所有已知引用。
 */
@Service
public class BotGroupMemberMergeService {

    @Resource
    private BotGroupMemberRepository memberRepository;
    @Resource
    private BotGroupMemberMergeRepository mergeRepository;
    @Resource
    private BotGroupPointTransactionRepository pointTransactionRepository;
    @Resource
    private List<BotGroupMemberMergeHandler> mergeHandlers = List.of();

    @Transactional
    public BotGroupMember merge(long groupId, BotGroupMember target, BotGroupMember source) {
        if (target.getId() == null || source.getId() == null
                || target.getId().equals(source.getId())) {
            throw new IllegalArgumentException("成员合并必须提供两个不同的持久成员");
        }
        if (!Long.valueOf(groupId).equals(target.getGroupId())
                || !Long.valueOf(groupId).equals(source.getGroupId())) {
            throw new IllegalArgumentException("只能合并同一逻辑群组的成员");
        }

        BotGroupMember lockedTarget = load(groupId, target.getId());
        BotGroupMember lockedSource = load(groupId, source.getId());
        ensureCompatible(lockedTarget, lockedSource);
        mergeFields(lockedTarget, lockedSource);

        if (pointTransactionRepository != null) {
            pointTransactionRepository.updateMemberId(groupId,
                    lockedSource.getId(), lockedTarget.getId());
        }
        if (mergeHandlers != null) {
            mergeHandlers.forEach(handler -> handler.mergeReferences(
                    groupId, lockedSource.getId(), lockedTarget.getId()));
        }

        // 先释放来源行上的唯一身份列，再写入主行，避免 PostgreSQL 唯一索引在 flush 顺序上冲突。
        clearSourceIdentities(lockedSource);
        memberRepository.saveAndFlush(lockedSource);
        memberRepository.save(lockedTarget);

        if (mergeRepository != null) {
            BotGroupMemberMerge alias = new BotGroupMemberMerge();
            alias.setGroupId(groupId);
            alias.setSourceMemberId(lockedSource.getId());
            alias.setTargetMemberId(lockedTarget.getId());
            alias.setMergedAt(LocalDateTime.now());
            mergeRepository.save(alias);
        }
        memberRepository.delete(lockedSource);
        return lockedTarget;
    }

    private BotGroupMember load(long groupId, long memberId) {
        return memberRepository.findByIdAndGroupId(memberId, groupId)
                .or(() -> memberRepository.findById(memberId)
                        .filter(member -> Long.valueOf(groupId).equals(member.getGroupId())))
                .orElseThrow(() -> new IllegalStateException("群组成员不存在: " + memberId));
    }

    private void ensureCompatible(BotGroupMember target, BotGroupMember source) {
        if (conflicts(target.getQqId(), source.getQqId())
                || conflicts(target.getMemberOpenid(), source.getMemberOpenid())
                || conflicts(target.getKookId(), source.getKookId())) {
            throw new IllegalArgumentException("成员存在冲突的平台身份，不能自动合并");
        }
    }

    private boolean conflicts(String target, String source) {
        return target != null && !target.isBlank()
                && source != null && !source.isBlank()
                && !target.equals(source);
    }

    private void mergeFields(BotGroupMember target, BotGroupMember source) {
        target.setPoints(Math.addExact(target.getPoints(), source.getPoints()));
        target.setTotalEarned(Math.addExact(target.getTotalEarned(), source.getTotalEarned()));
        target.setTotalSpent(Math.addExact(target.getTotalSpent(), source.getTotalSpent()));
        target.setEnabled(target.isEnabled() && source.isEnabled());
        target.setQqIdVerified(target.isQqIdVerified() || source.isQqIdVerified());
        if (isBlank(target.getDisplayName())) target.setDisplayName(source.getDisplayName());
        if (isBlank(target.getQqId())) target.setQqId(source.getQqId());
        if (isBlank(target.getMemberOpenid())) target.setMemberOpenid(source.getMemberOpenid());
        if (isBlank(target.getKookId())) target.setKookId(source.getKookId());
        if (target.getQqBoundAt() == null || source.getQqBoundAt() != null
                && source.getQqBoundAt().isBefore(target.getQqBoundAt())) {
            target.setQqBoundAt(source.getQqBoundAt());
        }
    }

    private void clearSourceIdentities(BotGroupMember source) {
        source.setQqId(null);
        source.setMemberOpenid(null);
        source.setKookId(null);
        source.setQqIdVerified(false);
        source.setQqBoundAt(null);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
