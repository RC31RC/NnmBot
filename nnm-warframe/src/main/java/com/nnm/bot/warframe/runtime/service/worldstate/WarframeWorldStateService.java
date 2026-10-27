package com.nnm.bot.warframe.runtime.service.worldstate;

import com.nnm.bot.common.task.TaskProgressReporter;
import com.nnm.bot.warframe.runtime.cache.worldstate.WorldStateRuntimeSnapshot;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;

/** Warframe 世界状态的读取、刷新和 JVM 内存快照能力。 */
public interface WarframeWorldStateService {

    /**
     * 读取当前已解析状态，并更新本机时钟驱动的周期。
     *
     * @return 已经解析并校验的世界状态快照。
     */
    WorldStateDTO getCurrentState();

    /**
     * 读取最近一次成功的原始输入、解析结果和采集元数据。
     *
     * @return 世界状态运行时快照对象。
     */
    WorldStateRuntimeSnapshot getCurrentSnapshot();

    /**
     * 接收已构造状态并发布本地应用更新事件。
     *
     * @param state 已经解析的当前世界状态。
     */
    void processNewState(WorldStateDTO state);

    /**
     * 使用无进度回调的方式刷新官方动态数据。
     *
     * @return 已经解析并校验的世界状态快照。
     */
    default WorldStateDTO refresh() {
        return refresh(TaskProgressReporter.noop());
    }

    /**
     * 下载并类型化绑定官方输入，补全成功后原子替换快照。
     *
     * @param reporter 向管理端任务提交进度的回调。
     * @return 已经解析并校验的世界状态快照。
     */
    WorldStateDTO refresh(TaskProgressReporter reporter);
}
