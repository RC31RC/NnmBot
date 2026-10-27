package com.nnm.bot.core.channel.notification.store;

import com.nnm.bot.core.constant.NotificationConstants.GroupResultStatus;

import com.nnm.bot.core.channel.notification.GroupNotificationResult;
import com.nnm.bot.core.config.BotNotificationProperties;
import io.github.rdt.state.SharedStateStore;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class GroupNotificationResultStore {
    @Resource
    private SharedStateStore states;
    @Resource
    private BotNotificationProperties properties;

    public Optional<GroupNotificationResult> find(UUID id) {
        return states.get(key(id), GroupNotificationResult.class);
    }

    public Map<UUID, GroupNotificationResult> findAll(Collection<UUID> ids) {
        Map<String, GroupNotificationResult> values = states.getAll(ids.stream().map(this::key).toList(), GroupNotificationResult.class);
        return ids.stream().filter(id -> values.containsKey(key(id)))
                .filter(id -> values.get(key(id)).status() != GroupResultStatus.IN_PROGRESS)
                .collect(Collectors.toMap(id -> id, id -> values.get(key(id))));
    }

    public void save(UUID id, GroupNotificationResult result) {
        states.put(key(id), result, properties.retention());
    }

    private String key(UUID id) { return "group-notification-result:" + id; }
}
