package com.nnm.bot.warframe.application;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.task.TaskProgressReporter;
import com.nnm.bot.warframe.domain.ChangeDetector;
import com.nnm.bot.warframe.domain.model.WorldState;
import com.nnm.bot.warframe.infrastructure.parser.WorldStateParser;
import com.nnm.bot.warframe.infrastructure.provider.WorldStateProvider;
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
public class WorldStateService {

    @Resource
    private ObjectProvider<ChangeDetector<WorldState>> detectorProvider;
    @Resource
    private ApplicationEventPublisher eventPublisher;
    @Resource
    private ObjectMapper objectMapper;
    private List<ChangeDetector<WorldState>> detectors = List.of();

    private final AtomicReference<WorldStateRuntimeSnapshot> currentSnapshot =
            new AtomicReference<>();

    @PostConstruct
    void initializeDetectors() {
        detectors = detectorProvider.orderedStream().toList();
    }

    public void processNewState(WorldState newState) {
        Objects.requireNonNull(newState, "newState must not be null");
        processNewSnapshot(WorldStateRuntimeSnapshot.fromState(newState));
    }

    private void processNewSnapshot(WorldStateRuntimeSnapshot newSnapshot) {
        WorldState newState = Objects.requireNonNull(newSnapshot.state(),
                "newSnapshot.state must not be null");
        // 原子替换完整快照，读取线程不会看到领域状态与原始 JSON 的半更新结果。
        // 世界数据只保存在当前 JVM 内存，不写入数据库或历史快照表。
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

    public WorldState getCurrentState() {
        WorldStateRuntimeSnapshot snapshot = currentSnapshot.get();
        return snapshot == null ? null : snapshot.state();
    }

    public WorldStateRuntimeSnapshot getCurrentSnapshot() {
        return currentSnapshot.get();
    }

    /**
     * 管理端手动刷新与定时轮询共用同一条处理链，确保变更检测和事件发布行为一致。
     */
    public WorldState refresh(WorldStateProvider provider, WorldStateParser parser) {
        return refresh(provider, parser, TaskProgressReporter.noop());
    }

    public WorldState refresh(
            WorldStateProvider provider,
            WorldStateParser parser,
            TaskProgressReporter reporter) {
        Objects.requireNonNull(provider, "provider must not be null");
        Objects.requireNonNull(parser, "parser must not be null");
        TaskProgressReporter progress = reporter == null
                ? TaskProgressReporter.noop() : reporter;
        progress.report("DOWNLOADING_WORLD_STATE", "正在拉取 Warframe 世界数据");
        return provider.fetch()
                .map(payload -> {
                    progress.report("PARSING_WORLD_STATE", "正在解析 Warframe 世界数据");
                    WorldState state = parser.parse(payload.body(), payload.source());
                    processNewSnapshot(new WorldStateRuntimeSnapshot(
                            state,
                            payload.source(),
                            payload.body(),
                            sha256(payload.body()),
                            state.metadata().fetchedAt(),
                            topLevelFields(payload.body())));
                    progress.complete("Warframe 世界数据已更新");
                    return state;
                })
                .orElseGet(() -> {
                    progress.report("COMPLETED", "未获取到新数据，保留当前世界数据", 1, 1);
                    return getCurrentState();
                });
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
            List<String> fields = new java.util.ArrayList<>();
            root.fieldNames().forEachRemaining(fields::add);
            return List.copyOf(fields);
        } catch (Exception exception) {
            log.warn("Unable to inspect WorldState top-level fields", exception);
            return List.of();
        }
    }

    private static String sha256(String value) {
        try {
            byte[] digest = java.security.MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (java.security.NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
