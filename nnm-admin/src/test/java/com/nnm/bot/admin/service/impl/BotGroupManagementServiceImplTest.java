package com.nnm.bot.admin.service.impl;

import org.mockito.Mockito;
import com.nnm.bot.admin.domain.BotChannelInstance;
import com.nnm.bot.admin.dto.bot.BotGroupMemberPointsUpdateRequest;
import com.nnm.bot.admin.repository.BotChannelInstanceRepository;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotDiscoveredMember;
import com.nnm.bot.core.channel.model.BotDiscoveredScope;
import com.nnm.bot.core.channel.model.BotScopeDiscoveryResult;
import com.nnm.bot.core.channel.port.BotChannelInstanceProvider;
import com.nnm.bot.core.channel.port.BotScopeDiscoveryProvider;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.group.domain.BotGroup;
import com.nnm.bot.core.group.domain.BotGroupBot;
import com.nnm.bot.core.group.domain.BotGroupMember;
import com.nnm.bot.core.group.repository.BotGroupBotRepository;
import com.nnm.bot.core.group.repository.BotGroupMemberRepository;
import com.nnm.bot.core.group.repository.BotGroupPointTransactionRepository;
import com.nnm.bot.core.group.repository.BotGroupRepository;
import com.nnm.bot.core.group.service.PointAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;

@ExtendWith(MockitoExtension.class)
class BotGroupManagementServiceImplTest {

    @Mock
    private BotGroupRepository groupRepository;
    @Mock
    private BotGroupBotRepository botRepository;
    @Mock
    private BotGroupMemberRepository memberRepository;
    @Mock
    private BotGroupPointTransactionRepository pointTransactionRepository;
    @Mock
    private PointAccountService pointAccountService;
    @Mock
    private BotChannelInstanceRepository instanceRepository;
    @Mock
    private BotChannelInstanceProvider instanceProvider;
    @Mock
    private BotScopeDiscoveryProvider discoveryProvider;

    private BotGroupManagementServiceImpl service;

    @BeforeEach
    void setUp() {
        this.service = new BotGroupManagementServiceImpl();
        ReflectionTestUtils.setField(this.service, "memberDiscoveryExecutor", (Executor) Runnable::run);
        ReflectionTestUtils.setField(this.service, "groupRepository", this.groupRepository);
        ReflectionTestUtils.setField(this.service, "botRepository", this.botRepository);
        ReflectionTestUtils.setField(this.service, "memberRepository", this.memberRepository);
        ReflectionTestUtils.setField(
                this.service, "pointTransactionRepository", this.pointTransactionRepository);
        ReflectionTestUtils.setField(
                this.service, "pointAccountService", this.pointAccountService);
        ReflectionTestUtils.setField(this.service, "instanceRepository", this.instanceRepository);
        ReflectionTestUtils.setField(this.service, "instanceProvider", this.instanceProvider);
        ReflectionTestUtils.setField(
                this.service, "discoveryProviders", List.of(this.discoveryProvider));
    }

    @Test
    void shouldSynchronizeOnlyBoundOneBotScopesAndPreservePoints() {
        BotChannelInstance instance = oneBotInstance();
        BotChannelInstanceRecord record = oneBotRecord();
        BotGroupBot binding = new BotGroupBot();
        binding.setGroupId(9L);
        BotGroupMember existing = new BotGroupMember();
        existing.setGroupId(9L);
        existing.setQqId("10001");
        existing.setDisplayName("旧昵称");
        existing.setMemberRole("MEMBER");
        existing.setPoints(88);
        existing.setTotalEarned(120);
        existing.setTotalSpent(32);

        when(this.instanceProvider.findAll()).thenReturn(List.of(record));
        when(this.instanceRepository.findAllByOrderByPlatformAscInstanceIdAsc())
                .thenReturn(List.of(instance));
        when(this.discoveryProvider.platform()).thenReturn(BotPlatform.ONEBOT);
        when(this.discoveryProvider.discover(record)).thenReturn(discoveryResult());
        when(this.botRepository.findByPlatformAndScopeTypeAndScopeKeyAndBotInstanceId(
                "ONEBOT", "GROUP", "20001", 1L)).thenReturn(Optional.of(binding));
        when(this.botRepository.findByPlatformAndScopeTypeAndScopeKeyAndBotInstanceId(
                "ONEBOT", "GROUP", "20002", 1L)).thenReturn(Optional.empty());
        when(this.botRepository.findAllByPlatformAndScopeTypeAndScopeKeyAndBotInstanceId(
                "ONEBOT", "GROUP", "20001", 1L)).thenReturn(List.of(binding));
        when(this.botRepository.findAllByPlatformAndScopeTypeAndScopeKeyAndBotInstanceId(
                "ONEBOT", "GROUP", "20002", 1L)).thenReturn(List.of());
        when(this.memberRepository.findByGroupIdAndQqId(9L, "10001"))
                .thenReturn(Optional.of(existing));
        when(this.memberRepository.findByGroupIdAndQqId(9L, "10002"))
                .thenReturn(Optional.empty());

        var result = this.service.discoverAndSynchronize();

        assertThat(result).singleElement().satisfies(discovery ->
                assertThat(discovery.scopes())
                        .extracting(scope -> scope.added())
                        .containsExactly(true, false));
        ArgumentCaptor<BotGroupMember> members =
                ArgumentCaptor.forClass(BotGroupMember.class);
        verify(this.memberRepository, Mockito.times(2)).save(members.capture());
        assertThat(members.getAllValues()).satisfiesExactly(
                updated -> {
                    assertThat(updated).isSameAs(existing);
                    assertThat(updated.getDisplayName()).isEqualTo("新昵称");
                    assertThat(updated.getMemberRole()).isEqualTo("ADMIN");
                    assertThat(updated.getPoints()).isEqualTo(88);
                    assertThat(updated.getTotalEarned()).isEqualTo(120);
                    assertThat(updated.getTotalSpent()).isEqualTo(32);
                    assertThat(updated.isQqIdVerified()).isTrue();
                    assertThat(updated.getQqBoundAt()).isNotNull();
                },
                added -> {
                    assertThat(added.getGroupId()).isEqualTo(9L);
                    assertThat(added.getQqId()).isEqualTo("10002");
                    assertThat(added.getDisplayName()).isEqualTo("新成员");
                    assertThat(added.getMemberRole()).isEqualTo("MEMBER");
                    assertThat(added.isEnabled()).isTrue();
                });
    }

    @Test
    void shouldDeleteMissingOneBotMemberWhenFullSnapshotHasNoHistory() {
        BotGroupMember present = member(101L, "10001");
        BotGroupMember missing = member(102L, "10002");
        stubBoundOneBotScope(new BotDiscoveredScope(
                "GROUP",
                "20001",
                "已绑定群",
                1,
                List.of(new BotDiscoveredMember("10001", "仍在群内", "MEMBER")),
                true));
        when(this.memberRepository.findByGroupIdAndQqId(9L, "10001"))
                .thenReturn(Optional.of(present));
        when(this.memberRepository.findByGroupIdAndQqId(9L, "10002"))
                .thenReturn(Optional.of(missing));
        when(this.memberRepository.findAllByGroupIdOrderByMemberRoleAscIdAsc(9L))
                .thenReturn(List.of(present, missing));
        when(this.pointTransactionRepository.existsByMemberId(102L)).thenReturn(false);

        this.service.discoverAndSynchronize();

        assertThat(missing.getQqId()).isNull();
        verify(this.memberRepository).delete(missing);
        verify(this.memberRepository).save(present);
    }

    @Test
    void shouldNotDeleteMissingOneBotMemberWhenSnapshotIsIncomplete() {
        BotGroupMember present = member(101L, "10001");
        BotGroupMember missing = member(102L, "10002");
        stubBoundOneBotScope(new BotDiscoveredScope(
                "GROUP",
                "20001",
                "已绑定群",
                null,
                List.of(new BotDiscoveredMember("10001", "仍在群内", "MEMBER")),
                false));
        when(this.memberRepository.findByGroupIdAndQqId(9L, "10001"))
                .thenReturn(Optional.of(present));

        this.service.discoverAndSynchronize();

        verify(this.memberRepository).save(present);
        verify(this.memberRepository, never()).findAllByGroupIdOrderByMemberRoleAscIdAsc(9L);
        verify(this.memberRepository, never()).delete(missing);
    }

    @Test
    void shouldKeepAndDisableMissingOneBotMemberWithPointHistory() {
        BotGroupMember present = member(101L, "10001");
        BotGroupMember missing = member(102L, "10002");
        stubBoundOneBotScope(singleMemberScope(true));
        when(this.memberRepository.findByGroupIdAndQqId(9L, "10001"))
                .thenReturn(Optional.of(present));
        when(this.memberRepository.findByGroupIdAndQqId(9L, "10002"))
                .thenReturn(Optional.of(missing));
        when(this.memberRepository.findAllByGroupIdOrderByMemberRoleAscIdAsc(9L))
                .thenReturn(List.of(present, missing));
        when(this.pointTransactionRepository.existsByMemberId(102L)).thenReturn(true);

        this.service.discoverAndSynchronize();

        assertThat(missing.getQqId()).isNull();
        assertThat(missing.isEnabled()).isFalse();
        verify(this.memberRepository).save(missing);
        verify(this.memberRepository, never()).delete(missing);
    }

    @Test
    void shouldKeepAndDisableMissingOneBotMemberWithCurrentPoints() {
        BotGroupMember present = member(101L, "10001");
        BotGroupMember missing = member(102L, "10002");
        missing.setPoints(3);
        stubBoundOneBotScope(singleMemberScope(true));
        when(this.memberRepository.findByGroupIdAndQqId(9L, "10001"))
                .thenReturn(Optional.of(present));
        when(this.memberRepository.findByGroupIdAndQqId(9L, "10002"))
                .thenReturn(Optional.of(missing));
        when(this.memberRepository.findAllByGroupIdOrderByMemberRoleAscIdAsc(9L))
                .thenReturn(List.of(present, missing));
        when(this.pointTransactionRepository.existsByMemberId(102L)).thenReturn(false);

        this.service.discoverAndSynchronize();

        assertThat(missing.getQqId()).isNull();
        assertThat(missing.isEnabled()).isFalse();
        verify(this.memberRepository).save(missing);
        verify(this.memberRepository, never()).delete(missing);
    }

    @Test
    void shouldDelegateMemberPointUpdateToPointAccountService() {
        BotGroup group = new BotGroup();
        group.setId(9L);
        group.setEnabled(true);
        when(this.groupRepository.findById(9L)).thenReturn(Optional.of(group));

        this.service.updateMemberPoints(
                9L, 101L, new BotGroupMemberPointsUpdateRequest(77L));

        verify(this.pointAccountService).adjust(
                eq(9L),
                eq(101L),
                eq(77L),
                startsWith("ADMIN_ADJUSTMENT:9:101:"),
                eq("后台管理员调整成员积分"));
    }

    private void stubBoundOneBotScope(BotDiscoveredScope scope) {
        BotGroupBot binding = new BotGroupBot();
        binding.setGroupId(9L);
        BotChannelInstanceRecord record = oneBotRecord();

        when(this.instanceProvider.findAll()).thenReturn(List.of(record));
        when(this.instanceRepository.findAllByOrderByPlatformAscInstanceIdAsc())
                .thenReturn(List.of(oneBotInstance()));
        when(this.discoveryProvider.platform()).thenReturn(BotPlatform.ONEBOT);
        when(this.discoveryProvider.discover(record)).thenReturn(
                new BotScopeDiscoveryResult(true, null, List.of(scope)));
        when(this.botRepository.findByPlatformAndScopeTypeAndScopeKeyAndBotInstanceId(
                "ONEBOT", "GROUP", "20001", 1L)).thenReturn(Optional.of(binding));
        when(this.botRepository.findAllByPlatformAndScopeTypeAndScopeKeyAndBotInstanceId(
                "ONEBOT", "GROUP", "20001", 1L)).thenReturn(List.of(binding));
    }

    private static BotDiscoveredScope singleMemberScope(boolean complete) {
        return new BotDiscoveredScope(
                "GROUP",
                "20001",
                "已绑定群",
                1,
                List.of(new BotDiscoveredMember("10001", "仍在群内", "MEMBER")),
                complete);
    }

    private static BotGroupMember member(long id, String qqId) {
        BotGroupMember member = new BotGroupMember();
        member.setId(id);
        member.setGroupId(9L);
        member.setQqId(qqId);
        member.setMemberRole("MEMBER");
        member.setEnabled(true);
        return member;
    }

    private static BotChannelInstance oneBotInstance() {
        BotChannelInstance instance = new BotChannelInstance();
        instance.setId(1L);
        instance.setPlatform(BotPlatform.ONEBOT);
        instance.setInstanceId("onebot-main");
        instance.setDisplayName("OneBot 主实例");
        instance.setBotAccountId("123456");
        return instance;
    }

    private static BotChannelInstanceRecord oneBotRecord() {
        return new BotChannelInstanceRecord(
                BotPlatform.ONEBOT,
                "onebot-main",
                "123456",
                true,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                null,
                null);
    }

    private static BotScopeDiscoveryResult discoveryResult() {
        return new BotScopeDiscoveryResult(
                true,
                null,
                List.of(
                        new BotDiscoveredScope(
                                "GROUP",
                                "20001",
                                "已绑定群",
                                2,
                                List.of(
                                        new BotDiscoveredMember("10001", "新昵称", "ADMIN"),
                                        new BotDiscoveredMember("10002", "新成员", "MEMBER"))),
                        new BotDiscoveredScope(
                                "GROUP",
                                "20002",
                                "未绑定群",
                                1,
                                List.of(new BotDiscoveredMember(
                                        "10003", "不应入库", "MEMBER")))));
    }
}
