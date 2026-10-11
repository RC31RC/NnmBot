package com.nnm.bot.warframe.application;

import com.nnm.bot.common.task.TaskProgressReporter;
import com.nnm.bot.warframe.pojo.WarframeArbitrationMergeRequest;
import com.nnm.bot.warframe.pojo.WarframeArbitrationPreview;

public interface WarframeArbitrationAdminService {
    WarframeArbitrationPreview refreshPreview(TaskProgressReporter reporter);

    WarframeArbitrationPreview preview(String previewId);

    WarframeArbitrationPreview merge(WarframeArbitrationMergeRequest request);
}
