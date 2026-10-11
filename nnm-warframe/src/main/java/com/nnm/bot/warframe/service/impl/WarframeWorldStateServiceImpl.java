package com.nnm.bot.warframe.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import org.springframework.context.event.EventListener;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.task.TaskProgressReporter;
import com.nnm.bot.warframe.application.WarframeWorldStateService;
import com.nnm.bot.warframe.application.WorldStateRuntimeSnapshot;
import com.nnm.bot.warframe.application.WorldStateUpdatedEvent;
import com.nnm.bot.warframe.application.WarframeCycleService;
import com.nnm.bot.warframe.messaging.model.WarframeWorldStateAvailableEvent;
import com.nnm.bot.warframe.domain.ChangeDetector;
import com.nnm.bot.warframe.model.WorldState;
import com.nnm.bot.warframe.client.WarframeOfficialApiClient;
import com.nnm.bot.warframe.cache.SharedWorldStateStore;
import com.nnm.bot.warframe.parser.WorldStateParser;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class WarframeWorldStateServiceImpl implements WarframeWorldStateService {

    @Resource
    private ObjectProvider<ChangeDetector<WorldState>> detectorProvider;
    @Resource
    private ApplicationEventPublisher eventPublisher;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private WarframeOfficialApiClient officialApiClient;
    @Resource
    private WorldStateParser worldStateParser;
    @Resource
    private WarframeCycleService cycleService;
    private List<ChangeDetector<WorldState>> detectors = List.of();
    @Resource
    private ObjectProvider<SharedWorldStateStore> sharedStore;

    private final AtomicReference<WorldStateRuntimeSnapshot> currentSnapshot =
            new AtomicReference<>();

    @PostConstruct
    void initializeDetectors() {
        detectors = detectorProvider.orderedStream().toList();
    }

    public void processNewState(WorldState newState) {
        Objects.requireNonNull(newState, "newState must not be null");
        var store = sharedStore == null ? null : sharedStore.getIfAvailable();
        if (store == null) {
            processNewSnapshot(WorldStateRuntimeSnapshot.fromState(newState));
        } else {
            store.refresh(() -> {
                processNewSnapshot(WorldStateRuntimeSnapshot.fromState(newState));
                return null;
            });
        }
    }

    private synchronized void processNewSnapshot(WorldStateRuntimeSnapshot newSnapshot) {
        var store = sharedStore == null ? null : sharedStore.getIfAvailable();
        if (store != null) {
            store.write(newSnapshot);
        }
        applySnapshot(newSnapshot);
    }

    private synchronized void applySnapshot(WorldStateRuntimeSnapshot newSnapshot) {
        WorldState newState = Objects.requireNonNull(newSnapshot.state(),
                "newSnapshot.state must not be null");
        // 原子替换完整快照，读取线程不会看到领域状态与原始 JSON 的半更新结果。
        // Redis 提供跨节点当前快照，本地引用用于变更检测，不写历史快照表。
        WorldStateRuntimeSnapshot oldSnapshot = currentSnapshot.getAndSet(newSnapshot);
        WorldState oldState = oldSnapshot == null ? null : oldSnapshot.state();

        if (ObjectUtil.isNull(oldState)) {
            log.info("Initial world state loaded");
            eventPublisher.publishEvent(new WorldStateUpdatedEvent(null, newState, List.of(), null));
            return;
        }

        LinkedHashSet<String> changedSections = new LinkedHashSet<>();
        for (ChangeDetector<WorldState> detector : detectors) {
            String detectorType = detectorType(detector);
            try {
                if (detector.hasChanged(oldState, newState)) {
                    changedSections.add(detectorType);
                    log.debug("Change detected by: {}", detectorType);
                }
            } catch (Exception e) {
                log.error("Detector [{}] failed", detectorType, e);
            }
        }
        eventPublisher.publishEvent(new WorldStateUpdatedEvent(oldState, newState,
                List.copyOf(changedSections), null));
    }

    @Override
    public WorldState getCurrentState() {
        WorldStateRuntimeSnapshot snapshot = getCurrentSnapshot();
        return snapshot == null ? null : cycleService.resolve(snapshot.state());
    }

    @Override
    public WorldStateRuntimeSnapshot getCurrentSnapshot() {
        var store = sharedStore == null ? null : sharedStore.getIfAvailable();
        if (store != null) {
            return store.read();
        }
        return currentSnapshot.get();
    }

    @EventListener
    public synchronized void onSharedSnapshot(
            WarframeWorldStateAvailableEvent ignored) {
        WorldStateRuntimeSnapshot snapshot = getCurrentSnapshot();
        if (snapshot != null && !Objects.equals(snapshot, currentSnapshot.get())) {
            applySnapshot(snapshot);
        }
    }

    /**
     * 管理端手动刷新与定时轮询共用同一条处理链，确保变更检测和事件发布行为一致。
     */
    public WorldState refresh() {
        return refresh(TaskProgressReporter.noop());
    }

    @Override
    public WorldState refresh(TaskProgressReporter reporter) {
        var store = sharedStore == null ? null : sharedStore.getIfAvailable();
        return store == null ? refreshLocally(reporter) : store.refresh(() -> refreshLocally(reporter));
    }

    private WorldState refreshLocally(TaskProgressReporter reporter) {
        TaskProgressReporter progress = reporter == null
                ? TaskProgressReporter.noop() : reporter;
        progress.report("DOWNLOADING_WORLD_STATE", "正在拉取 Warframe 世界数据");
        var result = officialApiClient.fetchWorldStateResult();
        if (result == null || !result.isSuccess()
                || StrUtil.isBlank(result.body())) {
            if (sharedStore != null && sharedStore.getIfAvailable() != null) {
                // Keep the last good snapshot, but let Stream retry/report upstream failures.
                throw new IllegalStateException("Warframe WorldState upstream unavailable");
            }
            progress.report("COMPLETED", "未获取到新数据，保留当前世界数据", 1, 1);
            return getCurrentState();
        }
        progress.report("PARSING_WORLD_STATE", "正在解析 Warframe 世界数据");
        String source = "warframe-official";
        WorldState state = cycleService.resolve(worldStateParser.parse(result.body(), source));
        processNewSnapshot(new WorldStateRuntimeSnapshot(
                state,
                source,
                result.body(),
                sha256(result.body()),
                state.metadata().fetchedAt(),
                topLevelFields(result.body())));
        progress.complete("Warframe 世界数据已更新");
        return state;
    }

    private String detectorType(ChangeDetector<WorldState> detector) {
        try {
            String type = detector.type();
            return StrUtil.isBlank(type) ? detector.getClass().getName() : type.trim();
        } catch (Exception e) {
            return detector.getClass().getName();
        }
    }

    private List<String> topLevelFields(String rawJson) {
        try {
            JsonNode root = objectMapper.readTree(rawJson);
            if (root == null || !root.isObject()) {
                return List.of();
            }
            List<String> fields = new ArrayList<>();
            root.fieldNames().forEachRemaining(fields::add);
            return List.copyOf(fields);
        } catch (Exception exception) {
            log.warn("Unable to inspect WorldState top-level fields", exception);
            return List.of();
        }
    }

    private static String sha256(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
