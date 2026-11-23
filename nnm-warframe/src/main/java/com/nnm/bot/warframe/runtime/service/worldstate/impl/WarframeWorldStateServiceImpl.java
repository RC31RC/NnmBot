package com.nnm.bot.warframe.runtime.service.worldstate.impl;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.common.error.SystemException;
import com.nnm.bot.warframe.constant.WarframeManagementErrorCode;
import com.nnm.bot.common.http.HttpClient.HttpResult;
import com.nnm.bot.core.task.service.TaskProgressReporter;
import com.nnm.bot.warframe.runtime.cache.worldstate.WorldStateRuntimeSnapshot;
import com.nnm.bot.warframe.runtime.client.worldstate.WarframeWorldStateApiClient;
import com.nnm.bot.warframe.runtime.event.worldstate.WorldStateUpdatedEvent;
import com.nnm.bot.warframe.runtime.mapper.worldstate.OfficialWorldStateMapper;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeBountyCycleService;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeInvasionEnrichmentService;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeCycleService;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeWorldStateService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
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
    /** 只补全匹配官方轮次的双方入侵任务；失败仍发布官方结果。 */
    @Resource
    private WarframeInvasionEnrichmentService invasionEnrichmentService;
    /** 最近一次完整的本机快照，所有分区必须一起替换。 */
    private final AtomicReference<WorldStateRuntimeSnapshot> currentSnapshot = new AtomicReference<>();

    /**
     * 一次替换完整快照，避免原始输入与展示结果来自不同响应。
     *
     * @param newSnapshot 新建快照。
     */
    private void applySnapshot(WorldStateRuntimeSnapshot newSnapshot) {
        WorldStateDTO newState = Objects.requireNonNull(newSnapshot.state(), "newSnapshot.state must not be null");
        WorldStateRuntimeSnapshot oldSnapshot = currentSnapshot.getAndSet(newSnapshot);
        if (oldSnapshot == null)
            log.info("Initial world state loaded");
        eventPublisher
            .publishEvent(new WorldStateUpdatedEvent(oldSnapshot == null ? null : oldSnapshot.state(), newState, null));
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
     * @return 最近一次完整的本机快照，所有分区必须一起替换。
     */
    @Override
    public WorldStateRuntimeSnapshot getCurrentSnapshot() {
        return currentSnapshot.get();
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
            TaskProgressReporter progress = reporter == null ? TaskProgressReporter.noop() : reporter;
            progress.report("DOWNLOADING_WORLD_STATE", "正在拉取 Warframe 世界数据");
            HttpResult result = officialApiClient.fetchWorldStateResult();
            if (result == null || !result.isSuccess() || StrUtil.isBlank(result.body())) {
                // 保留最近一次成功的 JVM 内存快照，由本机下一个轮询周期重试。
                throw new SystemException(WarframeManagementErrorCode.WORLD_STATE_REFRESH_FAILED, "世界状态上游响应失败或为空");
            }
            progress.report("PARSING_WORLD_STATE", "正在解析 Warframe 世界数据");
            String source = "warframe-official";
            OfficialWorldStateRawData rawData;
            try {
                rawData = objectMapper.readValue(result.body(), OfficialWorldStateRawData.class);
            } catch (JsonProcessingException exception) {
                throw new SystemException(WarframeManagementErrorCode.WORLD_STATE_REFRESH_FAILED, "官方世界状态字段绑定失败",
                    exception);
            }
            WorldStateDTO state = cycleService.resolve(invasionEnrichmentService
                .enrich(bountyCycleService.enrich(worldStateMapper.map(rawData, source, result.body()))));
            applySnapshot(new WorldStateRuntimeSnapshot(rawData, state, source, state.metadata().contentDigest(),
                state.metadata().fetchedAt()));
            progress.complete("Warframe 世界数据已更新");
            return state;
        } finally {
            refreshLock.unlock();
        }
    }

}
