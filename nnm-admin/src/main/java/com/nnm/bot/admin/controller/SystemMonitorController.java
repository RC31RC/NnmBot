package com.nnm.bot.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nnm.bot.admin.dto.system.OperationLogItem;
import com.nnm.bot.admin.dto.system.SessionItem;
import com.nnm.bot.admin.service.OnlineSessionService;
import com.nnm.bot.admin.service.OperationAuditService;
import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.admin.dto.system.OperationLogQueryRequest;
import com.nnm.bot.common.web.EmptyRequest;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/v1")
public class SystemMonitorController {

    @Resource
    private OperationAuditService auditService;
    @Resource
    private OnlineSessionService onlineSessionService;

    @PostMapping("/operation-logs/query")
    @SaCheckPermission("system:audit:view")
    public ApiResponse<PageResult<OperationLogItem>> operationLogs(
            @RequestBody
            OperationLogQueryRequest request
    ) {
        return ApiResponse.ok(auditService.logs(
                request.operatorName(),
                request.current() == null ? 1 : request.current(),
                request.size() == null ? 20 : request.size()
        ));
    }

    @PostMapping("/sessions/query")
    @SaCheckPermission("system:session:view")
    public ApiResponse<List<SessionItem>> sessions(
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(onlineSessionService.sessions());
    }

    @DeleteMapping("/sessions/{token}")
    @SaCheckPermission("system:session:kickout")
    public ApiResponse<Void> kickout(
            @PathVariable
            String token) {
        onlineSessionService.kickout(token);
        return ApiResponse.ok();
    }
}
