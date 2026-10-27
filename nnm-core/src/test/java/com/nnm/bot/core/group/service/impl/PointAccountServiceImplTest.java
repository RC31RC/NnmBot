package com.nnm.bot.core.group.service.impl;

import org.mockito.ArgumentMatchers;
import com.nnm.bot.core.group.domain.BotGroupMember;
import com.nnm.bot.core.group.domain.BotGroupPointTransaction;
import com.nnm.bot.core.group.repository.BotGroupMemberRepository;
import com.nnm.bot.core.group.repository.BotGroupPointTransactionRepository;
import com.nnm.bot.core.group.service.PointChangeResult;
import com.nnm.bot.core.group.service.CanonicalMemberResolver;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import com.nnm.bot.core.group.service.PointOperation;
import io.github.rdt.lock.DistributedLockManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointAccountServiceImplTest {

    private static final long GROUP_ID = 101L;
    private static final long MEMBER_ID = 201L;

    @Mock
    private BotGroupMemberRepository memberRepository;
    @Mock
    private BotGroupPointTransactionRepository transactionRepository;
    @Mock
    private DistributedLockManager lockManager;
    @Mock
    private CanonicalMemberResolver canonicalMembers;
    @InjectMocks
    private PointAccountServiceImpl service;

    @BeforeEach
    void canonical() {
        Mockito.lenient().when(canonicalMembers.resolve(Mockito.anyLong(), Mockito.anyLong()))
                .thenAnswer(call -> call.getArgument(1));
    }

    @Test
    void repeatedOperationReturnsCurrentBalanceWithoutMutatingAgain() {
        PointOperation operation = operation("SIGN_IN:101:201:2026-09-02", 10);
        BotGroupMember member = member(GROUP_ID, 30);
        BotGroupPointTransaction transaction = new BotGroupPointTransaction();
        transaction.setId(301L);
        when(this.transactionRepository.existsByIdempotencyKey(operation.idempotencyKey()))
                .thenReturn(true);
        when(this.transactionRepository.findByIdempotencyKey(operation.idempotencyKey()))
                .thenReturn(Optional.of(transaction));
        when(this.memberRepository.findById(MEMBER_ID)).thenReturn(Optional.of(member));

        PointChangeResult result = this.service.earn(operation);

        assertThat(result.applied()).isFalse();
        assertThat(result.allowed()).isTrue();
        assertThat(result.balance().points()).isEqualTo(30);
        verify(this.memberRepository, never()).save(member);
        verify(this.transactionRepository, never()).save(
                ArgumentMatchers.any(BotGroupPointTransaction.class));
    }

    @Test
    void insufficientBalanceDoesNotPersistMemberOrTransaction() {
        PointOperation operation = operation("COMMAND_COST:101:201:message-1", 20);
        BotGroupMember member = member(GROUP_ID, 5);
        when(this.transactionRepository.existsByIdempotencyKey(operation.idempotencyKey()))
                .thenReturn(false);
        when(this.memberRepository.findByIdAndGroupId(MEMBER_ID, GROUP_ID))
                .thenReturn(Optional.of(member));

        PointChangeResult result = this.service.spend(operation);

        assertThat(result.applied()).isFalse();
        assertThat(result.allowed()).isFalse();
        assertThat(result.message()).contains("积分不足");
        assertThat(result.balance().points()).isEqualTo(5);
        verify(this.memberRepository, never()).save(member);
        verify(this.transactionRepository, never()).save(
                ArgumentMatchers.any(BotGroupPointTransaction.class));
    }

    @Test
    void accountLookupRejectsMemberFromAnotherLogicalGroup() {
        PointOperation operation = operation("COMMAND_REWARD:101:201:message-1", 10);
        when(this.transactionRepository.existsByIdempotencyKey(operation.idempotencyKey()))
                .thenReturn(false);
        when(this.memberRepository.findByIdAndGroupId(MEMBER_ID, GROUP_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.service.earn(operation))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("积分账户不存在");

        verify(this.memberRepository, never()).save(
                ArgumentMatchers.any(BotGroupMember.class));
        verify(this.transactionRepository, never()).save(
                ArgumentMatchers.any(BotGroupPointTransaction.class));
    }

    @Test
    void transactionRejectsDisabledAccountRegardlessOfEntrySnapshot() {
        PointOperation operation = operation("disabled", 10);
        BotGroupMember disabled = member(GROUP_ID, 100);
        disabled.setEnabled(false);
        when(memberRepository.findByIdAndGroupId(MEMBER_ID, GROUP_ID)).thenReturn(Optional.of(disabled));
        assertThatThrownBy(() -> service.spend(operation)).isInstanceOf(IllegalStateException.class);
        verify(memberRepository, never()).save(ArgumentMatchers.any());
        verify(transactionRepository, never()).save(ArgumentMatchers.any());
    }

    @Test
    void successfulRewardWritesPostSettlementBalanceToLedger() {
        PointOperation operation = operation("COMMAND_REWARD:101:201:message-2", 10);
        BotGroupMember member = member(GROUP_ID, 5);
        when(this.transactionRepository.existsByIdempotencyKey(operation.idempotencyKey()))
                .thenReturn(false);
        when(this.memberRepository.findByIdAndGroupId(MEMBER_ID, GROUP_ID))
                .thenReturn(Optional.of(member));
        when(this.transactionRepository.save(
                ArgumentMatchers.any(BotGroupPointTransaction.class)))
                .thenAnswer(invocation -> {
                    BotGroupPointTransaction saved = invocation.getArgument(0);
                    saved.setId(301L);
                    return saved;
                });

        PointChangeResult result = this.service.earn(operation);

        assertThat(result.applied()).isTrue();
        assertThat(result.balance().points()).isEqualTo(15);
        assertThat(result.balance().totalEarned()).isEqualTo(10);
        ArgumentCaptor<BotGroupPointTransaction> transaction =
                ArgumentCaptor.forClass(BotGroupPointTransaction.class);
        verify(this.transactionRepository).save(transaction.capture());
        assertThat(transaction.getValue().getAmount()).isEqualTo(10);
        assertThat(transaction.getValue().getBalanceAfter()).isEqualTo(15);
    }

    private static PointOperation operation(String idempotencyKey, long amount) {
        return new PointOperation(
                GROUP_ID,
                MEMBER_ID,
                "sign-in",
                "SIGN_IN",
                amount,
                idempotencyKey,
                "测试积分变化");
    }

    private static BotGroupMember member(long groupId, long points) {
        BotGroupMember member = new BotGroupMember();
        member.setId(MEMBER_ID);
        member.setGroupId(groupId);
        member.setPoints(points);
        return member;
    }
}
