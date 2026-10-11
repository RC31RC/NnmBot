package com.nnm.bot.warframe.pojo;

import com.nnm.bot.warframe.constant.WarframeCommunitySourceStatus;

/** 来源处理摘要，不保存响应原文；操作索引用于跳转到该来源的首个差异页。 */
public record WarframeCommunitySourceSummary(
        String sourceCode,
        WarframeCommunitySourceStatus status,
        String endpoint,
        long elapsedMs,
        int recordCount,
        String errorMessage,
        int operationCount,
        Integer firstOperationIndex
) {
    public WarframeCommunitySourceSummary withOperations(int count, Integer firstIndex) {
        return new WarframeCommunitySourceSummary(
                sourceCode, status, endpoint, elapsedMs, recordCount, errorMessage,
                count, firstIndex);
    }
}
