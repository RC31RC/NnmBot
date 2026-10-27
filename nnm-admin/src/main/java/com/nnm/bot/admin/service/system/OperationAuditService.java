package com.nnm.bot.admin.service.system;

import com.nnm.bot.admin.pojo.dto.system.OperationLogDTO;
import com.nnm.bot.common.response.PageResult;

public interface OperationAuditService {

    void record(String action, String targetType, Object targetId, String detail);

    void recordForOperator(long operatorId, String action, String targetType, Object targetId, String detail);

    PageResult<OperationLogDTO> logs(String operatorName, int current, int size);
}
