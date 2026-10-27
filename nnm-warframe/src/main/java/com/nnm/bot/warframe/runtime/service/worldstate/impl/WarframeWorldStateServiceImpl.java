package com.nnm.bot.warframe.runtime.service.worldstate.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.common.http.HttpClient.HttpResult;
import com.nnm.bot.common.task.TaskProgressReporter;
import com.nnm.bot.warframe.runtime.cache.worldstate.WorldStateRuntimeSnapshot;
import com.nnm.bot.warframe.runtime.client.worldstate.WarframeWorldStateApiClient;
import com.nnm.bot.warframe.runtime.event.worldstate.WorldStateUpdatedEvent;
import com.nnm.bot.warframe.runtime.mapper.worldstate.OfficialWorldStateMapper;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeBountyCycleService;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeCycleService;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeWorldStateService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

/** 编排官方动态数据下载、类型绑定、补全与原子快照发布。 */
@Slf4j
@Service
public class WarframeWorldStateServiceImpl implements WarframeWorldStateService {
    private final ReentrantLock refreshLock = new ReentrantLock();

    /** 发布单实例内的世界状态更新事件，不承担 MQ 投递。 */
    @Resource
    private ApplicationEventPublisher eventPublisher;
    /** 绑定官方 JSON 输入的 Jackson 映射器。 */
    @Resource
    private ObjectMapper objectMapper;
    /** 官方世界状态 HTTP 客户端。 */
    @Resource
    private WarframeWorldStateApiClient officialApiClient;
    /** 类型化原始对象到稳定运行时 DTO 的映射器。 */
    @Resource
    private OfficialWorldStateMapper worldStateMapper;
    /** 根据已配置时间锚点补全本地周期。 */
    @Resource
    private WarframeCycleService cycleService;
    /** 编排赏金动态补充信息与官方静态数据解析。 */
    @Resource
    private WarframeBountyCycleService bountyCycleService;
    /** 最近一次完整的本机快照，所有分区必须一起替换。 */
    private final AtomicReference<WorldStateRuntimeSnapshot> currentSnapshot =
            new AtomicReference<>();

    /**
     * 接收已有运行时投影并统一发布本地状态更新事件。
     *
     * @param newState 新建状态。
     */
    public void processNewState(WorldStateDTO newState) {
        Objects.requireNonNull(newState, "newState must not be null");
        applySnapshot(WorldStateRuntimeSnapshot.fromState(newState));
    }

    /**
     * 一次替换完整快照，避免原始输入与展示结果来自不同响应。
     *
     * @param newSnapshot 新建快照。
     */
    private void applySnapshot(WorldStateRuntimeSnapshot newSnapshot) {
        refreshLock.lock();
        try {
            WorldStateDTO newState = Objects.requireNonNull(newSnapshot.state(),
                    "newSnapshot.state must not be null");
            // 原子替换完整快照，读取线程不会看到领域状态与原始 JSON 的半更新结果。
            WorldStateRuntimeSnapshot oldSnapshot = currentSnapshot.getAndSet(newSnapshot);
            WorldStateDTO oldState = oldSnapshot == null ? null : oldSnapshot.state();

            if (ObjectUtil.isNull(oldState)) {
                log.info("Initial world state loaded");
                eventPublisher.publishEvent(new WorldStateUpdatedEvent(null, newState, null));
                return;
            }

            eventPublisher.publishEvent(new WorldStateUpdatedEvent(oldState, newState, null));
        } finally {
            refreshLock.unlock();
        }
    }

    /**
     * 读取当前状态并重算由本机时钟驱动的周期。
     *
     * @return 已经解析并校验的世界状态快照。
     */
    @Override
    public WorldStateDTO getCurrentState() {
        WorldStateRuntimeSnapshot snapshot = getCurrentSnapshot();
        return snapshot == null ? null : cycleService.resolve(snapshot.state());
    }

    /**
     * 读取最近一次成功的原始数据和解析结果。
     *
     * @return 最近一次完整的本机快照，所有分区必须一起替换。。
     */
    @Override
    public WorldStateRuntimeSnapshot getCurrentSnapshot() {
        return currentSnapshot.get();
    }

    /**
     * 管理端手动刷新与定时轮询共用同一条处理链，确保变更检测和事件发布行为一致。
     *
     * @return 已经解析并校验的世界状态快照。
     */
    @Override
    public WorldStateDTO refresh() {
        return refresh(TaskProgressReporter.noop());
    }

    /**
     * 供定时任务和管理端共用的刷新入口；失败时不替换已有成功快照。
     *
     * @param reporter 向管理端任务提交进度的回调。
     * @return 已经解析并校验的世界状态快照。
     */
    @Override
    public WorldStateDTO refresh(TaskProgressReporter reporter) {
        refreshLock.lock();
        try {
            TaskProgressReporter progress = reporter == null
                    ? TaskProgressReporter.noop() : reporter;
            progress.report("DOWNLOADING_WORLD_STATE", "正在拉取 Warframe 世界数据");
            HttpResult result = officialApiClient.fetchWorldStateResult();
            if (result == null || !result.isSuccess()
                    || StrUtil.isBlank(result.body())) {
                // 保留最近一次成功的 JVM 内存快照，由本机下一个轮询周期重试。
                throw new SystemException("Warframe WorldStateDTO upstream unavailable");
            }
            progress.report("PARSING_WORLD_STATE", "正在解析 Warframe 世界数据");
            String source = "warframe-official";
            OfficialWorldStateRawData rawData;
            try {
                rawData = objectMapper.readValue(result.body(), OfficialWorldStateRawData.class);
            } catch (JsonProcessingException exception) {
                throw new IllegalArgumentException("官方世界状态字段绑定失败：" + exception.getMessage(), exception);
            }
            WorldStateDTO state = cycleService.resolve(bountyCycleService.enrich(
                    worldStateMapper.map(rawData, source, result.body())));
            applySnapshot(new WorldStateRuntimeSnapshot(
                    rawData,
                    state,
                    source,
                    result.body(),
                    sha256(result.body()),
                    state.metadata().fetchedAt(),
                    topLevelFields(result.body())));
            progress.complete("Warframe 世界数据已更新");
            return state;
        } finally {
            refreshLock.unlock();
        }
    }

    /**
     * 只扫描顶层字段名供排障使用，不把整个响应再次读成 JSON 树。
     *
     * @param rawJson 用于审核或排障的原始响应文本。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<String> topLevelFields(String rawJson) {
        try (JsonParser parser = objectMapper.getFactory().createParser(rawJson)) {
            if (parser.nextToken() != JsonToken.START_OBJECT) {
                throw new IllegalArgumentException("官方世界状态根输入必须为对象");
            }
            List<String> fields = new ArrayList<>();
            while (parser.nextToken() == JsonToken.FIELD_NAME) {
                fields.add(parser.currentName());
                parser.nextToken();
                parser.skipChildren();
            }
            return List.copyOf(fields);
        } catch (IOException exception) {
            throw new IllegalArgumentException("无法读取官方世界状态字段目录", exception);
        }
    }

    /**
     * 对上游完整原文生成 SHA-256 摘要，用于快照审计。
     *
     * @param value 待校验或规范化的文本。
     * @return 输入内容的 SHA-256 十六进制摘要。
     */
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
