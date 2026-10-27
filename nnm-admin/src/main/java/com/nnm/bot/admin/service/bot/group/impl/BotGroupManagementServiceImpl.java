package com.nnm.bot.admin.service.bot.group.impl;

import java.util.Locale;
import java.util.UUID;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.nnm.bot.admin.pojo.entity.bot.channel.BotChannelInstance;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupBotSaveDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupBindingPreviewDTO;
import com.nnm.bot.core.channel.runtime.BotConnectionPlacementCoordinator;
import com.nnm.bot.core.channel.runtime.BotConnectionPlacementCoordinator.Plan;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupBotUpdateDTO;
import com.nnm.bot.core.channel.runtime.ClusterBotRuntime;
import com.nnm.bot.core.channel.topology.BotTopologyInvalidator;
import org.springframework.beans.factory.ObjectProvider;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupCreateDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupMemberDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupMemberPointsUpdateDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupMemberUpdateDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupUpdateDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeDiscoveryMemberDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeDiscoveryDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeDiscoveryScopeDTO;
import com.nnm.bot.admin.repository.bot.channel.BotChannelInstanceRepository;
import com.nnm.bot.admin.service.bot.group.BotGroupManagementService;
import com.nnm.bot.common.exception.DataNotFoundException;
import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.common.task.TaskProgressReporter;
import com.nnm.bot.core.channel.port.BotChannelInstanceProvider;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.model.BotChannelSnapshot;
import com.nnm.bot.core.channel.model.BotDiscoveredMember;
import com.nnm.bot.core.channel.model.BotMemberQueryResult;
import com.nnm.bot.core.channel.model.BotScopeDiscoveryResult;
import com.nnm.bot.core.channel.runtime.BotScopeDiscoveryRouter;
import com.nnm.bot.core.channel.registry.BotChannelRegistry;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.constant.BotChannelErrorCode;
import com.nnm.bot.core.group.domain.BotGroup;
import com.nnm.bot.core.group.domain.BotGroupBot;
import com.nnm.bot.core.group.domain.BotGroupMember;
import com.nnm.bot.core.group.repository.BotGroupBotRepository;
import com.nnm.bot.core.group.repository.BotGroupMemberRepository;
import com.nnm.bot.core.group.repository.BotGroupPointTransactionRepository;
import com.nnm.bot.core.group.repository.BotGroupRepository;
import com.nnm.bot.core.group.service.PointAccountService;
import com.nnm.bot.core.group.service.BotGroupMemberIdentityAdapters;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 逻辑群组管理。平台群号只作为群组下的一条机器人绑定保存。
 */
@Service
public class BotGroupManagementServiceImpl implements BotGroupManagementService {

    @Resource
    private BotGroupRepository groupRepository;
    @Resource
    private BotGroupBotRepository botRepository;
    @Resource
    private BotGroupMemberRepository memberRepository;
    @Resource
    private BotGroupPointTransactionRepository pointTransactionRepository;
    @Resource
    private PointAccountService pointAccountService;
    @Resource
    private BotGroupMemberIdentityAdapters identityAdapters = BotGroupMemberIdentityAdapters.defaults();
    @Resource
    private BotChannelInstanceRepository instanceRepository;
    @Resource
    private BotChannelInstanceProvider instanceProvider;
    @Resource
    private BotScopeDiscoveryRouter discoveryRouter;
    @Resource
    private BotChannelRegistry channelRegistry;
    @Resource
    private ObjectProvider<ClusterBotRuntime> clusterProvider;
    @Resource
    private ObjectProvider<BotConnectionPlacementCoordinator> placementCoordinator;
    @Resource(name = "memberDiscoveryExecutor")
    private Executor memberDiscoveryExecutor;
    @Resource
    private BotTopologyInvalidator topologyInvalidator;

    @Override
    public List<BotGroupDTO> list() {
        Map<Long, BotChannelInstance> instances = instanceRepository.findAll().stream()
                .collect(Collectors.toMap(BotChannelInstance::getId, Function.identity()));
        return groupRepository.findAllByOrderByNameAscIdAsc().stream()
                .map(group -> new BotGroupDTO(
                        group.getId(),
                        group.getCode(),
                        group.getName(),
                        group.getDescription(),
                        group.isEnabled(),
                        memberRepository.countByGroupId(group.getId()),
                        botRepository.findAllByGroupIdOrderByPlatformAscScopeKeyAsc(group.getId())
                                .stream()
                                .map(binding -> binding(binding, instances.get(binding.getBotInstanceId())))
                                .toList(),
                        group.getCreatedAt(),
                        group.getUpdatedAt()))
                .toList();
    }

    @Override
    public List<BotScopeDiscoveryDTO> discover() {
        return discover(false, TaskProgressReporter.noop());
    }

    @Override
    public List<BotScopeDiscoveryDTO> discover(TaskProgressReporter reporter) {
        return discover(false, reporter);
    }

    @Override
    @Transactional
    public List<BotScopeDiscoveryDTO> discoverAndSynchronize() {
        return discover(true, TaskProgressReporter.noop());
    }

    @Override
    @Transactional
    public List<BotScopeDiscoveryDTO> discoverAndSynchronize(
            TaskProgressReporter reporter) {
        return discover(true, reporter);
    }

    private List<BotScopeDiscoveryDTO> discover(
            boolean synchronizeBoundMembers, TaskProgressReporter reporter) {
        TaskProgressReporter progress = reporter == null
                ? TaskProgressReporter.noop() : reporter;
        Map<String, BotChannelInstanceRecord> records = instanceProvider.findAll().stream()
                .collect(Collectors.toMap(BotChannelInstanceRecord::instanceId, Function.identity()));
        List<BotChannelInstance> instances =
                instanceRepository.findAllByOrderByPlatformAscInstanceIdAsc();
        progress.report("DISCOVERING_BOTS", "正在调用机器人发现功能", 0, instances.size());
        Map<Long, DiscoveryExecution> executions = discoverInstances(instances, records);
        List<BotScopeDiscoveryDTO> result = new ArrayList<>(instances.size());
        for (int index = 0; index < instances.size(); index++) {
            BotChannelInstance instance = instances.get(index);
            result.add(discover(
                    instance,
                    executions.get(instance.getId()),
                    synchronizeBoundMembers));
            progress.report("PROCESSING_DISCOVERY",
                    "正在处理 " + instance.getDisplayName() + " 的发现结果",
                    index + 1,
                    instances.size());
        }
        progress.complete("机器人群组发现完成");
        return result;
    }

    @Override
    @Transactional
    public void refreshMembers(long groupId) {
        refreshMembers(groupId, TaskProgressReporter.noop());
    }

    @Override
    @Transactional
    public void refreshMembers(long groupId, TaskProgressReporter reporter) {
        TaskProgressReporter progress = reporter == null
                ? TaskProgressReporter.noop() : reporter;
        group(groupId);
        List<BotGroupBot> bindings = botRepository.findAllByGroupIdOrderByPlatformAscScopeKeyAsc(groupId)
                .stream()
                .filter(BotGroupBot::isEnabled)
                .toList();
        if (bindings.isEmpty()) {
            progress.complete("当前群组没有启用的机器人绑定");
            return;
        }

        progress.report("DISCOVERING_BOTS", "正在调用当前群组绑定的机器人", 0, bindings.size());
        Map<Long, BotChannelInstance> instances = instanceRepository.findAllById(
                        bindings.stream().map(BotGroupBot::getBotInstanceId).distinct().toList())
                .stream()
                .collect(Collectors.toMap(BotChannelInstance::getId, Function.identity()));
        Map<String, BotChannelInstanceRecord> records = instanceProvider.findAll().stream()
                .collect(Collectors.toMap(BotChannelInstanceRecord::instanceId, Function.identity()));
        Map<Long, DiscoveryExecution> executions =
                discoverInstances(instances.values(), records);

        int processed = 0;
        for (BotGroupBot binding : bindings) {
            BotChannelInstance instance = instances.get(binding.getBotInstanceId());
            if (instance == null) {
                progress.report("PROCESSING_MEMBERS",
                        "跳过不存在的机器人实例绑定 " + binding.getScopeKey(),
                        ++processed,
                        bindings.size());
                continue;
            }
            DiscoveryExecution execution = executions.get(instance.getId());
            if (execution == null || !execution.result().supported()) {
                progress.report("PROCESSING_MEMBERS",
                        "跳过无法发现的机器人绑定 " + binding.getScopeKey(),
                        ++processed,
                        bindings.size());
                continue;
            }
            execution.result().scopes().stream()
                    .filter(scope -> scope.scopeType().equals(binding.getScopeType())
                            && scope.scopeKey().equals(binding.getScopeKey()))
                    .findFirst()
                    .ifPresent(scope -> synchronizeMembers(
                            groupId,
                            instance.getPlatform(),
                            records.get(instance.getInstanceId()),
                            binding.getScopeKey(),
                            scope.members()));
            progress.report("PROCESSING_MEMBERS",
                    "已刷新机器人群组 " + binding.getScopeKey(),
                    ++processed,
                    bindings.size());
        }
        progress.complete("当前群组成员刷新完成");
    }

    @Override
    @Transactional
    public long create(BotGroupCreateDTO request) {
        topologyInvalidator.invalidateAfterCommit();
        String code = request.code().trim().toLowerCase(Locale.ROOT);
        if (groupRepository.findByCodeIgnoreCase(code).isPresent()) {
            throw new IllegalArgumentException("群组编码已存在");
        }
        BotGroup group = new BotGroup();
        group.setCode(code);
        group.setName(request.name().trim());
        group.setDescription(trimToNull(request.description()));
        group.setEnabled(request.enabled());
        return groupRepository.save(group).getId();
    }

    @Override
    public BotGroupBindingPreviewDTO previewGroupUpdate(long id, BotGroupUpdateDTO request) {
        group(id);
        BotConnectionPlacementCoordinator coordinator = placementCoordinator == null ? null : placementCoordinator.getIfAvailable();
        return coordinator == null ? BotGroupBindingPreviewDTO.unchanged()
                : preview(coordinator.previewGroup(id, request.enabled()));
    }

    @Override
    @Transactional
    public void update(long id, BotGroupUpdateDTO request) {
        topologyInvalidator.invalidateAfterCommit();
        groupRepository.lockTopology();
        BotGroup group = group(id);
        BotConnectionPlacementCoordinator coordinator = placementCoordinator == null ? null : placementCoordinator.getIfAvailable();
        Plan plan = coordinator == null ? null : coordinator.prepareGroup(id, request.enabled(), request.confirmationToken());
        group.setName(request.name().trim());
        group.setDescription(trimToNull(request.description()));
        group.setEnabled(request.enabled());
        groupRepository.save(group);
        if (plan != null && !plan.transfers().isEmpty()) coordinator.approveGroup(plan, id);
    }

    @Override
    @Transactional
    public void delete(long id) {
        topologyInvalidator.invalidateAfterCommit();
        groupRepository.lockTopology();
        group(id);
        botRepository.deleteAllByGroupId(id);
        memberRepository.deleteAllByGroupId(id);
        groupRepository.deleteById(id);
    }

    @Override
    public BotGroupBindingPreviewDTO previewBinding(long groupId, BotGroupBotSaveDTO request) {
        return preview(binding(groupId, request));
    }

    @Override
    public BotGroupBindingPreviewDTO previewBindingUpdate(long groupId, long bindingId, BotGroupBotUpdateDTO request) {
        return preview(bindingUpdate(groupId, bindingId, request));
    }

    private BotGroupBindingPreviewDTO preview(BotGroupBot candidate) {
        BotConnectionPlacementCoordinator coordinator = placementCoordinator == null ? null : placementCoordinator.getIfAvailable();
        if (coordinator == null) return BotGroupBindingPreviewDTO.unchanged();
        return preview(coordinator.preview(candidate));
    }

    private BotGroupBindingPreviewDTO preview(Plan plan) {
        return new BotGroupBindingPreviewDTO(!plan.transfers().isEmpty(), plan.confirmationToken(), plan.moves());
    }

    private Plan preparePlacement(BotGroupBot candidate, String token) {
        BotConnectionPlacementCoordinator coordinator = placementCoordinator == null ? null : placementCoordinator.getIfAvailable();
        return coordinator == null ? null : coordinator.prepare(candidate, token);
    }

    private void approvePlacement(Plan plan, BotGroupBot binding) {
        if (plan != null && !plan.transfers().isEmpty()) {
            placementCoordinator.getObject().approve(plan, binding.getId());
        }
    }

    private BotGroupBot binding(long groupId, BotGroupBotSaveDTO request) {
        group(groupId);
        BotChannelInstance instance = instanceRepository.findById(request.botInstanceId())
                .orElseThrow(() -> new DataNotFoundException("机器人实例不存在"));
        if (instance.getPlatform() != request.platform()) {
            throw new IllegalArgumentException("机器人实例平台与群组绑定平台不一致");
        }
        String scopeType = request.scopeType().trim().toUpperCase();
        String scopeKey = request.scopeKey().trim();
        BusinessAssert.isTrue(botRepository.findByPlatformAndScopeTypeAndScopeKeyAndBotInstanceId(
                request.platform().name(), scopeType, scopeKey, instance.getId()).isEmpty(),
                CommonErrorCode.DATA_CONFLICT, "该机器人作用域已经绑定到逻辑群组");
        BotGroupBot binding = new BotGroupBot();
        binding.setGroupId(groupId);
        binding.setBotInstanceId(instance.getId());
        binding.setPlatform(request.platform().name());
        binding.setScopeType(scopeType);
        binding.setScopeKey(scopeKey);
        binding.setDisplayName(trimToNull(request.displayName()));
        binding.setEnabled(request.enabled());
        validateOfficialInstance(binding);
        return binding;
    }

    private void validateOfficialInstance(BotGroupBot candidate) {
        if (!"QQ_OFFICIAL".equals(candidate.getPlatform()) || !"GROUP".equals(candidate.getScopeType())) return;
        boolean conflict = botRepository.findAllByGroupIdOrderByPlatformAscScopeKeyAsc(candidate.getGroupId()).stream()
                .anyMatch(binding -> "QQ_OFFICIAL".equals(binding.getPlatform()) && "GROUP".equals(binding.getScopeType())
                        && !binding.getBotInstanceId().equals(candidate.getBotInstanceId()));
        BusinessAssert.isFalse(conflict, BotChannelErrorCode.MULTIPLE_OFFICIAL_INSTANCES_UNSUPPORTED);
    }

    @Override
    @Transactional
    public void bindBot(long groupId, BotGroupBotSaveDTO request) {
        topologyInvalidator.invalidateAfterCommit();
        groupRepository.lockTopology();
        BotGroupBot binding = binding(groupId, request);
        Plan plan = preparePlacement(binding, request.confirmationToken());
        botRepository.save(binding);
        if (Boolean.TRUE.equals(request.syncMembers()) && "GROUP".equals(binding.getScopeType())) {
            BotChannelInstance instance = instanceRepository.findById(request.botInstanceId()).orElseThrow();
            synchronizeMembers(groupId, request.platform(), binding.getScopeType(), binding.getScopeKey(), instance);
        }
        approvePlacement(plan, binding);
    }

    @Override
    @Transactional
    public void unbindBot(long groupId, long bindingId) {
        topologyInvalidator.invalidateAfterCommit();
        groupRepository.lockTopology();
        group(groupId);
        BotGroupBot binding = botRepository.findById(bindingId)
                .filter(item -> item.getGroupId().equals(groupId))
                .orElseThrow(() -> new DataNotFoundException("群组机器人绑定不存在"));
        botRepository.delete(binding);
    }

    private BotGroupBot bindingUpdate(long groupId, long bindingId, BotGroupBotUpdateDTO request) {
        group(groupId);
        BotGroupBot existing = botRepository.findById(bindingId)
                .filter(item -> item.getGroupId().equals(groupId))
                .orElseThrow(() -> new DataNotFoundException("群组机器人绑定不存在"));
        // Keep the managed entity unchanged while comparing the current and proposed topology.
        BotGroupBot candidate = new BotGroupBot();
        candidate.setId(existing.getId());
        candidate.setGroupId(groupId);
        candidate.setBotInstanceId(existing.getBotInstanceId());
        candidate.setPlatform(existing.getPlatform());
        candidate.setScopeType(existing.getScopeType());
        candidate.setScopeKey(existing.getScopeKey());
        candidate.setDisplayName(existing.getDisplayName());
        candidate.setEnabled(request.enabled());
        candidate.setReplyPriority(request.replyPriority());
        validateOfficialInstance(candidate);
        return candidate;
    }

    @Override
    @Transactional
    public void updateBot(long groupId, long bindingId, BotGroupBotUpdateDTO request) {
        topologyInvalidator.invalidateAfterCommit();
        groupRepository.lockTopology();
        BotGroupBot binding = bindingUpdate(groupId, bindingId, request);
        Plan plan = preparePlacement(binding, request.confirmationToken());
        botRepository.save(binding);
        approvePlacement(plan, binding);
    }

    @Override
    public List<BotGroupMemberDTO> members(long groupId) {
        group(groupId);
        return memberRepository.findAllByGroupIdOrderByMemberRoleAscIdAsc(groupId)
                .stream()
                .map(member -> new BotGroupMemberDTO(
                        member.getId(),
                        member.getQqId(),
                        member.getMemberOpenid(),
                        member.getKookId(),
                        member.isQqIdVerified(),
                        member.getQqBoundAt(),
                        member.getDisplayName(),
                        member.getMemberRole(),
                        member.isEnabled(),
                        member.getPoints(),
                        member.getTotalEarned(),
                        member.getTotalSpent()))
                .toList();
    }

    @Override
    @Transactional
    public void updateMember(long groupId, long memberId, BotGroupMemberUpdateDTO request) {
        group(groupId);
        BotGroupMember member = memberRepository.findById(memberId)
                .filter(item -> item.getGroupId().equals(groupId))
                .orElseThrow(() -> new DataNotFoundException("群组成员不存在"));
        String role = request.memberRole().trim().toUpperCase();
        if (!role.equals("OWNER") && !role.equals("ADMIN") && !role.equals("MEMBER")) {
            throw new IllegalArgumentException("群组成员角色不合法");
        }
        member.setDisplayName(trimToNull(request.displayName()));
        member.setMemberRole(role);
        member.setEnabled(request.enabled());
        memberRepository.save(member);
    }

    @Override
    public void updateMemberPoints(long groupId, long memberId,
                                   BotGroupMemberPointsUpdateDTO request) {
        // 账户服务自行开启短事务，确保事务提交后才退出其 RDT 锁作用域。
        group(groupId);
        pointAccountService.adjust(
                groupId,
                memberId,
                request.points(),
                "ADMIN_ADJUSTMENT:" + groupId + ":" + memberId + ":"
                        + UUID.randomUUID(),
                "后台管理员调整成员积分");
    }

    private BotGroupDTO.BotBindingDTO binding(
            BotGroupBot binding, BotChannelInstance instance) {
        BotChannelSnapshot snapshot = channelSnapshot(instance).orElse(null);
        return new BotGroupDTO.BotBindingDTO(
                binding.getId(),
                binding.getBotInstanceId(),
                ObjectUtil.isNull(instance) ? "-" : instance.getInstanceId(),
                ObjectUtil.isNull(instance) ? "-" : instance.getDisplayName(),
                binding.getPlatform(),
                binding.getScopeType(),
                binding.getScopeKey(),
                binding.getDisplayName(),
                binding.isEnabled(),
                binding.getReplyPriority(),
                ObjectUtil.isNull(snapshot) ? null : snapshot.status(),
                ObjectUtil.isNull(snapshot) ? null : snapshot.statusChangedAt(),
                ObjectUtil.isNull(snapshot) ? null : snapshot.lastError());
    }

    private BotScopeDiscoveryDTO discover(
            BotChannelInstance instance,
            DiscoveryExecution execution,
            boolean synchronizeBoundMembers) {
        BotScopeDiscoveryResult result = ObjectUtil.isNull(execution)
                ? BotScopeDiscoveryResult.unsupported("渠道未提供发现能力")
                : execution.result();
        return new BotScopeDiscoveryDTO(
                instance.getId(),
                instance.getInstanceId(),
                instance.getDisplayName(),
                instance.getPlatform(),
                channelSnapshot(instance).map(BotChannelSnapshot::status).orElse(null),
                result.supported(),
                result.message(),
                result.scopes().stream()
                        .map(scope -> {
                            Optional<BotGroupBot> binding =
                                    botRepository
                                            .findByPlatformAndScopeTypeAndScopeKeyAndBotInstanceId(
                                                    instance.getPlatform().name(),
                                                    scope.scopeType(),
                                                    scope.scopeKey(),
                                                    instance.getId());
                            if (synchronizeBoundMembers
                                    && instance.getPlatform() == BotPlatform.ONEBOT) {
                                botRepository
                                        .findAllByPlatformAndScopeTypeAndScopeKeyAndBotInstanceId(
                                                instance.getPlatform().name(),
                                                scope.scopeType(),
                                                scope.scopeKey(),
                                                instance.getId())
                                        .forEach(item -> synchronizeMembers(
                                                item.getGroupId(),
                                                instance.getPlatform(),
                                                scope.members(),
                                                scope.membersComplete()));
                            }
                            return new BotScopeDiscoveryScopeDTO(
                                    scope.scopeType(),
                                    scope.scopeKey(),
                                    scope.displayName(),
                                    scope.memberCount(),
                                    binding.isPresent(),
                                    binding.map(BotGroupBot::getGroupId).orElse(null),
                                    scope.members().stream()
                                            .map(member -> new BotScopeDiscoveryMemberDTO(
                                                    member.platformUserId(),
                                                    member.displayName(),
                                                    member.role()))
                                            .toList());
                        })
                        .toList());
    }

    private Optional<BotChannelSnapshot> channelSnapshot(BotChannelInstance instance) {
        if (ObjectUtil.isNull(instance) || ObjectUtil.isNull(channelRegistry)) {
            return Optional.empty();
        }
        BotChannelKey key = new BotChannelKey(instance.getPlatform(), instance.getBotAccountId());
        ClusterBotRuntime cluster = clusterProvider == null ? null : clusterProvider.getIfAvailable();
        return ObjectUtil.isNull(cluster) ? channelRegistry.find(key) : cluster.snapshot(key);
    }

    private void synchronizeMembers(
            long groupId, BotPlatform platform, String scopeType, String scopeKey,
            BotChannelInstance instance) {
        BotChannelInstanceRecord record = instanceProvider.findAll().stream()
                .filter(item -> item.instanceId().equals(instance.getInstanceId()))
                .findFirst()
                .orElse(null);
        if (ObjectUtil.isNull(record)) {
            return;
        }
        BotMemberQueryResult query = discoveryRouter.queryMembers(record, scopeKey);
        if (query.complete()) {
            synchronizeMembers(groupId, platform, query.members(), true);
            return;
        }
        // 失败或无权限时只保留已经观察到的成员，不按空结果清理成员身份。
        discoveryRouter.discover(record).scopes().stream()
                .filter(item -> item.scopeType().equals(scopeType)
                        && item.scopeKey().equals(scopeKey))
                .findFirst()
                .filter(item -> !item.members().isEmpty())
                .ifPresent(item -> synchronizeMembers(
                        groupId, platform, item.members(), false));
    }

    private void synchronizeMembers(
            long groupId,
            BotPlatform platform,
            BotChannelInstanceRecord record,
            String scopeKey,
            List<BotDiscoveredMember> fallbackMembers) {
        if (record == null) {
            return;
        }
        BotMemberQueryResult query = discoveryRouter.queryMembers(record, scopeKey);
        if (query.complete()) {
            synchronizeMembers(groupId, platform, query.members(), true);
            return;
        }
        synchronizeMembers(groupId, platform, fallbackMembers, false);
    }

    private Map<Long, DiscoveryExecution> discoverInstances(
            Collection<BotChannelInstance> instances,
            Map<String, BotChannelInstanceRecord> records) {
        List<CompletableFuture<DiscoveryExecution>> futures = instances.stream()
                .map(instance -> CompletableFuture.supplyAsync(
                        () -> discoverInstance(instance, records.get(instance.getInstanceId())),
                        memberDiscoveryExecutor))
                .toList();
        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toMap(
                        execution -> execution.instance().getId(),
                        Function.identity(),
                        (left, right) -> left,
                        LinkedHashMap::new));
    }

    private DiscoveryExecution discoverInstance(
            BotChannelInstance instance, BotChannelInstanceRecord record) {
        try {
            BotScopeDiscoveryResult result = ObjectUtil.isNull(record)
                    ? BotScopeDiscoveryResult.unsupported("机器人实例配置不存在")
                    : discoveryRouter.discover(record);
            return new DiscoveryExecution(instance, result);
        } catch (RuntimeException exception) {
            return new DiscoveryExecution(
                    instance, BotScopeDiscoveryResult.unsupported(
                            StrUtil.blankToDefault(exception.getMessage(), "发现失败")));
        }
    }

    private record DiscoveryExecution(
            BotChannelInstance instance,
            BotScopeDiscoveryResult result) {
    }

    private void synchronizeMembers(
            long groupId,
            BotPlatform platform,
            List<BotDiscoveredMember> discoveredMembers,
            boolean membersComplete) {
        Set<String> discoveredIds = new HashSet<>();
        discoveredMembers.forEach(discovered -> {
            if (StrUtil.isBlank(discovered.platformUserId())) {
                return;
            }
            discoveredIds.add(discovered.platformUserId());
            BotGroupMember member = identityAdapters.find(
                            memberRepository, groupId, platform, discovered.platformUserId())
                    .orElseGet(BotGroupMember::new);
            member.setGroupId(groupId);
            identityAdapters.assign(platform, member, discovered.platformUserId());
            if (StrUtil.isNotBlank(discovered.displayName())) {
                member.setDisplayName(discovered.displayName().trim());
            }
            if (StrUtil.isNotBlank(discovered.role())) {
                member.setMemberRole(discovered.role());
            }
            member.setEnabled(true);
            memberRepository.save(member);
        });

        if (platform == BotPlatform.ONEBOT && membersComplete) {
            removeMissingOneBotMembers(groupId, discoveredIds);
        }
    }

    @Override
    @Transactional
    public void handleMemberJoined(
            BotPlatform platform, String botAccountId, String scopeKey, BotDiscoveredMember member) {
        if (platform == null || StrUtil.isBlank(botAccountId) || StrUtil.isBlank(scopeKey)
                || member == null
                || StrUtil.isBlank(member.platformUserId())) {
            return;
        }
        instanceRepository.findByPlatformAndBotAccountId(
                        platform, botAccountId.trim())
                .ifPresent(instance -> botRepository
                        .findAllByPlatformAndScopeTypeAndScopeKeyAndBotInstanceId(
                                platform.name(),
                                "GROUP",
                                scopeKey.trim(),
                                instance.getId())
                        .forEach(binding -> synchronizeMembers(
                                binding.getGroupId(),
                                platform,
                                List.of(member),
                                false)));
    }

    @Override
    @Transactional
    public void handleMemberLeft(
            BotPlatform platform, String botAccountId, String scopeKey, String platformUserId) {
        if (platform == null || StrUtil.isBlank(botAccountId) || StrUtil.isBlank(scopeKey)
                || StrUtil.isBlank(platformUserId)) {
            return;
        }
        instanceRepository.findByPlatformAndBotAccountId(
                        platform, botAccountId.trim())
                .ifPresent(instance -> botRepository
                        .findAllByPlatformAndScopeTypeAndScopeKeyAndBotInstanceId(
                                platform.name(),
                                "GROUP",
                                scopeKey.trim(),
                                instance.getId())
                        .forEach(binding -> removeMemberIdentity(
                                binding.getGroupId(), platform, platformUserId.trim())));
    }

    private void removeMissingOneBotMembers(long groupId, Set<String> discoveredIds) {
        memberRepository.findAllByGroupIdOrderByMemberRoleAscIdAsc(groupId).stream()
                .filter(member -> StrUtil.isNotBlank(member.getQqId()))
                .filter(member -> !discoveredIds.contains(member.getQqId()))
                .forEach(member -> removeMemberIdentity(groupId, BotPlatform.ONEBOT, member.getQqId()));
    }

    private void removeMemberIdentity(long groupId, BotPlatform platform, String platformUserId) {
        Optional<BotGroupMember> existing = switch (platform) {
            case ONEBOT -> memberRepository.findByGroupIdAndQqId(groupId, platformUserId);
            case QQ_OFFICIAL -> memberRepository.findByGroupIdAndMemberOpenid(groupId, platformUserId);
        };
        existing.ifPresent(member -> {
        identityAdapters.clear(platform, member);
            // A departure clears only that channel identity; linked identities and points survive.
            if (StrUtil.isBlank(member.getQqId())
                    && StrUtil.isBlank(member.getMemberOpenid())
                    && StrUtil.isBlank(member.getKookId())) {
                boolean hasPointHistory = pointTransactionRepository.existsByMemberId(
                        member.getId());
                if (!hasPointHistory
                        && member.getPoints() == 0
                        && member.getTotalEarned() == 0
                        && member.getTotalSpent() == 0) {
                    memberRepository.delete(member);
                    return;
                }
                member.setEnabled(false);
            }
            memberRepository.save(member);
        });
    }

    private BotGroup group(long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("逻辑群组不存在"));
    }

    private String trimToNull(String value) {
        return StrUtil.isBlank(value) ? null : value.trim();
    }
}
