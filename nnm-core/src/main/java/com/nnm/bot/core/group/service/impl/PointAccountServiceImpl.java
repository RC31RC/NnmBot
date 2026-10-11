package com.nnm.bot.core.group.service.impl;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.core.group.domain.BotGroupMember;
import com.nnm.bot.core.group.domain.BotGroupPointTransaction;
import com.nnm.bot.core.group.repository.BotGroupMemberRepository;
import com.nnm.bot.core.group.repository.BotGroupPointTransactionRepository;
import com.nnm.bot.core.group.service.PointAccountService;
import com.nnm.bot.core.group.service.PointBalance;
import com.nnm.bot.core.group.service.PointChangeResult;
import com.nnm.bot.core.group.service.PointOperation;
import io.github.rdt.lock.DistributedLockManager;
import io.github.rdt.lock.LockOptions;
import io.github.rdt.lock.RdtLock;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/** 数据库积分账户实现，所有余额变更都在短事务内持锁完成。 */
@Service
public class PointAccountServiceImpl implements PointAccountService {

    @Resource
    private BotGroupMemberRepository memberRepository;
    @Resource
    private BotGroupPointTransactionRepository transactionRepository;
    @Resource
    private DistributedLockManager lockManager;

    @Override
    @Transactional
    @RdtLock
    public PointChangeResult earn(PointOperation operation) {
        lockManager.lock("group-member:" + operation.memberId());
        if (existsByIdempotencyKey(operation.idempotencyKey())) {
            return repeated(operation.idempotencyKey(), operation.groupId(), operation.memberId());
        }
        BotGroupMember member = lockedMember(operation);
        long points = Math.addExact(member.getPoints(), operation.amount());
        member.setPoints(points);
        member.setTotalEarned(Math.addExact(member.getTotalEarned(), operation.amount()));
        memberRepository.save(member);
        BotGroupPointTransaction transaction =
                saveTransaction(operation, member, operation.amount());
        return PointChangeResult.applied(snapshot(member), transaction.getId());
    }

    @Override
    @Transactional
    @RdtLock
    public PointChangeResult spend(PointOperation operation) {
        lockManager.lock("group-member:" + operation.memberId());
        if (existsByIdempotencyKey(operation.idempotencyKey())) {
            return repeated(operation.idempotencyKey(), operation.groupId(), operation.memberId());
        }
        BotGroupMember member = lockedMember(operation);
        if (member.getPoints() < operation.amount()) {
            return PointChangeResult.denied(
                    "积分不足，需要 " + operation.amount() + " 分", snapshot(member));
        }
        member.setPoints(member.getPoints() - operation.amount());
        member.setTotalSpent(Math.addExact(member.getTotalSpent(), operation.amount()));
        memberRepository.save(member);
        BotGroupPointTransaction transaction =
                saveTransaction(operation, member, -operation.amount());
        return PointChangeResult.applied(snapshot(member), transaction.getId());
    }

    @Override
    @Transactional
    @RdtLock
    public PointChangeResult refund(String originalIdempotencyKey, String description) {
        lockManager.lock("point-refund:" + originalIdempotencyKey);
        BotGroupPointTransaction original = transactionRepository
                .findByIdempotencyKey(originalIdempotencyKey)
                .orElseThrow(() -> new IllegalStateException("原积分扣费流水不存在"));
        if (!"COMMAND_COST".equals(original.getTransactionType())
                || original.getAmount() >= 0) {
            throw new IllegalArgumentException("只有命令扣费流水允许退款");
        }
        String refundKey = "REFUND:" + original.getId();
        if (existsByIdempotencyKey(refundKey)) {
            return repeated(refundKey, original.getGroupId(), original.getMemberId());
        }
        lockManager.lock(
                "group-member:" + original.getMemberId(),
                LockOptions.defaults());
        BotGroupMember member = memberRepository.findById(original.getMemberId())
                .filter(item -> item.getGroupId().equals(original.getGroupId()))
                .orElseThrow(() -> new IllegalStateException("积分账户不存在"));
        long refund = Math.abs(original.getAmount());
        member.setPoints(Math.addExact(member.getPoints(), refund));
        member.setTotalSpent(Math.max(0, member.getTotalSpent() - refund));
        memberRepository.save(member);

        BotGroupPointTransaction transaction = new BotGroupPointTransaction();
        transaction.setGroupId(original.getGroupId());
        transaction.setMemberId(member.getId());
        transaction.setCommandCode(original.getCommandCode());
        transaction.setTransactionType("REFUND");
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
    @RdtLock
    public PointChangeResult adjust(long groupId,
                                    long memberId,
                                    long targetPoints,
                                    String idempotencyKey,
                                    String description) {
        lockManager.lock("group-member:" + memberId);
        if (groupId <= 0 || memberId <= 0) {
            throw new IllegalArgumentException("积分账户标识必须为正数");
        }
        if (targetPoints < 0) {
            throw new IllegalArgumentException("积分余额不能为负数");
        }
        String normalizedIdempotencyKey = requireText(idempotencyKey, "idempotencyKey");
        if (existsByIdempotencyKey(normalizedIdempotencyKey)) {
            return repeated(normalizedIdempotencyKey, groupId, memberId);
        }
        BotGroupMember member = memberRepository.findById(memberId)
                .filter(item -> item.getGroupId().equals(groupId))
                .orElseThrow(() -> new IllegalStateException("积分账户不存在"));
        long amount = Math.subtractExact(targetPoints, member.getPoints());
        if (amount == 0) {
            return PointChangeResult.applied(snapshot(member), null);
        }
        if (amount > 0) {
            member.setTotalEarned(Math.addExact(member.getTotalEarned(), amount));
        } else {
            member.setTotalSpent(Math.addExact(member.getTotalSpent(), Math.abs(amount)));
        }
        member.setPoints(targetPoints);
        memberRepository.save(member);

        PointOperation operation = new PointOperation(
                groupId, memberId, null, "ADMIN_ADJUST", Math.abs(amount),
                normalizedIdempotencyKey, description);
        BotGroupPointTransaction transaction =
                saveTransaction(operation, member, amount);
        return PointChangeResult.applied(snapshot(member), transaction.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PointBalance> balance(long groupId, long memberId) {
        return memberRepository.findById(memberId)
                .filter(member -> member.getGroupId().equals(groupId))
                .map(this::snapshot);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdempotencyKey(String idempotencyKey) {
        return StrUtil.isNotBlank(idempotencyKey)
                && transactionRepository.existsByIdempotencyKey(idempotencyKey);
    }

    private BotGroupMember lockedMember(PointOperation operation) {
        return memberRepository.findById(operation.memberId())
                .filter(member -> member.getGroupId().equals(operation.groupId()))
                .orElseThrow(() -> new IllegalStateException("积分账户不存在"));
    }

    private BotGroupPointTransaction saveTransaction(PointOperation operation,
                                                     BotGroupMember member,
                                                     long amount) {
        BotGroupPointTransaction transaction = new BotGroupPointTransaction();
        transaction.setGroupId(operation.groupId());
        transaction.setMemberId(operation.memberId());
        transaction.setCommandCode(operation.commandCode());
        transaction.setTransactionType(operation.transactionType());
        transaction.setAmount(amount);
        transaction.setBalanceAfter(member.getPoints());
        transaction.setIdempotencyKey(operation.idempotencyKey());
        transaction.setDescription(operation.description());
        transaction.setCreatedAt(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    private PointBalance snapshot(BotGroupMember member) {
        return new PointBalance(
                member.getPoints(), member.getTotalEarned(), member.getTotalSpent());
    }

    private String requireText(String value, String field) {
        if (StrUtil.isBlank(value)) {
            throw new IllegalArgumentException(field + "不能为空");
        }
        return value.trim();
    }

    private PointChangeResult repeated(String idempotencyKey,
                                       long groupId,
                                       long memberId) {
        BotGroupPointTransaction transaction = transactionRepository
                .findByIdempotencyKey(idempotencyKey)
                .orElseThrow(() -> new IllegalStateException("幂等流水不存在"));
        return PointChangeResult.repeated(
                balance(groupId, memberId).orElseThrow(
                        () -> new IllegalStateException("幂等流水存在，但积分账户不存在")),
                transaction.getId());
    }
}
