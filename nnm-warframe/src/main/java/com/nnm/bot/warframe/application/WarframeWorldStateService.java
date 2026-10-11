package com.nnm.bot.warframe.application;

import com.nnm.bot.common.task.TaskProgressReporter;
import com.nnm.bot.warframe.model.WorldState;

/** Warframe 世界状态的读取、刷新和 JVM 内存快照能力。 */
public interface WarframeWorldStateService {

    WorldState getCurrentState();

    WorldStateRuntimeSnapshot getCurrentSnapshot();

    void processNewState(WorldState state);

    default WorldState refresh() {
        return refresh(TaskProgressReporter.noop());
    }

    WorldState refresh(TaskProgressReporter reporter);
}
