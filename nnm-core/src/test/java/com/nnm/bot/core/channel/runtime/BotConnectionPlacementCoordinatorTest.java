package com.nnm.bot.core.channel.runtime;

import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.port.BotChannelInstanceProvider;
import com.nnm.bot.core.channel.topology.BotTopologyLoader;
import com.nnm.bot.core.channel.topology.BotTopologySnapshot;
import com.nnm.bot.core.channel.runtime.BotConnectionPlacementCoordinator.Approval;
import com.nnm.bot.core.channel.runtime.BotConnectionPlacementCoordinator.Plan;
import java.util.HashMap;
import java.util.ArrayList;
import com.nnm.bot.core.config.ClusterProperties;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.group.domain.BotGroupBot;
import com.nnm.bot.core.group.domain.BotGroup;
import com.nnm.bot.core.group.repository.BotGroupRepository;
import com.nnm.bot.core.group.repository.BotGroupBotRepository;
import io.github.rdt.state.SharedStateStore;
import io.github.rdt.redisson.lease.RedisOwnerLeaseService;
import io.github.rdt.redisson.lease.RedisOwnerLeaseService.Lease;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BotConnectionPlacementCoordinatorTest {
    private final BotConnectionPlacementCoordinator coordinator = new BotConnectionPlacementCoordinator();
    private final BotConnectionAffinityResolver resolver = mock(BotConnectionAffinityResolver.class);
    private final BotChannelInstanceProvider instances = mock(BotChannelInstanceProvider.class);
    private final BotGroupBotRepository bindings = mock(BotGroupBotRepository.class);
    private final BotGroupRepository groups = mock(BotGroupRepository.class);
    private final RedisOwnerLeaseService leases = mock(RedisOwnerLeaseService.class);
    private final SharedStateStore stateStore = mock(SharedStateStore.class);
    private final Lease a = new Lease("bot:ONEBOT:a", "node-a:1:a", 1);
    private final Lease b = new Lease("bot:ONEBOT:b", "node-b:1:b", 1);
    private final Lease node = new Lease("node:node-a", "node-a:1:node", 1);
    private final List<BotChannelInstanceRecord> records = List.of(record("a"), record("b"));
    private final BotGroupBot candidate = new BotGroupBot();

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(coordinator, "instances", instances);
        ReflectionTestUtils.setField(coordinator, "bindings", bindings);
        ReflectionTestUtils.setField(coordinator, "groups", groups);
        ReflectionTestUtils.setField(coordinator, "resolver", resolver);
        ReflectionTestUtils.setField(coordinator, "leases", leases);
        ReflectionTestUtils.setField(coordinator, "stateStore", stateStore);
        ReflectionTestUtils.setField(coordinator, "properties", mock(ClusterProperties.class));
        candidate.setGroupId(1L);
        candidate.setBotInstanceId(2L);
        candidate.setPlatform("ONEBOT");
        candidate.setScopeKey("group-2");
        candidate.setScopeType("GROUP");
        BotTopologyLoader loader = mock(BotTopologyLoader.class);
        when(loader.load()).thenAnswer(call -> {
            BotGroup enabled = groups.findById(1L).orElseThrow();
            enabled.setId(1L);
            List<BotGroup> supplied = groups.findAll();
            if (!supplied.isEmpty()) enabled = supplied.getFirst();
            List<BotGroupBot> existing = new ArrayList<>();
            BotGroupBot first = new BotGroupBot();
            first.setId(9L);
            first.setGroupId(1L);
            first.setBotInstanceId(1L);
            first.setPlatform("ONEBOT");
            first.setScopeType("GROUP");
            first.setScopeKey("group-1");
            existing.add(first);
            if (candidate.getId() != null || !enabled.isEnabled()) {
                candidate.setId(10L);
                existing.add(candidate);
            }
            return BotTopologySnapshot.of(Map.of(1L, records.getFirst(), 2L, records.getLast()), List.of(enabled), existing);
        });
        ReflectionTestUtils.setField(coordinator, "topologyLoader", loader);
        when(resolver.resolve(anyMap(), anyList(), anySet())).thenAnswer(call -> {
            Set<Long> enabled = call.<Set<Long>>getArgument(2);
            List<BotGroupBot> proposed = call.getArgument(1);
            return Map.of("a", "a", "b", enabled.contains(1L) && proposed.stream()
                    .anyMatch(binding -> binding.getBotInstanceId() == 2L) ? "a" : "b");
        });
        when(leases.findAll(anyCollection())).thenAnswer(call -> {
            Map<String, Lease> current = new HashMap<>();
            for (String name : call.<java.util.Collection<String>>getArgument(0)) leases.find(name).ifPresent(lease -> current.put(name, lease));
            return current;
        });
        when(instances.findAll()).thenReturn(records);
        BotGroup group = new BotGroup();
        group.setEnabled(true);
        when(groups.findById(1L)).thenReturn(Optional.of(group));
        when(resolver.instanceId(records, 2L)).thenReturn(Optional.of("b"));
        when(resolver.resolve(records)).thenReturn(Map.of("a", "a", "b", "b"));
        when(resolver.resolve(eq(records), anyList())).thenReturn(Map.of("a", "a", "b", "a"));
        when(leases.find(a.name())).thenReturn(Optional.of(a));
        when(leases.find(b.name())).thenReturn(Optional.of(b));
        when(leases.find(node.name())).thenReturn(Optional.of(node));
    }

    @Test
    void sameNodeBindingNeedsNoConfirmationAndDoesNotTouchConnectionsOrRedisState() {
        when(leases.find(b.name())).thenReturn(Optional.of(new Lease(b.name(), "node-a:2:b", 2)));
        assertThat(coordinator.preview(candidate).moves()).isEmpty();
        assertThat(coordinator.prepare(candidate, null).transfers()).isEmpty();
        verifyNoInteractions(stateStore);
        verify(bindings, never()).save(any());
    }

    @Test
    void crossNodePreviewIsReadOnlyAndSaveRequiresTheCurrentConfirmationToken() {
        Plan preview = coordinator.preview(candidate);
        assertThat(preview.moves()).containsExactly(new BotConnectionPlacementCoordinator.Move("b", "node-b", "node-a"));
        verifyNoInteractions(stateStore);
        verify(bindings, never()).save(any());
        assertThatThrownBy(() -> coordinator.prepare(candidate, null)).isInstanceOf(BusinessException.class);
        assertThat(coordinator.prepare(candidate, preview.confirmationToken())).isEqualTo(preview);
        when(leases.find(b.name())).thenReturn(Optional.of(new Lease(b.name(), "node-c:2:new", 2)));
        assertThatThrownBy(() -> coordinator.prepare(candidate, preview.confirmationToken()))
                .isInstanceOf(BusinessException.class).hasMessageContaining("最新迁移方案");
    }

    @Test
    void anAlreadySavedBindingCanRequestANewPlanAfterOwnershipChanged() {
        when(resolver.resolve(records)).thenReturn(Map.of("a", "a", "b", "a"));
        candidate.setId(10L);
        Plan preview = coordinator.preview(candidate);
        assertThat(preview.moves()).containsExactly(new BotConnectionPlacementCoordinator.Move("b", "node-b", "node-a"));
        assertThatThrownBy(() -> coordinator.prepare(candidate, null)).isInstanceOf(BusinessException.class);
    }

    @Test
    void approvalCannotMoveConnectionsBeforeBindingCommitOrAfterOwnersChange() {
        Plan preview = coordinator.preview(candidate);
        Approval approval = new BotConnectionPlacementCoordinator.Approval(10L, null, List.of("a", "b"), node,
                preview.transfers().getFirst().owners());
        when(stateStore.get(anyString(), eq(BotConnectionPlacementCoordinator.Approval.class)))
                .thenReturn(Optional.of(approval));
        assertThat(coordinator.approvedTarget("a", List.of("a", "b"))).isEmpty();
        when(bindings.existsById(10L)).thenReturn(true);
        when(leases.isHeld(node)).thenReturn(true);
        assertThat(coordinator.approvedTarget("a", List.of("a", "b"))).contains("node-a");
        when(leases.find(b.name())).thenReturn(Optional.of(new Lease(b.name(), "node-c:2:new", 2)));
        assertThat(coordinator.approvedTarget("a", List.of("a", "b"))).isEmpty();
        assertThat(coordinator.approvedTarget("a", List.of("a", "b", "c"))).isEmpty();
    }

    @Test
    void enablingAnExistingGroupUsesTheSameConfirmationPolicy() {
        BotGroup group = new BotGroup();
        group.setId(1L);
        group.setEnabled(false);
        when(groups.findAll()).thenReturn(List.of(group));
        when(resolver.resolve(eq(records), anyList(), eq(Set.of(1L))))
                .thenReturn(Map.of("a", "a", "b", "a"));
        Plan plan = coordinator.previewGroup(1L, true);
        assertThat(plan.moves()).hasSize(1);
        assertThat(group.isEnabled()).isFalse();
        assertThatThrownBy(() -> coordinator.prepareGroup(1L, true, null)).isInstanceOf(BusinessException.class);
        assertThat(coordinator.prepareGroup(1L, true, plan.confirmationToken())).isEqualTo(plan);
        verify(groups, never()).save(any());
    }

    @Test
    void redisFailuresArePropagatedInsteadOfBeingTreatedAsOfflineOwners() {
        IllegalStateException failure = new IllegalStateException("redis unavailable");
        when(leases.find(a.name())).thenThrow(failure);
        assertThatThrownBy(() -> coordinator.preview(candidate)).isSameAs(failure);
    }

    private static BotChannelInstanceRecord record(String id) {
        return new BotChannelInstanceRecord(BotPlatform.ONEBOT, id, id, true,
                null, null, null, null, null, null, null, null, 0, null, null);
    }
}
