package com.nnm.bot.core.channel.runtime;

import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.port.BotChannelInstanceProvider;
import com.nnm.bot.core.channel.topology.BotTopologyLoader;
import com.nnm.bot.core.channel.topology.BotTopologySnapshot;
import com.nnm.bot.core.config.ClusterProperties;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.core.group.domain.BotGroup;
import com.nnm.bot.core.group.domain.BotGroupBot;
import com.nnm.bot.core.group.repository.BotGroupBotRepository;
import com.nnm.bot.core.group.repository.BotGroupRepository;
import io.github.rdt.state.SharedStateStore;
import io.github.rdt.redisson.lease.RedisOwnerLeaseService.Lease;
import io.github.rdt.redisson.lease.RedisOwnerLeaseService;
import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

/** Plans topology changes from actual ownership, without modifying bindings or connections. */
@Component
public class BotConnectionPlacementCoordinator {
    @Resource
    private BotChannelInstanceProvider instances;
    @Resource
    private BotGroupBotRepository bindings;
    @Resource
    private BotGroupRepository groups;
    @Resource
    private BotConnectionAffinityResolver resolver;
    @Resource
    private RedisOwnerLeaseService leases;
    @Resource
    private SharedStateStore stateStore;
    @Resource
    private ClusterProperties properties;
    @Resource
    private BotTopologyLoader topologyLoader;

    public Plan preview(BotGroupBot candidate) {
        // 预检查和提交校验直接读取一致的数据库快照，不使用可能尚未刷新的 JVM 配置。
        BotTopologySnapshot current = topologyLoader.load();
        List<BotGroupBot> existing = current.bindings().values().stream().flatMap(List::stream)
                .map(binding -> binding.entity()).toList();
        List<BotGroupBot> proposed = new ArrayList<>(existing.stream()
                .filter(binding -> candidate.getId() == null || !candidate.getId().equals(binding.getId())).toList());
        proposed.add(candidate);
        Set<Long> enabled = current.groups().values().stream().filter(group -> group.enabled())
                .map(group -> group.id()).collect(Collectors.toSet());
        Map<String, String> placement = resolver.resolve(current.instances(), proposed, enabled);
        Map<String, String> previous = resolver.resolve(current.instances(), existing, enabled);
        String revision = candidate.getGroupId() + ":" + candidate.getId() + ":" + candidate.getBotInstanceId()
                + ":" + candidate.getPlatform() + ":" + candidate.getScopeType() + ":" + candidate.getScopeKey()
                + ":" + candidate.isEnabled();
        BotChannelInstanceRecord instance = current.instances().get(candidate.getBotInstanceId());
        List<String> touched = candidate.isEnabled() && "GROUP".equals(candidate.getScopeType())
                && enabled.contains(candidate.getGroupId()) && instance != null
                ? Stream.of(placement.get(instance.instanceId())).filter(Objects::nonNull).toList() : List.of();
        return preview(revision, List.copyOf(current.instances().values()), placement, previous, touched);
    }

    public Plan previewGroup(long groupId, boolean enabled) {
        BotTopologySnapshot current = topologyLoader.load();
        Set<Long> enabledGroups = current.groups().values().stream().filter(group -> group.enabled())
                .map(group -> group.id()).collect(Collectors.toCollection(HashSet::new));
        List<BotGroupBot> bindings = current.bindings().values().stream().flatMap(List::stream)
                .map(binding -> binding.entity()).toList();
        Map<String, String> previous = resolver.resolve(current.instances(), bindings, enabledGroups);
        if (enabled) enabledGroups.add(groupId);
        else enabledGroups.remove(groupId);
        Map<String, String> placement = resolver.resolve(current.instances(), bindings, enabledGroups);
        List<String> touched = enabled ? current.bindings().getOrDefault(groupId, List.of()).stream()
                .filter(binding -> binding.enabled()).map(binding -> current.instances().get(binding.botInstanceId()))
                .filter(Objects::nonNull).map(record -> placement.get(record.instanceId())).filter(Objects::nonNull).toList() : List.of();
        return preview("group:" + groupId + ":" + enabled, List.copyOf(current.instances().values()), placement, previous, touched);
    }

    private Plan preview(String revision, List<BotChannelInstanceRecord> records, Map<String, String> placement,
                         Map<String, String> previous, List<String> touched) {
        List<String> affected = Stream.concat(touched.stream(), placement.entrySet().stream()
                        .filter(entry -> !Objects.equals(entry.getValue(), previous.get(entry.getKey())))
                        .map(Map.Entry::getValue)).distinct().sorted().toList();
        Map<String, Lease> owners = leases.findAll(records.stream().map(BotConnectionPlacementCoordinator::leaseName).toList());
        List<Transfer> transfers = affected.stream().map(component -> plan(component, placement, records, owners))
                .filter(transfer -> !transfer.moves().isEmpty()).toList();
        String token = UUID.nameUUIDFromBytes((revision + ":" + transfers).getBytes(StandardCharsets.UTF_8)).toString();
        return new Plan(token, transfers);
    }

    private Transfer plan(String component, Map<String, String> placement, List<BotChannelInstanceRecord> records,
                          Map<String, Lease> currentOwners) {
        List<String> members = members(component, placement);
        List<Owner> owners = records.stream().filter(record -> members.contains(record.instanceId()))
                .sorted(Comparator.comparing(BotChannelInstanceRecord::instanceId))
                .filter(record -> currentOwners.containsKey(leaseName(record)))
                .map(record -> new Owner(record.instanceId(), currentOwners.get(leaseName(record))))
                .toList();
        if (owners.stream().map(owner -> owner.lease().owner()).distinct().count() < 2) {
            return new Transfer(component, members, null, List.of(), owners);
        }
        String target = owners.stream().filter(owner -> component.equals(owner.instanceId()))
                .findFirst().orElse(owners.getFirst()).lease().owner();
        Lease targetIdentity = leases.find("node:" + target).orElseThrow(() ->
                new IllegalStateException("迁移目标节点身份租约已失效，请刷新后重试: " + target));
        List<Move> moves = owners.stream().filter(owner -> !target.equals(owner.lease().owner()))
                .map(owner -> new Move(owner.instanceId(), owner.lease().owner(), target)).toList();
        return new Transfer(component, members, targetIdentity, moves, owners);
    }

    public Plan prepare(BotGroupBot candidate, String confirmationToken) {
        return confirmed(preview(candidate), confirmationToken);
    }

    public Plan prepareGroup(long groupId, boolean enabled, String confirmationToken) {
        return confirmed(previewGroup(groupId, enabled), confirmationToken);
    }

    private Plan confirmed(Plan plan, String confirmationToken) {
        BusinessAssert.isTrue(plan.transfers().isEmpty() || Objects.equals(plan.confirmationToken(), confirmationToken),
                BotChannelErrorCode.CONNECTION_MIGRATION_CONFIRMATION_REQUIRED,
                "该绑定需要迁移机器人连接，请重新预检查并确认最新迁移方案");
        return plan;
    }

    public void approve(Plan plan, long bindingId) {
        approve(plan, bindingId, null);
    }

    public void approveGroup(Plan plan, long groupId) {
        approve(plan, null, groupId);
    }

    private void approve(Plan plan, Long bindingId, Long groupId) {
        BusinessAssert.isTrue(plan.transfers().stream().allMatch(transfer -> leases.isHeld(transfer.targetIdentity())
                        && transfer.owners().stream().allMatch(owner -> leases.isHeld(owner.lease()))),
                BotChannelErrorCode.CONNECTION_MIGRATION_CONFIRMATION_REQUIRED,
                "连接归属在保存期间发生变化，请重新预检查并确认迁移方案");
        for (Transfer transfer : plan.transfers()) {
            // The intent stays inert until its triggering change and resulting component are visible after commit.
            stateStore.put(planKey(transfer.component()), new Approval(bindingId, groupId, transfer.members(), transfer.targetIdentity(), transfer.owners()),
                    properties.taskRetention());
        }
    }

    public Optional<String> approvedTarget(String component, List<String> members) {
        Optional<Approval> entry = stateStore.get(planKey(component), Approval.class);
        if (entry.isPresent()) {
            Approval approval = entry.get();
            if (approval.members().equals(members) && committed(approval)
                    && leases.isHeld(approval.targetIdentity())) {
                String target = approval.targetIdentity().owner();
                List<String> names = instances.findAll().stream().filter(record -> members.contains(record.instanceId()))
                        .map(BotConnectionPlacementCoordinator::leaseName).toList();
                List<Lease> current = List.copyOf(leases.findAll(names).values());
                boolean unchanged = current.stream().allMatch(lease -> target.equals(lease.owner())
                        || approval.owners().stream().anyMatch(owner -> owner.lease().equals(lease)));
                if (!unchanged) return Optional.empty();
                if (!current.isEmpty() && current.stream().allMatch(lease -> target.equals(lease.owner()))) {
                    clearApproval(component);
                }
                return Optional.of(target);
            }
        }
        return Optional.empty();
    }

    private boolean committed(Approval approval) {
        return approval.bindingId() != null ? bindings.existsById(approval.bindingId())
                : groups.findById(approval.groupId()).map(BotGroup::isEnabled).orElse(false);
    }

    public void clearApproval(String component) {
        stateStore.evict(planKey(component));
    }

    public static List<String> members(String component, Map<String, String> placement) {
        return placement.entrySet().stream().filter(entry -> component.equals(entry.getValue()))
                .map(Map.Entry::getKey).sorted().toList();
    }

    public static String leaseName(BotChannelInstanceRecord record) {
        return "bot:" + record.platform() + ":" + record.botAccountId();
    }

    private String planKey(String component) {
        return "bot-placement-approval:" + component;
    }

    public record Move(String instanceId, String fromNode, String toNode) {
    }

    public record Plan(String confirmationToken, List<Transfer> transfers) {
        public List<Move> moves() {
            return transfers.stream().flatMap(transfer -> transfer.moves().stream()).toList();
        }
    }

    public record Transfer(String component, List<String> members, Lease targetIdentity, List<Move> moves,
                           List<Owner> owners) {
    }

    public record Owner(String instanceId, Lease lease) {
    }

    public record Approval(Long bindingId, Long groupId, List<String> members, Lease targetIdentity, List<Owner> owners) {
    }
}
