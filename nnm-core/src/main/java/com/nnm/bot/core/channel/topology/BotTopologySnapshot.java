package com.nnm.bot.core.channel.topology;

import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotChannelInstanceMetadata;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Set;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.group.domain.BotGroup;
import com.nnm.bot.core.group.domain.BotGroupBot;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** 小型稳定配置的只读索引；不共享 Hibernate 实体，也不把凭据发送到 Redis。 */
public record BotTopologySnapshot(String version,
                                  Map<Long, BotChannelInstanceRecord> instances,
                                  Map<Long, BotChannelInstanceMetadata> metadata,
                                  Map<String, String> connectionGroups,
                                  Map<BotChannelKey, Long> instanceIds,
                                  Map<Long, Group> groups,
                                  Map<Long, List<Binding>> bindings,
                                  Map<Scope, Binding> scopes) {
    public BotTopologySnapshot {
        instances = Map.copyOf(instances);
        metadata = Map.copyOf(metadata);
        connectionGroups = Map.copyOf(connectionGroups);
        instanceIds = Map.copyOf(instanceIds);
        groups = Map.copyOf(groups);
        bindings = bindings.entrySet().stream().collect(Collectors.toUnmodifiableMap(
                Map.Entry::getKey, entry -> List.copyOf(entry.getValue())));
        scopes = Map.copyOf(scopes);
    }

    public static BotTopologySnapshot of(Map<Long, BotChannelInstanceRecord> instances,
                                         List<BotGroup> groups, List<BotGroupBot> bindings) {
        Map<BotChannelKey, Long> ids = instances.entrySet().stream().collect(Collectors.toMap(
                entry -> new BotChannelKey(entry.getValue().platform(), entry.getValue().botAccountId()), Map.Entry::getKey));
        List<Binding> values = bindings.stream().map(Binding::of)
                .sorted(Comparator.comparingInt(Binding::replyPriority).thenComparing(Binding::id)).toList();
        Map<Long, BotChannelInstanceMetadata> metadata = instances.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey, entry -> BotChannelInstanceMetadata.of(entry.getValue())));
        Set<Long> enabled = groups.stream().filter(BotGroup::isEnabled).map(BotGroup::getId).collect(Collectors.toSet());
        return new BotTopologySnapshot(version(instances, groups, bindings), instances, metadata,
                connectionGroups(instances, bindings, enabled), ids, groups.stream().map(Group::of)
                .collect(Collectors.toMap(Group::id, Function.identity())), values.stream()
                .collect(Collectors.groupingBy(Binding::groupId)), values.stream().collect(Collectors.toMap(
                        binding -> new Scope(binding.botInstanceId(), binding.platform(), binding.scopeType(), binding.scopeKey()),
                        Function.identity())));
    }

    /** 连接分组只在配置变化时构建，迁移预检查也复用同一算法。 */
    public static Map<String, String> connectionGroups(Map<Long, BotChannelInstanceRecord> records,
                                                       List<BotGroupBot> bindings, Set<Long> enabledGroups) {
        Map<Long, String> names = new HashMap<>();
        Map<String, String> parents = new HashMap<>();
        records.forEach((id, record) -> {
            if (record.enabled()) {
                parents.put(record.instanceId(), record.instanceId());
                names.put(id, record.instanceId());
            }
        });
        Map<Long, String> firstByGroup = new HashMap<>();
        for (BotGroupBot binding : bindings) {
            String name = names.get(binding.getBotInstanceId());
            if (!binding.isEnabled() || name == null || !"GROUP".equals(binding.getScopeType())
                    || !enabledGroups.contains(binding.getGroupId())) continue;
            String first = firstByGroup.putIfAbsent(binding.getGroupId(), name);
            if (first == null) continue;
            String left = root(parents, first);
            String right = root(parents, name);
            if (!left.equals(right)) parents.put(left.compareTo(right) < 0 ? right : left,
                    left.compareTo(right) < 0 ? left : right);
        }
        Map<String, String> result = new HashMap<>();
        parents.keySet().forEach(name -> result.put(name, root(parents, name)));
        return Map.copyOf(result);
    }

    private static String root(Map<String, String> parents, String value) {
        String parent = parents.get(value);
        while (!parent.equals(value)) {
            value = parent;
            parent = parents.get(value);
        }
        return value;
    }

    private static String version(Map<Long, BotChannelInstanceRecord> instances,
                                  List<BotGroup> groups, List<BotGroupBot> bindings) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // 稳定内容摘要让乱序、重复通知保持幂等；通知中不包含原始配置或凭据。
            instances.entrySet().stream().sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        BotChannelInstanceRecord record = entry.getValue();
                        update(digest, entry.getKey(), record.platform(), record.instanceId(), record.botAccountId(), record.enabled(),
                                record.connectionUrl(), record.accessToken(), record.appId(), record.clientSecret(), record.tokenUrl(),
                                record.apiBaseUrl(), record.gatewayUrl(), record.reconnectInterval(), record.intents(),
                                record.gatewayConnectTimeout(), record.reconnectDelay());
                    });
            groups.stream().map(Group::of).sorted(Comparator.comparingLong(Group::id))
                    .forEach(group -> update(digest, group.id(), group.code(), group.name(), group.description(), group.enabled()));
            bindings.stream().map(Binding::of).sorted(Comparator.comparingLong(Binding::id))
                    .forEach(binding -> update(digest, binding.id(), binding.groupId(), binding.botInstanceId(), binding.platform(),
                            binding.scopeType(), binding.scopeKey(), binding.displayName(), binding.enabled(), binding.replyPriority()));
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("无法计算拓扑版本", exception);
        }
    }

    private static void update(MessageDigest digest, Object... values) {
        for (Object value : values) {
            digest.update((byte) (value == null ? 0 : 1));
            if (value == null) continue;
            byte[] bytes = value.toString().getBytes(StandardCharsets.UTF_8);
            digest.update(Integer.toString(bytes.length).getBytes(StandardCharsets.UTF_8));
            digest.update((byte) ':');
            digest.update(bytes);
        }
    }

    public List<BotGroupBot> groupBindings(long groupId) {
        return bindings.getOrDefault(groupId, List.of()).stream().map(Binding::entity).toList();
    }

    public boolean enabled(long groupId) {
        Group group = groups.get(groupId);
        return group != null && group.enabled();
    }

    public record Scope(long instanceId, String platform, String scopeType, String scopeKey) {
    }

    public record Group(long id, String code, String name, String description, boolean enabled) {
        public static Group of(BotGroup value) {
            return new Group(value.getId(), value.getCode(), value.getName(), value.getDescription(), value.isEnabled());
        }

        public BotGroup entity() {
            BotGroup value = new BotGroup();
            value.setId(id);
            value.setCode(code);
            value.setName(name);
            value.setDescription(description);
            value.setEnabled(enabled);
            return value;
        }
    }

    public record Binding(long id, long groupId, long botInstanceId, String platform, String scopeType,
                          String scopeKey, String displayName, boolean enabled, int replyPriority) {
        public static Binding of(BotGroupBot value) {
            return new Binding(value.getId(), value.getGroupId(), value.getBotInstanceId(), value.getPlatform(),
                    value.getScopeType(), value.getScopeKey(), value.getDisplayName(), value.isEnabled(), value.getReplyPriority());
        }

        public BotGroupBot entity() {
            BotGroupBot value = new BotGroupBot();
            value.setId(id);
            value.setGroupId(groupId);
            value.setBotInstanceId(botInstanceId);
            value.setPlatform(platform);
            value.setScopeType(scopeType);
            value.setScopeKey(scopeKey);
            value.setDisplayName(displayName);
            value.setEnabled(enabled);
            value.setReplyPriority(replyPriority);
            return value;
        }
    }
}
