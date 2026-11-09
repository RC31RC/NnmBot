package com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot;

import java.time.Instant;

/** 刷新准入的轻量闭合投影，不读取 manifest、审批选择和执行计划。 */
public interface WarframeOfficialConflictBatchDTO {
    /** @return 批次标识。 */
    String getId();

    /** @return 单文件路径；全量为空。 */
    String getProductPath();

    /** @return 上游版本。 */
    String getSourceRef();

    /** @return 持久化业务状态。 */
    String getStatus();

    /** @return 创建时间。 */
    Instant getCreatedAt();

    /** @return 预览有效期。 */
    Instant getExpiresAt();

    /** @return 部分失败允许重试的截止时间。 */
    Instant getRetryExpiresAt();
}
