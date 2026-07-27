package com.nnm.bot.admin.service;

import com.nnm.bot.admin.dto.system.OperationLogItem;
import com.nnm.bot.common.response.PageResult;

public interface OperationAuditService {

    void record(String action, String targetType, Object targetId, String detail);

    PageResult<OperationLogItem> logs(String operatorName, int current, int size);
}
