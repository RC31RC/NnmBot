package com.nnm.bot.admin.service.impl;

import com.nnm.bot.admin.service.bot.role.impl.BotRobotRoleManagementServiceImpl;
import org.mockito.ArgumentMatchers;
import io.github.rdt.lock.DistributedLockManager;
import com.nnm.bot.admin.pojo.entity.bot.channel.BotChannelInstance;
import com.nnm.bot.admin.pojo.entity.bot.role.BotRobotRole;
import com.nnm.bot.admin.pojo.entity.bot.role.BotRobotRoleInstance;
import com.nnm.bot.admin.repository.bot.channel.BotChannelInstanceRepository;
import com.nnm.bot.admin.repository.bot.command.BotCommandRepository;
import com.nnm.bot.admin.repository.bot.command.BotCommandRobotRoleRepository;
import com.nnm.bot.admin.repository.bot.role.BotRobotRoleInstanceRepository;
import com.nnm.bot.admin.repository.bot.role.BotRobotRoleRepository;
import com.nnm.bot.core.plugin.registry.CommandRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BotRobotRoleManagementServiceImplTest {

    @Mock
    private BotRobotRoleRepository roleRepository;
    @Mock
    private BotRobotRoleInstanceRepository instanceBindingRepository;
    @Mock
    private BotCommandRobotRoleRepository commandBindingRepository;
    @Mock
    private BotChannelInstanceRepository instanceRepository;
    @Mock
    private BotCommandRepository commandRepository;
    @Mock
    private CommandRegistry commandRegistry;
    @Mock
    private DistributedLockManager lockManager;
    @InjectMocks
    private BotRobotRoleManagementServiceImpl service;

    @Test
    void shouldNotRewriteUnchangedInstanceRoles() {
        BotChannelInstance instance = new BotChannelInstance();
        BotRobotRole role = role(1L);
        BotRobotRoleInstance binding = binding(1L, 1L);
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(roleRepository.findAllById(anySet())).thenReturn(List.of(role));
        when(instanceBindingRepository.findAllByBotInstanceId(1L))
                .thenReturn(List.of(binding));

        service.assignInstanceRoles(1L, List.of(1L));

        verify(instanceBindingRepository, never()).deleteAll(anyList());
        verify(instanceBindingRepository, never()).saveAll(anyList());
    }

    @Test
    void shouldDeduplicateRequestedRoleIds() {
        BotChannelInstance instance = new BotChannelInstance();
        BotRobotRole role = role(1L);
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(roleRepository.findAllById(anySet())).thenReturn(List.of(role));
        when(instanceBindingRepository.findAllByBotInstanceId(1L))
                .thenReturn(List.of());

        service.assignInstanceRoles(1L, List.of(1L, 1L));

        verify(instanceBindingRepository).saveAll(ArgumentMatchers.argThat(
                bindings -> containsSingleBinding(bindings, 1L, 1L)));
    }

    @Test
    void shouldSynchronizeAddedAndRemovedInstanceRoles() {
        BotChannelInstance instance = new BotChannelInstance();
        BotRobotRole retainedRole = role(2L);
        BotRobotRole addedRole = role(3L);
        BotRobotRoleInstance removed = binding(1L, 1L);
        BotRobotRoleInstance retained = binding(2L, 1L);
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(roleRepository.findAllById(anySet()))
                .thenReturn(List.of(retainedRole, addedRole));
        when(instanceBindingRepository.findAllByBotInstanceId(1L))
                .thenReturn(List.of(removed, retained));

        service.assignInstanceRoles(1L, List.of(2L, 3L));

        verify(instanceBindingRepository).deleteAll(List.of(removed));
        verify(instanceBindingRepository).flush();
        verify(instanceBindingRepository).saveAll(ArgumentMatchers.argThat(
                bindings -> containsSingleBinding(bindings, 3L, 1L)));
    }

    private static BotRobotRole role(long id) {
        BotRobotRole role = new BotRobotRole();
        role.setId(id);
        return role;
    }

    private static BotRobotRoleInstance binding(long roleId, long instanceId) {
        BotRobotRoleInstance binding = new BotRobotRoleInstance();
        binding.setRobotRoleId(roleId);
        binding.setBotInstanceId(instanceId);
        return binding;
    }

    private static boolean containsSingleBinding(
            Iterable<BotRobotRoleInstance> bindings,
            long roleId,
            long instanceId) {
        List<BotRobotRoleInstance> values = StreamSupport.stream(
                        bindings.spliterator(), false)
                .toList();
        return values.size() == 1
                && values.getFirst().getRobotRoleId().equals(roleId)
                && values.getFirst().getBotInstanceId().equals(instanceId);
    }
}
