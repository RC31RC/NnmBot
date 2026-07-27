package com.nnm.bot.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nnm.bot.admin.dto.system.OperationLogItem;
import com.nnm.bot.admin.dto.system.SessionItem;
import com.nnm.bot.admin.service.OnlineSessionService;
import com.nnm.bot.admin.service.OperationAuditService;
import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.common.response.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/v1")
@RequiredArgsConstructor
public class SystemMonitorController {

    private final OperationAuditService auditService;
    private final OnlineSessionService onlineSessionService;

    @GetMapping("/operation-logs")
    @SaCheckPermission("system:audit:view")
    public ApiResponse<PageResult<OperationLogItem>> operationLogs(
            @RequestParam(required = false) String operatorName,
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.ok(auditService.logs(operatorName, current, size));
    }

    @GetMapping("/sessions")
    @SaCheckPermission("system:session:view")
    public ApiResponse<List<SessionItem>> sessions() {
        return ApiResponse.ok(onlineSessionService.sessions());
    }

    @DeleteMapping("/sessions/{token}")
    @SaCheckPermission("system:session:kickout")
    public ApiResponse<Void> kickout(@PathVariable String token) {
        onlineSessionService.kickout(token);
        return ApiResponse.ok();
    }
}
