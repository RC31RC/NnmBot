package com.nnm.bot.admin.service.impl;

import com.nnm.bot.admin.service.bot.group.impl.BotGroupManagementServiceImpl;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeDiscoveryDTO;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.mockito.Mockito;
import com.nnm.bot.admin.pojo.entity.bot.channel.BotChannelInstance;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupMemberPointsUpdateDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupBotUpdateDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupBotSaveDTO;
import com.nnm.bot.core.channel.topology.BotTopologyInvalidator;
import com.nnm.bot.admin.repository.bot.channel.BotChannelInstanceRepository;
import com.nnm.bot.common.exception.DataNotFoundException;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotDiscoveredMember;
import com.nnm.bot.core.channel.model.BotDiscoveredScope;
import com.nnm.bot.core.channel.model.BotMemberQueryResult;
import com.nnm.bot.core.channel.model.BotScopeDiscoveryResult;
import com.nnm.bot.core.channel.port.BotChannelInstanceProvider;
import com.nnm.bot.core.channel.runtime.BotScopeDiscoveryRouter;
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
import org.springframework.beans.factory.ObjectProvider;
import com.nnm.bot.core.channel.runtime.BotConnectionPlacementCoordinator;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.constant.BotChannelErrorCode;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import jakarta.validation.Validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.ArgumentMatchers.isNull;
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
    private BotScopeDiscoveryRouter discoveryRouter;

    private BotGroupManagementServiceImpl service;

    @BeforeEach
    void setUp() {
        this.service = new BotGroupManagementServiceImpl();
        ReflectionTestUtils.setField(service, "topologyInvalidator", mock(BotTopologyInvalidator.class));
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
        ReflectionTestUtils.setField(this.service, "discoveryRouter", this.discoveryRouter);
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
        when(this.discoveryRouter.discover(record)).thenReturn(discoveryResult());
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

        List<BotScopeDiscoveryDTO> result = this.service.discoverAndSynchronize();

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
                9L, 101L, new BotGroupMemberPointsUpdateDTO(77L));

        verify(this.pointAccountService).adjust(
                eq(9L),
                eq(101L),
                eq(77L),
                startsWith("ADMIN_ADJUSTMENT:9:101:"),
                eq("后台管理员调整成员积分"));
    }

    @Test
    void shouldUpdateReplyPriorityAndEnabledStateWithoutChangingBinding() {
        BotGroup group = new BotGroup();
        group.setId(9L);
        BotGroupBot binding = new BotGroupBot();
        binding.setId(7L);
        binding.setGroupId(9L);
        binding.setBotInstanceId(1L);
        binding.setScopeKey("20001");
        when(groupRepository.findById(9L)).thenReturn(Optional.of(group));
        when(botRepository.findById(7L)).thenReturn(Optional.of(binding));

        service.updateBot(9L, 7L, new BotGroupBotUpdateDTO(false, 10));

        ArgumentCaptor<BotGroupBot> saved = ArgumentCaptor.forClass(BotGroupBot.class);
        verify(botRepository).save(saved.capture());
        assertThat(saved.getValue().isEnabled()).isFalse();
        assertThat(saved.getValue().getReplyPriority()).isEqualTo(10);
        assertThat(saved.getValue().getBotInstanceId()).isEqualTo(1L);
        assertThat(saved.getValue().getScopeKey()).isEqualTo("20001");
    }

    @Test
    void shouldRejectReplySettingsForAnotherLogicalGroup() {
        BotGroup group = new BotGroup();
        group.setId(9L);
        BotGroupBot binding = new BotGroupBot();
        binding.setGroupId(10L);
        when(groupRepository.findById(9L)).thenReturn(Optional.of(group));
        when(botRepository.findById(7L)).thenReturn(Optional.of(binding));

        assertThatThrownBy(() -> service.updateBot(9L, 7L, new BotGroupBotUpdateDTO(true, 10)))
                .isInstanceOf(DataNotFoundException.class);

        verify(botRepository, never()).save(any());
    }

    @Test
    void shouldValidateReplyPriorityRangeAndRequireEnabledState() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            assertThat(validator.validate(new BotGroupBotUpdateDTO(true, 0))).isEmpty();
            assertThat(validator.validate(new BotGroupBotUpdateDTO(false, 10000))).isEmpty();
            assertThat(validator.validate(new BotGroupBotUpdateDTO(true, -1))).hasSize(1);
            assertThat(validator.validate(new BotGroupBotUpdateDTO(true, 10001))).hasSize(1);
            assertThat(validator.validate(new BotGroupBotUpdateDTO(null, 100))).hasSize(1);
        }
    }

    @Test
    void shouldNotSaveBindingOrQueryMembersWhenMigrationIsNotConfirmed() {
        BotGroup group = new BotGroup();
        group.setId(9L);
        BotChannelInstance instance = oneBotInstance();
        when(groupRepository.findById(9L)).thenReturn(Optional.of(group));
        when(instanceRepository.findById(instance.getId())).thenReturn(Optional.of(instance));
        BotConnectionPlacementCoordinator coordinator = mock(BotConnectionPlacementCoordinator.class);
        ObjectProvider<BotConnectionPlacementCoordinator> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(coordinator);
        ReflectionTestUtils.setField(service, "placementCoordinator", provider);
        doThrow(new BusinessException(BotChannelErrorCode.CONNECTION_MIGRATION_CONFIRMATION_REQUIRED))
                .when(coordinator).prepare(any(), isNull());

        assertThatThrownBy(() -> service.bindBot(9L, new BotGroupBotSaveDTO(instance.getId(),
                BotPlatform.ONEBOT, "GROUP", "20001", null, true, true)))
                .isInstanceOf(BusinessException.class).hasMessageContaining("管理员确认");

        verify(botRepository, never()).save(any());
        verifyNoInteractions(discoveryRouter, memberRepository);
    }

    @Test
    void shouldRejectAnotherOfficialInstanceEvenWhenTheExistingBindingIsDisabled() {
        BotGroup group = new BotGroup();
        group.setId(9L);
        BotChannelInstance instance = new BotChannelInstance();
        instance.setId(2L);
        instance.setPlatform(BotPlatform.QQ_OFFICIAL);
        when(groupRepository.findById(9L)).thenReturn(Optional.of(group));
        when(instanceRepository.findById(2L)).thenReturn(Optional.of(instance));
        BotGroupBot existing = new BotGroupBot();
        existing.setBotInstanceId(3L);
        existing.setPlatform("QQ_OFFICIAL");
        existing.setScopeType("GROUP");
        existing.setEnabled(false);
        when(botRepository.findAllByGroupIdOrderByPlatformAscScopeKeyAsc(9L)).thenReturn(List.of(existing));
        assertThatThrownBy(() -> service.bindBot(9L, new BotGroupBotSaveDTO(2L, BotPlatform.QQ_OFFICIAL,
                "GROUP", "openid", null, true, false)))
                .isInstanceOf(BusinessException.class).hasMessageContaining("QQ 官方");
        verify(botRepository, never()).save(any());
        verifyNoInteractions(discoveryRouter, memberRepository);
    }

    @Test
    void shouldBindDiscoveredQqGroupToExistingLogicalGroupAndSynchronizeObservedMembers() {
        BotGroup group = new BotGroup();
        group.setId(9L);
        BotChannelInstance instance = new BotChannelInstance();
        instance.setId(2L);
        instance.setPlatform(BotPlatform.QQ_OFFICIAL);
        instance.setInstanceId("qq-main");
        instance.setBotAccountId("app-1");
        BotChannelInstanceRecord record = new BotChannelInstanceRecord(BotPlatform.QQ_OFFICIAL, "qq-main", "app-1",
                true, null, null, "app-1", "secret", null, null, null, null,
                117440512, null, null);
        BotDiscoveredScope scope = new BotDiscoveredScope("GROUP", "group-openid", "发现的群组", null,
                List.of(new BotDiscoveredMember("member-openid", "成员", "MEMBER")), false);
        when(groupRepository.findById(9L)).thenReturn(Optional.of(group));
        when(instanceRepository.findById(2L)).thenReturn(Optional.of(instance));
        when(instanceProvider.findAll()).thenReturn(List.of(record));
        when(discoveryRouter.discover(record))
                .thenReturn(new BotScopeDiscoveryResult(true, "观察结果", List.of(scope)));
        when(discoveryRouter.queryMembers(record, "group-openid"))
                .thenReturn(new BotMemberQueryResult(List.of(), false));

        service.bindBot(9L, new BotGroupBotSaveDTO(2L, BotPlatform.QQ_OFFICIAL,
                " group ", " group-openid ", "发现的群组", true, true));

        ArgumentCaptor<BotGroupBot> binding = ArgumentCaptor.forClass(BotGroupBot.class);
        verify(botRepository).save(binding.capture());
        assertThat(binding.getValue().getGroupId()).isEqualTo(9L);
        assertThat(binding.getValue().getBotInstanceId()).isEqualTo(2L);
        assertThat(binding.getValue().getPlatform()).isEqualTo("QQ_OFFICIAL");
        assertThat(binding.getValue().getScopeType()).isEqualTo("GROUP");
        assertThat(binding.getValue().getScopeKey()).isEqualTo("group-openid");
        assertThat(binding.getValue().getDisplayName()).isEqualTo("发现的群组");
        ArgumentCaptor<BotGroupMember> member = ArgumentCaptor.forClass(BotGroupMember.class);
        verify(memberRepository).save(member.capture());
        assertThat(member.getValue().getGroupId()).isEqualTo(9L);
        assertThat(member.getValue().getMemberOpenid()).isEqualTo("member-openid");
        assertThat(member.getValue().getQqId()).isNull();
        verify(groupRepository, never()).save(any());
        verify(memberRepository, never()).findAllByGroupIdOrderByMemberRoleAscIdAsc(any());
    }

    @Test
    void shouldClearOnlyQqOfficialIdentityWhenMemberLeaves() {
        BotChannelInstance instance = new BotChannelInstance();
        instance.setId(2L);
        instance.setPlatform(BotPlatform.QQ_OFFICIAL);
        instance.setBotAccountId("app-1");
        BotGroupBot binding = new BotGroupBot();
        binding.setGroupId(9L);
        BotGroupMember member = member(101L, "10001");
        member.setMemberOpenid("member-openid");
        when(instanceRepository.findByPlatformAndBotAccountId(BotPlatform.QQ_OFFICIAL, "app-1"))
                .thenReturn(Optional.of(instance));
        when(botRepository.findAllByPlatformAndScopeTypeAndScopeKeyAndBotInstanceId(
                "QQ_OFFICIAL", "GROUP", "group-openid", 2L)).thenReturn(List.of(binding));
        when(memberRepository.findByGroupIdAndMemberOpenid(9L, "member-openid"))
                .thenReturn(Optional.of(member));

        service.handleMemberLeft(BotPlatform.QQ_OFFICIAL, "app-1", "group-openid", "member-openid");

        assertThat(member.getMemberOpenid()).isNull();
        assertThat(member.getQqId()).isEqualTo("10001");
        assertThat(member.isEnabled()).isTrue();
        verify(memberRepository).save(member);
        verify(memberRepository, never()).delete(member);
    }

    private void stubBoundOneBotScope(BotDiscoveredScope scope) {
        BotGroupBot binding = new BotGroupBot();
        binding.setGroupId(9L);
        BotChannelInstanceRecord record = oneBotRecord();

        when(this.instanceProvider.findAll()).thenReturn(List.of(record));
        when(this.instanceRepository.findAllByOrderByPlatformAscInstanceIdAsc())
                .thenReturn(List.of(oneBotInstance()));
        when(this.discoveryRouter.discover(record)).thenReturn(
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
