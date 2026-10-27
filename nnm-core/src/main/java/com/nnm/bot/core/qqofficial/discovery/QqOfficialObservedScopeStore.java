package com.nnm.bot.core.qqofficial.discovery;

import io.github.rdt.lock.LockScope;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.core.channel.model.BotDiscoveredMember;
import com.nnm.bot.core.channel.model.BotDiscoveredScope;
import com.nnm.bot.core.config.BotCacheProperties;
import io.github.rdt.state.ExpiringMapStore;
import io.github.rdt.lock.DistributedLockManager;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.util.Locale;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.CacheManager;
import org.springframework.stereotype.Component;

/** 观察结果是可过期的共享发现数据，不代表平台全量群组或正式成员档案。 */
@Component
public class QqOfficialObservedScopeStore implements AutoCloseable {
    @Resource
    private ExpiringMapStore cache;
    @Resource
    private DistributedLockManager lockManager;
    @Resource
    private BotCacheProperties properties;
    private CacheManager manager;
    private Cache<String, Boolean> touches;

    @PostConstruct
    public void initialize() {
        manager = CacheManager.getInstance("qq-observations-" + UUID.randomUUID());
        touches = Cache2kBuilder.of(String.class, Boolean.class).manager(manager).name("touches")
                .entryCapacity(properties.observationTouchCapacity()).expireAfterWrite(properties.observationTouchInterval()).build();
    }

    public void observeGroup(String account, String group) {
        if (StrUtil.isBlank(account) || StrUtil.isBlank(group)) return;
        String key = account.length() + ":" + account + group;
        if (!touches.putIfAbsent(key, true)) return;
        try {
            update(account, group, true);
        } catch (RuntimeException failure) {
            // 写入失败不能把这次触摸当作成功，下一条事件必须允许重试。
            touches.remove(key);
            throw failure;
        }
    }

    public void deactivateGroup(String account, String group) {
        if (StrUtil.isBlank(account) || StrUtil.isBlank(group)) return;
        update(account, group, false);
        touches.remove(account.length() + ":" + account + group);
    }

    public void observeMember(String account, String group, String member, String name, String role) {
        if (StrUtil.isBlank(account) || StrUtil.isBlank(group) || StrUtil.isBlank(member)) return;
        try (LockScope scope = lockManager.beginScope()) {
            // 同一成员的名称、角色需要读改写；不同成员之间不再串行等待整群锁。
            lockManager.lock(memberLockName(account, group, member));
            ObservedMember previous = cache.<ObservedMember>get(memberMap(account, group), member, ObservedMember.class).orElse(null);
            cache.put(memberMap(account, group), member, new ObservedMember(member,
                    StrUtil.isBlank(name) ? previous == null ? null : previous.displayName() : name,
                    StrUtil.isBlank(role) ? previous == null ? null : previous.role() : role, Instant.now()),
                    properties.observationRetention());
        }
        observeGroup(account, group);
    }

    public void removeMember(String account, String group, String member) {
        if (StrUtil.isBlank(account) || StrUtil.isBlank(group) || StrUtil.isBlank(member)) return;
        try (LockScope scope = lockManager.beginScope()) {
            lockManager.lock(memberLockName(account, group, member));
            cache.remove(memberMap(account, group), member);
        }
    }

    public List<BotDiscoveredScope> findActive(String account) {
        if (StrUtil.isBlank(account)) return List.of();
        return cache.<GroupObservation>getAll(groupMap(account), GroupObservation.class).values().stream()
                .filter(group -> group.active() && live(group.lastObservedAt())).sorted(Comparator.comparing(GroupObservation::groupOpenid))
                .map(group -> {
                    List<BotDiscoveredMember> members = cache.<ObservedMember>getAll(
                                    memberMap(account, group.groupOpenid()), ObservedMember.class).values().stream()
                            .filter(member -> live(member.lastObservedAt()))
                            .sorted(Comparator.comparing(ObservedMember::memberOpenid))
                            .map(member -> new BotDiscoveredMember(member.memberOpenid(), member.displayName(), normalizeRole(member.role())))
                            .toList();
                    // 字段各自可能过期，观察数据始终按增量处理，不能用它批量删除数据库成员。
                    return new BotDiscoveredScope("GROUP", group.groupOpenid(), group.displayName(), group.memberCount(), members, false);
                }).toList();
    }

    private void update(String account, String group, boolean active) {
        try (LockScope scope = lockManager.beginScope()) {
            // 锁粒度缩小到单群，热门群不再阻塞同账号的其他群组。
            lockManager.lock(lockName(account, group));
            writeGroup(account, group, active);
        }
    }

    private void writeGroup(String account, String group, boolean active) {
        GroupObservation previous = cache.<GroupObservation>get(groupMap(account), group, GroupObservation.class).orElse(null);
        cache.put(groupMap(account), group, new GroupObservation(group,
                previous == null ? null : previous.displayName(), previous == null ? null : previous.memberCount(),
                active, Instant.now()), properties.observationRetention());
    }

    private boolean live(Instant observedAt) {
        return observedAt != null && observedAt.plus(properties.observationRetention()).isAfter(Instant.now());
    }

    private String memberLockName(String account, String group, String member) {
        return lockName(account, group) + ":member:" + group.length() + ":" + member;
    }

    private String normalizeRole(String role) {
        return switch (StrUtil.emptyIfNull(role).toLowerCase(Locale.ROOT)) {
            case "owner" -> "OWNER";
            case "admin" -> "ADMIN";
            default -> "MEMBER";
        };
    }

    private String groupMap(String account) { return "qq-observed-groups:" + account; }
    private String memberMap(String account, String group) { return "qq-observed-members:" + account.length() + ":" + account + group; }
    private String lockName(String account, String group) { return "qq-observed-group:" + account.length() + ":" + account + group; }

    @PreDestroy
    @Override
    public void close() {
        if (manager != null) manager.close();
    }

    public record GroupObservation(String groupOpenid, String displayName, Integer memberCount,
                                   boolean active, Instant lastObservedAt) {
    }

    public record ObservedMember(String memberOpenid, String displayName, String role, Instant lastObservedAt) {
    }
}
