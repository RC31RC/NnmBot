package com.nnm.bot.core.channel.runtime;

import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.topology.BotTopologyStore;
import com.nnm.bot.core.channel.topology.BotTopologySnapshot;
import com.nnm.bot.core.group.domain.BotGroupBot;
import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** Shared group bindings form connected components that must live on the same node. */
@Component
public class BotConnectionAffinityResolver {
    @Resource
    private BotTopologyStore topology;

    public Map<String, String> resolve(List<BotChannelInstanceRecord> records) {
        BotTopologySnapshot snapshot = topology.snapshot();
        return snapshot.connectionGroups();
    }

    public Map<String, String> resolve(List<BotChannelInstanceRecord> records, List<BotGroupBot> candidates) {
        Set<Long> enabledGroups = topology.snapshot().groups().values().stream().filter(group -> group.enabled())
                .map(group -> group.id()).collect(Collectors.toSet());
        return resolve(records, candidates, enabledGroups);
    }

    public Map<String, String> resolve(List<BotChannelInstanceRecord> records, List<BotGroupBot> candidates,
                                       Set<Long> enabledGroups) {
        Map<Long, BotChannelInstanceRecord> indexed = new HashMap<>();
        BotTopologySnapshot snapshot = topology.snapshot();
        records.forEach(record -> {
            Long id = snapshot.instanceIds().get(new BotChannelKey(record.platform(), record.botAccountId()));
            if (id != null) indexed.put(id, record);
        });
        return resolve(indexed, candidates, enabledGroups);
    }

    public Map<String, String> resolve(Map<Long, BotChannelInstanceRecord> records, List<BotGroupBot> candidates,
                                       Set<Long> enabledGroups) {
        return BotTopologySnapshot.connectionGroups(records, candidates, enabledGroups);
    }

    public Optional<String> instanceId(List<BotChannelInstanceRecord> records, Long id) {
        return Optional.ofNullable(topology.snapshot().instances().get(id)).map(BotChannelInstanceRecord::instanceId);
    }

}
