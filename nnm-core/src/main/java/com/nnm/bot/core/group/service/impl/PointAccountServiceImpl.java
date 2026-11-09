package com.nnm.bot.core.group.service.impl;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.core.constant.GroupPointConstants.PointErrorCode;
import com.nnm.bot.core.constant.GroupPointConstants.TransactionType;
import com.nnm.bot.core.group.domain.BotGroupMember;
import com.nnm.bot.core.group.domain.BotGroupPointTransaction;
import com.nnm.bot.core.group.repository.BotGroupMemberRepository;
import com.nnm.bot.core.group.repository.BotGroupPointTransactionRepository;
import com.nnm.bot.core.group.service.CanonicalMemberResolver;
import com.nnm.bot.core.group.service.PointAccountService;
import com.nnm.bot.core.group.service.PointBalance;
import com.nnm.bot.core.group.service.PointChangeResult;
import com.nnm.bot.core.group.service.PointOperation;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 数据库积分账户实现，所有余额变更都在短事务内持锁完成。 */
@Service
public class PointAccountServiceImpl implements PointAccountService {

    @Resource
    private BotGroupMemberRepository memberRepository;
    @Resource
    private EntityManager entityManager;
    @Resource
    private BotGroupPointTransactionRepository transactionRepository;
    @Resource
    private CanonicalMemberResolver canonicalMembers;

    @Override
    @Transactional
    public PointChangeResult earn(PointOperation operation) {
        BotGroupMember member = lockedMember(operation.groupId(), operation.memberId());
        // 行锁后再查流水，等待前一事务提交的重试才能看到已经落库的幂等记录。
        if (existsByIdempotencyKey(operation.idempotencyKey())) {
            return repeated(operation.idempotencyKey(), member);
        }
        if (!member.isEnabled())
            throw new BusinessException(PointErrorCode.ACCOUNT_UNAVAILABLE);
        long points = Math.addExact(member.getPoints(), operation.amount());
        member.setPoints(points);
        member.setTotalEarned(Math.addExact(member.getTotalEarned(), operation.amount()));
        memberRepository.save(member);
        BotGroupPointTransaction transaction = saveTransaction(operation, member, operation.amount());
        return PointChangeResult.applied(snapshot(member), transaction.getId());
    }

    @Override
    @Transactional
    public PointChangeResult spend(PointOperation operation) {
        BotGroupMember member = lockedMember(operation.groupId(), operation.memberId());
        // 行锁后再查流水，等待前一事务提交的重试才能看到已经落库的幂等记录。
        if (existsByIdempotencyKey(operation.idempotencyKey())) {
            return repeated(operation.idempotencyKey(), member);
        }
        if (!member.isEnabled())
            throw new BusinessException(PointErrorCode.ACCOUNT_UNAVAILABLE);
        if (member.getPoints() < operation.amount()) {
            return PointChangeResult.denied("积分不足，需要 " + operation.amount() + " 分", snapshot(member));
        }
        member.setPoints(member.getPoints() - operation.amount());
        member.setTotalSpent(Math.addExact(member.getTotalSpent(), operation.amount()));
        memberRepository.save(member);
        BotGroupPointTransaction transaction = saveTransaction(operation, member, -operation.amount());
        return PointChangeResult.applied(snapshot(member), transaction.getId());
    }

    @Override
    @Transactional
    public PointChangeResult refund(String originalIdempotencyKey, String description) {
        BotGroupPointTransaction original = transactionRepository.findByIdempotencyKey(originalIdempotencyKey)
            .orElseThrow(() -> new SystemException(PointErrorCode.LEDGER_INCONSISTENT, "原积分扣费流水不存在"));
        if (!TransactionType.COMMAND_COST.name().equals(original.getTransactionType()) || original.getAmount() >= 0) {
            throw new SystemException(PointErrorCode.LEDGER_INCONSISTENT, "只有命令扣费流水允许退款");
        }
        BotGroupMember member = lockedMember(original.getGroupId(), original.getMemberId());
        String refundKey = "REFUND:" + original.getId();
        if (existsByIdempotencyKey(refundKey)) {
            return repeated(refundKey, member);
        }
        long refund = Math.abs(original.getAmount());
        member.setPoints(Math.addExact(member.getPoints(), refund));
        member.setTotalSpent(Math.max(0, member.getTotalSpent() - refund));
        memberRepository.save(member);

        BotGroupPointTransaction transaction = new BotGroupPointTransaction();
        transaction.setGroupId(original.getGroupId());
        transaction.setMemberId(member.getId());
        transaction.setCommandCode(original.getCommandCode());
        transaction.setTransactionType(TransactionType.REFUND.name());
        transaction.setAmount(refund);
        transaction.setBalanceAfter(member.getPoints());
        transaction.setIdempotencyKey(refundKey);
        transaction.setDescription(StrUtil.blankToDefault(description, "命令执行失败退款"));
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
        return PointChangeResult.applied(snapshot(member), transaction.getId());
    }

    @Override
    @Transactional
    public PointChangeResult adjust(long groupId, long memberId, long targetPoints, String idempotencyKey,
        String description) {
        if (groupId <= 0 || memberId <= 0) {
            throw new BusinessException(CommonErrorCode.PARAM_INVALID, "积分账户标识必须为正数");
        }
        if (targetPoints < 0) {
            throw new BusinessException(CommonErrorCode.PARAM_INVALID, "积分余额不能为负数");
        }
        String normalizedIdempotencyKey = requireText(idempotencyKey, "idempotencyKey");
        BotGroupMember member = lockedMember(groupId, memberId);
        if (existsByIdempotencyKey(normalizedIdempotencyKey)) {
            return repeated(normalizedIdempotencyKey, member);
        }
        long amount = Math.subtractExact(targetPoints, member.getPoints());
        if (amount > 0) {
            member.setTotalEarned(Math.addExact(member.getTotalEarned(), amount));
        } else {
            member.setTotalSpent(Math.addExact(member.getTotalSpent(), Math.abs(amount)));
        }
        member.setPoints(targetPoints);
        memberRepository.save(member);

        PointOperation operation = new PointOperation(groupId, member.getId(), null, TransactionType.ADMIN_ADJUST,
            Math.abs(amount), normalizedIdempotencyKey, description);
        BotGroupPointTransaction transaction = saveTransaction(operation, member, amount);
        return PointChangeResult.applied(snapshot(member), transaction.getId());
    }

    @Override
    public Optional<PointBalance> balance(long groupId, long memberId) {
        memberId = canonicalMemberId(groupId, memberId);
        return memberRepository.findById(memberId).filter(member -> member.getGroupId().equals(groupId))
            .map(this::snapshot);
    }

    @Override
    public boolean existsByIdempotencyKey(String idempotencyKey) {
        return StrUtil.isNotBlank(idempotencyKey) && transactionRepository.existsByIdempotencyKey(idempotencyKey);
    }

    private BotGroupMember lockedMember(long groupId, long memberId) {
        long canonical = canonicalMemberId(groupId, memberId);
        for (int attempt = 0; attempt < CanonicalMemberResolver.MAX_RESOLUTION_DEPTH; attempt++) {
            var lockedId = memberRepository.lockIdByIdAndGroupId(canonical, groupId);
            if (lockedId.isPresent()) {
                // 先以标量查询取锁，再刷新旧实体，避免锁查询自身触发一级缓存版本冲突。
                BotGroupMember member = memberRepository.findById(lockedId.get())
                    .orElseThrow(() -> new SystemException(PointErrorCode.LEDGER_INCONSISTENT, "已锁定的积分账户不存在"));
                entityManager.refresh(member);
                return member;
            }
            // 合并可在解析身份与取行锁之间提交；此时沿已提交别名重取目标行。
            long next = canonicalMemberId(groupId, canonical);
            if (next == canonical)
                throw new BusinessException(PointErrorCode.ACCOUNT_NOT_FOUND);
            canonical = next;
        }
        throw new SystemException(PointErrorCode.LEDGER_INCONSISTENT, "积分账户合并链持续变化");
    }

    private long canonicalMemberId(long groupId, long memberId) {
        return canonicalMembers.resolve(groupId, memberId);
    }

    private BotGroupPointTransaction saveTransaction(PointOperation operation, BotGroupMember member, long amount) {
        BotGroupPointTransaction transaction = new BotGroupPointTransaction();
        transaction.setGroupId(operation.groupId());
        transaction.setMemberId(member.getId());
        transaction.setCommandCode(operation.commandCode());
        transaction.setTransactionType(operation.transactionType().name());
        transaction.setAmount(amount);
        transaction.setBalanceAfter(member.getPoints());
        transaction.setIdempotencyKey(operation.idempotencyKey());
        transaction.setDescription(operation.description());
        transaction.setCreatedAt(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    private PointBalance snapshot(BotGroupMember member) {
        return new PointBalance(member.getPoints(), member.getTotalEarned(), member.getTotalSpent());
    }

    private String requireText(String value, String field) {
        if (StrUtil.isBlank(value)) {
            throw new BusinessException(CommonErrorCode.PARAM_INVALID, field + "不能为空");
        }
        return value.trim();
    }

    private PointChangeResult repeated(String idempotencyKey, BotGroupMember member) {
        BotGroupPointTransaction transaction = transactionRepository.findByIdempotencyKey(idempotencyKey)
            .orElseThrow(() -> new SystemException(PointErrorCode.LEDGER_INCONSISTENT, "幂等流水不存在"));
        if (!member.getGroupId().equals(transaction.getGroupId()) || !member.getId().equals(transaction.getMemberId()))
            throw new SystemException(PointErrorCode.LEDGER_INCONSISTENT, "幂等键属于其他积分账户");
        return PointChangeResult.repeated(snapshot(member), transaction.getId());
    }
}
