package com.nnm.bot.warframe.application;

import com.nnm.bot.warframe.dto.WarframeCommunityDatasetInfo;
import com.nnm.bot.warframe.dto.WarframeCommunityDatasetItem;
import com.nnm.bot.warframe.dto.WarframeCommunityDatasetMergeRequest;
import com.nnm.bot.warframe.dto.WarframeCommunityDatasetPreview;
import com.nnm.bot.warframe.dto.WarframeCommunityDatasetQueryRequest;
import com.nnm.bot.warframe.dto.WarframeCommunityDatasetBatchRefreshResult;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.common.task.TaskProgressReporter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WarframeCommunityDatasetService {

    List<WarframeCommunityDatasetInfo> datasets();

    PageResult<WarframeCommunityDatasetItem> query(
            String dataset, WarframeCommunityDatasetQueryRequest request);

    WarframeCommunityDatasetPreview refresh(String dataset);

    WarframeCommunityDatasetPreview refresh(
            String dataset, TaskProgressReporter reporter);

    WarframeCommunityDatasetBatchRefreshResult refreshAll(
            TaskProgressReporter reporter);

    WarframeCommunityDatasetItem createSelfMaintained(
            String dataset, String currentJson);

    WarframeCommunityDatasetPreview uploadPreview(
            String dataset, MultipartFile file);

    WarframeCommunityDatasetPreview merge(
            String dataset, WarframeCommunityDatasetMergeRequest request);

    WarframeCommunityDatasetItem updateManual(
            String dataset, long id, String currentJson);
}
