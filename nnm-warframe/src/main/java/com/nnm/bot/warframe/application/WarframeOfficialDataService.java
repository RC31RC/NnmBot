package com.nnm.bot.warframe.application;

import com.nnm.bot.warframe.pojo.WarframeOfficialRefreshResult;
import com.nnm.bot.common.task.TaskProgressReporter;

public interface WarframeOfficialDataService {

    default WarframeOfficialRefreshResult refresh() {
        return refresh(TaskProgressReporter.noop());
    }

    WarframeOfficialRefreshResult refresh(TaskProgressReporter reporter);
}
