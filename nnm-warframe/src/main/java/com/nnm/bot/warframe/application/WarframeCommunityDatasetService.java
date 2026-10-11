package com.nnm.bot.warframe.application;

import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetInfo;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetItem;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetMergeRequest;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetPreview;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetPreviewPage;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetQueryRequest;
import com.nnm.bot.warframe.pojo.WarframeCommunityDatasetBatchRefreshResult;
import com.nnm.bot.warframe.constant.WarframeCommunityRecordChangeType;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.common.task.TaskProgressReporter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WarframeCommunityDatasetService {

    List<WarframeCommunityDatasetInfo> datasets();

    PageResult<WarframeCommunityDatasetItem> query(String dataset, WarframeCommunityDatasetQueryRequest request);

    WarframeCommunityDatasetPreview refresh(String dataset);

    WarframeCommunityDatasetPreview refresh(String dataset, TaskProgressReporter reporter);

    WarframeCommunityDatasetBatchRefreshResult refreshAll(TaskProgressReporter reporter);

    WarframeCommunityDatasetItem createSelfMaintained(String dataset, String currentJson);

    WarframeCommunityDatasetPreview uploadPreview(String dataset, MultipartFile file);

    WarframeCommunityDatasetPreview uploadPreview(
            String dataset, String sourceCode, MultipartFile file);

    WarframeCommunityDatasetPreview preview(String dataset, String previewId);

    default WarframeCommunityDatasetPreviewPage previewPage(
            String dataset, String previewId, int current, int size) {
        return previewPage(dataset, previewId, current, size, null, null);
    }

    WarframeCommunityDatasetPreviewPage previewPage(
            String dataset, String previewId, int current, int size,
            String sourceCode, WarframeCommunityRecordChangeType changeType);

    WarframeCommunityDatasetPreview merge(String dataset, WarframeCommunityDatasetMergeRequest request);

    WarframeCommunityDatasetItem updateManual(String dataset, long id, String currentJson);

    void deleteManual(String dataset, long id);
}
