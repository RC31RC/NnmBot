package com.nnm.bot.admin.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.nnm.bot.admin.domain.BotChannelInstance;
import com.nnm.bot.admin.dto.bot.BotGroupBotSaveRequest;
import com.nnm.bot.admin.dto.bot.BotGroupCreateRequest;
import com.nnm.bot.admin.dto.bot.BotGroupItem;
import com.nnm.bot.admin.dto.bot.BotGroupMemberItem;
import com.nnm.bot.admin.dto.bot.BotGroupMemberPointsUpdateRequest;
import com.nnm.bot.admin.dto.bot.BotGroupMemberUpdateRequest;
import com.nnm.bot.admin.dto.bot.BotGroupUpdateRequest;
import com.nnm.bot.admin.dto.bot.BotScopeDiscoveryItem;
import com.nnm.bot.admin.repository.BotChannelInstanceRepository;
import com.nnm.bot.admin.service.BotGroupManagementService;
import com.nnm.bot.common.exception.DataNotFoundException;
import com.nnm.bot.common.task.TaskProgressReporter;
import com.nnm.bot.core.channel.port.BotChannelInstanceProvider;
import com.nnm.bot.core.channel.model.BotChannelInstanceRecord;
import com.nnm.bot.core.channel.model.BotChannelKey;
import com.nnm.bot.core.channel.model.BotChannelSnapshot;
import com.nnm.bot.core.channel.model.BotDiscoveredMember;
import com.nnm.bot.core.channel.model.BotScopeDiscoveryResult;
import com.nnm.bot.core.channel.port.BotScopeDiscoveryProvider;
import com.nnm.bot.core.channel.registry.BotChannelRegistry;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.group.domain.BotGroup;
import com.nnm.bot.core.group.domain.BotGroupBot;
import com.nnm.bot.core.group.domain.BotGroupMember;
import com.nnm.bot.core.group.domain.BotGroupPointTransaction;
import com.nnm.bot.core.group.repository.BotGroupBotRepository;
import com.nnm.bot.core.group.repository.BotGroupMemberRepository;
import com.nnm.bot.core.group.repository.BotGroupPointTransactionRepository;
import com.nnm.bot.core.group.repository.BotGroupRepository;
import com.nnm.bot.core.group.service.PointAccountService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
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
    private BotChannelInstanceRepository instanceRepository;
    @Resource
    private BotChannelInstanceProvider instanceProvider;
    @Resource
    private List<BotScopeDiscoveryProvider> discoveryProviders;
    @Resource
    private BotChannelRegistry channelRegistry;
    @Resource(name = "memberDiscoveryExecutor")
    private ExecutorService memberDiscoveryExecutor;
    @Resource
    private com.nnm.bot.core.config.ExecutorProperties executorProperties;

    private volatile Semaphore discoverySemaphore;

    @jakarta.annotation.PostConstruct
    void initializeDiscoveryLimiter() {
        discoverySemaphore = new Semaphore(executorProperties.getDiscoveryConcurrency());
    }

    @Override
    public List<BotGroupItem> list() {
        Map<Long, BotChannelInstance> instances = instanceRepository.findAll().stream()
                .collect(Collectors.toMap(BotChannelInstance::getId, Function.identity()));
        return groupRepository.findAllByOrderByNameAscIdAsc().stream()
                .map(group -> new BotGroupItem(
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
    public List<BotScopeDiscoveryItem> discover() {
        return discover(false, TaskProgressReporter.noop());
    }

    @Override
    public List<BotScopeDiscoveryItem> discover(TaskProgressReporter reporter) {
        return discover(false, reporter);
    }

    @Override
    @Transactional
    public List<BotScopeDiscoveryItem> discoverAndSynchronize() {
        return discover(true, TaskProgressReporter.noop());
    }

    @Override
    @Transactional
    public List<BotScopeDiscoveryItem> discoverAndSynchronize(
            TaskProgressReporter reporter) {
        return discover(true, reporter);
    }

    private List<BotScopeDiscoveryItem> discover(
            boolean synchronizeBoundMembers, TaskProgressReporter reporter) {
        TaskProgressReporter progress = reporter == null
                ? TaskProgressReporter.noop() : reporter;
        Map<String, BotChannelInstanceRecord> records = instanceProvider.findAll().stream()
                .collect(Collectors.toMap(BotChannelInstanceRecord::instanceId, Function.identity()));
        List<BotChannelInstance> instances =
                instanceRepository.findAllByOrderByPlatformAscInstanceIdAsc();
        progress.report("DISCOVERING_BOTS", "正在调用机器人发现功能", 0, instances.size());
        Map<Long, DiscoveryExecution> executions = discoverInstances(instances, records);
        List<BotScopeDiscoveryItem> result = new ArrayList<>(instances.size());
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
                            scope.members(),
                            scope.membersComplete()));
            progress.report("PROCESSING_MEMBERS",
                    "已刷新机器人群组 " + binding.getScopeKey(),
                    ++processed,
                    bindings.size());
        }
        progress.complete("当前群组成员刷新完成");
    }

    @Override
    @Transactional
    public long create(BotGroupCreateRequest request) {
        String code = request.code().trim().toLowerCase(java.util.Locale.ROOT);
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
    @Transactional
    public void update(long id, BotGroupUpdateRequest request) {
        BotGroup group = group(id);
        group.setName(request.name().trim());
        group.setDescription(trimToNull(request.description()));
        group.setEnabled(request.enabled());
        groupRepository.save(group);
    }

    @Override
    @Transactional
    public void delete(long id) {
        group(id);
        botRepository.deleteAllByGroupId(id);
        memberRepository.deleteAllByGroupId(id);
        groupRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void bindBot(long groupId, BotGroupBotSaveRequest request) {
        group(groupId);
        BotChannelInstance instance = instanceRepository.findById(request.botInstanceId())
                .orElseThrow(() -> new DataNotFoundException("机器人实例不存在"));
        if (instance.getPlatform() != request.platform()) {
            throw new IllegalArgumentException("机器人实例平台与群组绑定平台不一致");
        }
        String scopeType = request.scopeType().trim().toUpperCase();
        String scopeKey = request.scopeKey().trim();
        if (botRepository.findByGroupIdAndPlatformAndScopeTypeAndScopeKeyAndBotInstanceId(
                groupId, request.platform().name(), scopeType, scopeKey, instance.getId()).isPresent()) {
            throw new IllegalArgumentException("该机器人已经绑定到当前群组");
        }
        BotGroupBot binding = new BotGroupBot();
        binding.setGroupId(groupId);
        binding.setBotInstanceId(instance.getId());
        binding.setPlatform(request.platform().name());
        binding.setScopeType(scopeType);
        binding.setScopeKey(scopeKey);
        binding.setDisplayName(trimToNull(request.displayName()));
        binding.setEnabled(request.enabled());
        botRepository.save(binding);

        if (Boolean.TRUE.equals(request.syncMembers()) && "GROUP".equals(scopeType)) {
            synchronizeMembers(groupId, request.platform(), scopeType, scopeKey, instance);
        }
    }

    @Override
    @Transactional
    public void unbindBot(long groupId, long bindingId) {
        group(groupId);
        BotGroupBot binding = botRepository.findById(bindingId)
                .filter(item -> item.getGroupId().equals(groupId))
                .orElseThrow(() -> new DataNotFoundException("群组机器人绑定不存在"));
        botRepository.delete(binding);
    }

    @Override
    public List<BotGroupMemberItem> members(long groupId) {
        group(groupId);
        return memberRepository.findAllByGroupIdOrderByMemberRoleAscIdAsc(groupId)
                .stream()
                .map(member -> new BotGroupMemberItem(
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
    public void updateMember(long groupId, long memberId, BotGroupMemberUpdateRequest request) {
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
    @Transactional
    public void updateMemberPoints(long groupId, long memberId,
                                   BotGroupMemberPointsUpdateRequest request) {
        group(groupId);
        pointAccountService.adjust(
                groupId,
                memberId,
                request.points(),
                "ADMIN_ADJUSTMENT:" + groupId + ":" + memberId + ":"
                        + java.util.UUID.randomUUID(),
                "后台管理员调整成员积分");
    }

    private BotGroupItem.BotBindingItem binding(
            BotGroupBot binding, BotChannelInstance instance) {
        BotChannelSnapshot snapshot = channelSnapshot(instance).orElse(null);
        return new BotGroupItem.BotBindingItem(
                binding.getId(),
                binding.getBotInstanceId(),
                ObjectUtil.isNull(instance) ? "-" : instance.getInstanceId(),
                ObjectUtil.isNull(instance) ? "-" : instance.getDisplayName(),
                binding.getPlatform(),
                binding.getScopeType(),
                binding.getScopeKey(),
                binding.getDisplayName(),
                binding.isEnabled(),
                ObjectUtil.isNull(snapshot) ? null : snapshot.status(),
                ObjectUtil.isNull(snapshot) ? null : snapshot.statusChangedAt(),
                ObjectUtil.isNull(snapshot) ? null : snapshot.lastError());
    }

    private BotScopeDiscoveryItem discover(
            BotChannelInstance instance,
            DiscoveryExecution execution,
            boolean synchronizeBoundMembers) {
        BotScopeDiscoveryResult result = ObjectUtil.isNull(execution)
                ? BotScopeDiscoveryResult.unsupported("渠道未提供发现能力")
                : execution.result();
        return new BotScopeDiscoveryItem(
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
                            return new com.nnm.bot.admin.dto.bot.BotScopeDiscoveryScopeItem(
                                    scope.scopeType(),
                                    scope.scopeKey(),
                                    scope.displayName(),
                                    scope.memberCount(),
                                    binding.isPresent(),
                                    binding.map(BotGroupBot::getGroupId).orElse(null),
                                    scope.members().stream()
                                            .map(member ->
                                                    new com.nnm.bot.admin.dto.bot
                                                            .BotScopeDiscoveryMemberItem(
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
        return channelRegistry.find(new BotChannelKey(
                instance.getPlatform(), instance.getBotAccountId()));
    }

    private void synchronizeMembers(
            long groupId, BotPlatform platform, String scopeType, String scopeKey,
            BotChannelInstance instance) {
        BotChannelInstanceRecord record = instanceProvider.findAll().stream()
                .filter(item -> item.instanceId().equals(instance.getInstanceId()))
                .findFirst()
                .orElse(null);
        BotScopeDiscoveryProvider provider = discoveryProviders.stream()
                .filter(item -> item.platform() == platform)
                .findFirst()
                .orElse(null);
        if (ObjectUtil.isNull(record) || ObjectUtil.isNull(provider)) {
            return;
        }
        provider.discover(record).scopes().stream()
                .filter(item -> item.scopeType().equals(scopeType)
                        && item.scopeKey().equals(scopeKey))
                .findFirst()
                .ifPresent(item -> synchronizeMembers(
                        groupId, platform, item.members(), item.membersComplete()));
    }

    private Map<Long, DiscoveryExecution> discoverInstances(
            Collection<BotChannelInstance> instances,
            Map<String, BotChannelInstanceRecord> records) {
        List<CompletableFuture<DiscoveryExecution>> futures = instances.stream()
                .map(instance -> CompletableFuture.supplyAsync(
                        () -> discoverInstance(instance, records.get(instance.getInstanceId())),
                        discoveryExecutor()))
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
        boolean acquired = false;
        try {
            discoveryLimiter().acquire();
            acquired = true;
            BotScopeDiscoveryProvider provider = discoveryProviders.stream()
                    .filter(item -> item.platform() == instance.getPlatform())
                    .findFirst()
                    .orElse(null);
            BotScopeDiscoveryResult result =
                    ObjectUtil.isNull(provider) || ObjectUtil.isNull(record)
                            ? BotScopeDiscoveryResult.unsupported("渠道未提供发现能力")
                            : provider.discover(record);
            return new DiscoveryExecution(instance, result);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return new DiscoveryExecution(
                    instance, BotScopeDiscoveryResult.unsupported("发现任务被中断"));
        } catch (RuntimeException exception) {
            return new DiscoveryExecution(
                    instance, BotScopeDiscoveryResult.unsupported(
                            StrUtil.blankToDefault(exception.getMessage(), "发现失败")));
        } finally {
            if (acquired) {
                discoveryLimiter().release();
            }
        }
    }

    private Executor discoveryExecutor() {
        // 允许纯单元测试直接实例化服务；Spring 运行时始终使用虚拟线程 Bean。
        return ObjectUtil.isNull(memberDiscoveryExecutor)
                ? Runnable::run
                : memberDiscoveryExecutor;
    }

    private Semaphore discoveryLimiter() {
        Semaphore limiter = discoverySemaphore;
        if (limiter != null) {
            return limiter;
        }
        synchronized (this) {
            limiter = discoverySemaphore;
            if (limiter == null) {
                int concurrency = ObjectUtil.isNull(executorProperties)
                        ? 4
                        : executorProperties.getDiscoveryConcurrency();
                limiter = new Semaphore(Math.max(1, concurrency));
                discoverySemaphore = limiter;
            }
            return limiter;
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
            BotGroupMember member = platform == BotPlatform.ONEBOT
                    ? memberRepository.findByGroupIdAndQqId(
                            groupId, discovered.platformUserId())
                    .orElseGet(BotGroupMember::new)
                    : platform == BotPlatform.QQ_OFFICIAL
                    ? memberRepository.findByGroupIdAndMemberOpenid(
                            groupId, discovered.platformUserId())
                    .orElseGet(BotGroupMember::new)
                    : memberRepository.findByGroupIdAndKookId(
                            groupId, discovered.platformUserId())
                    .orElseGet(BotGroupMember::new);
            member.setGroupId(groupId);
            if (platform == BotPlatform.ONEBOT) {
                member.setQqId(discovered.platformUserId());
                member.setQqIdVerified(true);
                if (ObjectUtil.isNull(member.getQqBoundAt())) {
                    member.setQqBoundAt(LocalDateTime.now());
                }
            } else if (platform == BotPlatform.QQ_OFFICIAL) {
                member.setMemberOpenid(discovered.platformUserId());
            } else {
                member.setKookId(discovered.platformUserId());
            }
            member.setDisplayName(trimToNull(discovered.displayName()));
            member.setMemberRole(discovered.role());
            member.setEnabled(true);
            memberRepository.save(member);
        });

        if (platform == BotPlatform.ONEBOT && membersComplete) {
            removeMissingOneBotMembers(groupId, discoveredIds);
        }
    }

    @Override
    @Transactional
    public void handleOneBotMemberJoined(
            String botAccountId, long groupId, BotDiscoveredMember member) {
        if (StrUtil.isBlank(botAccountId)
                || member == null
                || StrUtil.isBlank(member.platformUserId())) {
            return;
        }
        instanceRepository.findByPlatformAndBotAccountId(
                        BotPlatform.ONEBOT, botAccountId.trim())
                .ifPresent(instance -> botRepository
                        .findAllByPlatformAndScopeTypeAndScopeKeyAndBotInstanceId(
                                BotPlatform.ONEBOT.name(),
                                "GROUP",
                                String.valueOf(groupId),
                                instance.getId())
                        .forEach(binding -> synchronizeMembers(
                                binding.getGroupId(),
                                BotPlatform.ONEBOT,
                                List.of(member),
                                false)));
    }

    @Override
    @Transactional
    public void handleOneBotMemberLeft(String botAccountId, long groupId, long userId) {
        if (StrUtil.isBlank(botAccountId)) {
            return;
        }
        instanceRepository.findByPlatformAndBotAccountId(
                        BotPlatform.ONEBOT, botAccountId.trim())
                .ifPresent(instance -> botRepository
                        .findAllByPlatformAndScopeTypeAndScopeKeyAndBotInstanceId(
                                BotPlatform.ONEBOT.name(),
                                "GROUP",
                                String.valueOf(groupId),
                                instance.getId())
                        .forEach(binding -> removeMissingOneBotMember(
                                binding.getGroupId(), String.valueOf(userId))));
    }

    private void removeMissingOneBotMembers(long groupId, Set<String> discoveredIds) {
        memberRepository.findAllByGroupIdOrderByMemberRoleAscIdAsc(groupId).stream()
                .filter(member -> StrUtil.isNotBlank(member.getQqId()))
                .filter(member -> !discoveredIds.contains(member.getQqId()))
                .forEach(member -> removeMissingOneBotMember(groupId, member.getQqId()));
    }

    private void removeMissingOneBotMember(long groupId, String qqId) {
        memberRepository.findByGroupIdAndQqId(groupId, qqId).ifPresent(member -> {
            member.setQqId(null);
            member.setQqIdVerified(false);
            member.setQqBoundAt(null);
            if (StrUtil.isBlank(member.getMemberOpenid())
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
