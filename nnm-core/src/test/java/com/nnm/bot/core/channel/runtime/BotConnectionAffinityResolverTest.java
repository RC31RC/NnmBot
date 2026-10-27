package com.nnm.bot.core.channel.runtime;

import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.topology.BotTopologySnapshot;
import com.nnm.bot.core.channel.topology.BotTopologyStore;
import java.util.Map;
import com.nnm.bot.core.channel.port.BotChannelInstanceIdentityResolver;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.group.domain.BotGroup;
import com.nnm.bot.core.group.domain.BotGroupBot;
import com.nnm.bot.core.group.repository.BotGroupBotRepository;
import com.nnm.bot.core.group.repository.BotGroupRepository;
import java.util.List;
import java.util.OptionalLong;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BotConnectionAffinityResolverTest {
    @Test
    void overlappingGroupsAreColocatedTransitivelyWhileDisabledBindingsAreIgnored() {
        BotConnectionAffinityResolver resolver = new BotConnectionAffinityResolver();
        BotGroupBotRepository bindings = mock(BotGroupBotRepository.class);
        BotGroupRepository groups = mock(BotGroupRepository.class);
        BotChannelInstanceIdentityResolver identities = mock(BotChannelInstanceIdentityResolver.class);
        when(groups.findAll()).thenReturn(List.of(group(1, true), group(2, true), group(3, false)));
        BotGroupBot disabled = binding(2, 4);
        disabled.setEnabled(false);
        when(bindings.findAll()).thenReturn(List.of(binding(1, 1), binding(1, 2),
                binding(2, 2), binding(2, 3), disabled, binding(3, 3), binding(3, 4)));
        for (int id = 1; id <= 4; id++) {
            when(identities.resolveId(BotPlatform.ONEBOT, "bot-" + id)).thenReturn(OptionalLong.of(id));
        }

        BotTopologyStore topology = mock(BotTopologyStore.class);
        List<BotGroupBot> data = bindings.findAll();
        for (int index = 0; index < data.size(); index++) data.get(index).setId((long) index + 1);
        BotTopologySnapshot snapshot = BotTopologySnapshot.of(Map.of(1L, record(1), 2L, record(2),
                3L, record(3), 4L, record(4)), groups.findAll(), data);
        when(topology.snapshot()).thenReturn(snapshot);
        ReflectionTestUtils.setField(resolver, "topology", topology);

        assertThat(resolver.resolve(List.of(record(1), record(2), record(3), record(4))))
                .containsEntry("bot-1", "bot-1").containsEntry("bot-2", "bot-1")
                .containsEntry("bot-3", "bot-1").containsEntry("bot-4", "bot-4");
    }

    private static BotGroup group(long id, boolean enabled) {
        BotGroup group = new BotGroup();
        group.setId(id);
        group.setEnabled(enabled);
        return group;
    }

    private static BotGroupBot binding(long groupId, long botId) {
        BotGroupBot binding = new BotGroupBot();
        binding.setGroupId(groupId);
        binding.setBotInstanceId(botId);
        binding.setPlatform("ONEBOT");
        binding.setScopeKey("group-" + groupId);
        return binding;
    }

    private static BotChannelInstanceRecord record(int id) {
        return new BotChannelInstanceRecord(BotPlatform.ONEBOT, "bot-" + id, "bot-" + id, true,
                null, null, null, null, null, null, null, null, 0, null, null);
    }
}
