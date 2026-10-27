package com.nnm.bot.core.channel.runtime;

import com.nnm.bot.core.channel.topology.BotTopologySnapshot.Binding;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotChannelInstanceMetadata;
import com.nnm.bot.core.channel.topology.BotTopologySnapshot;
import com.nnm.bot.core.channel.model.BotChannelTarget;
import com.nnm.bot.core.channel.registry.BotChannelRegistry;
import com.nnm.bot.core.channel.topology.BotTopologyStore;
import com.nnm.bot.core.constant.BotChannelCapability;
import com.nnm.bot.core.constant.BotChannelStatus;
import com.nnm.bot.core.group.domain.BotGroupBot;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/** Shared pre-delivery selection; callers add command and conversation-specific checks. */
@Component
public class BotChannelTargetSelector {

    @Resource
    private BotChannelRegistry registry;
    @Resource
    private ObjectProvider<ClusterBotRuntime> clusterProvider;
    @Resource
    private BotTopologyStore topology;

    public List<BotChannelTarget> candidates(long groupId) {
        BotTopologySnapshot snapshot = topology.snapshot();
        return candidatesInternal(snapshot.groupBindings(groupId), snapshot.metadata(), Set.of(), snapshot);
    }

    public List<BotChannelTarget> candidates(long groupId, List<Binding> bindings) {
        BotTopologySnapshot snapshot = topology.snapshot();
        return candidatesInternal(bindings.stream().map(BotTopologySnapshot.Binding::entity)
                .filter(binding -> binding.getGroupId() == groupId).toList(), snapshot.metadata(), Set.of(), snapshot);
    }

    public List<BotChannelTarget> candidates(List<BotGroupBot> values, Map<Long, BotChannelInstanceRecord> records) {
        return candidates(values, records, Set.of());
    }

    public List<BotChannelTarget> candidates(
            List<BotGroupBot> values,
            Map<Long, BotChannelInstanceRecord> records,
            Set<BotChannelCapability> requiredCapabilities) {
        Map<Long, BotChannelInstanceMetadata> metadata = records.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey, entry -> BotChannelInstanceMetadata.of(entry.getValue())));
        return candidatesInternal(values, metadata, requiredCapabilities, topology.snapshot());
    }

    public Map<Long, List<BotChannelTarget>> candidates(Collection<Long> groupIds) {
        if (groupIds.isEmpty()) return Map.of();
        BotTopologySnapshot snapshot = topology.snapshot();
        return candidatesInternal(groupIds.stream().flatMap(id -> snapshot.groupBindings(id).stream()).toList(),
                snapshot.metadata(), Set.of(), snapshot).stream()
                .collect(Collectors.groupingBy(target -> target.binding().getGroupId()));
    }

    public boolean supports(BotChannelTarget target, Set<BotChannelCapability> capabilities) {
        return capabilities.stream().allMatch(capability -> registry.supports(target.key(), capability));
    }

    private List<BotChannelTarget> candidatesInternal(
            List<BotGroupBot> values,
            Map<Long, BotChannelInstanceMetadata> records,
            Set<BotChannelCapability> requiredCapabilities,
            BotTopologySnapshot topologySnapshot) {
        ClusterBotRuntime cluster = clusterProvider.getObject();
        return values.stream()
                .filter(BotGroupBot::isEnabled)
                .filter(binding -> topologySnapshot.enabled(binding.getGroupId()))
                .filter(binding -> "GROUP".equals(binding.getScopeType()))
                .filter(binding -> binding.getScopeKey() != null && !binding.getScopeKey().isBlank())
                .sorted(Comparator.comparingInt(BotGroupBot::getReplyPriority)
                        .thenComparing(BotGroupBot::getId))
                .map(binding -> {
                    BotChannelInstanceMetadata record = records.get(binding.getBotInstanceId());
                    if (record == null || !record.enabled() || !record.platform().name().equals(binding.getPlatform())) return null;
                    return new BotChannelTarget(binding, record);
                })
                .filter(Objects::nonNull)
                .filter(target -> registry.find(target.key())
                        .filter(snapshot -> snapshot.status() == BotChannelStatus.CONNECTED).isPresent())
                .filter(target -> cluster.isLocalOwner(target.key()))
                .filter(target -> supports(target, requiredCapabilities))
                .toList();
    }
}
