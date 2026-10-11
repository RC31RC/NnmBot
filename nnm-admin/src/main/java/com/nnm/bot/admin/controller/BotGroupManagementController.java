package com.nnm.bot.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nnm.bot.admin.dto.bot.BotGroupBotSaveRequest;
import com.nnm.bot.admin.dto.bot.BotGroupCreateRequest;
import com.nnm.bot.admin.dto.bot.BotGroupItem;
import com.nnm.bot.admin.dto.bot.BotGroupMemberItem;
import com.nnm.bot.admin.dto.bot.BotGroupMemberPointsUpdateRequest;
import com.nnm.bot.admin.dto.bot.BotGroupMemberUpdateRequest;
import com.nnm.bot.admin.dto.bot.BotGroupUpdateRequest;
import com.nnm.bot.admin.dto.bot.BotScopeDiscoveryItem;
import com.nnm.bot.admin.service.BotGroupManagementService;
import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.common.task.AdminTaskService;
import com.nnm.bot.common.task.AdminTaskSubmission;
import com.nnm.bot.common.web.EmptyRequest;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/v1/bot-groups")
public class BotGroupManagementController {

    @Resource
    private BotGroupManagementService service;
    @Resource
    private AdminTaskService taskService;

    @PostMapping("/query")
    @SaCheckPermission("bot:group:view")
    public ApiResponse<List<BotGroupItem>> list(
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(service.list());
    }

    @PostMapping("/discovery/query")
    @SaCheckPermission("bot:group:view")
    public ApiResponse<List<BotScopeDiscoveryItem>> discovery(
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(service.discover());
    }

    @PostMapping("/discovery")
    @SaCheckPermission("bot:group:update")
    public ApiResponse<AdminTaskSubmission> discoverAndSynchronize() {
        return ApiResponse.ok(submitTask(
                "BOT_GROUP_DISCOVERY",
                "bot:group:update",
                reporter -> service.discoverAndSynchronize(reporter)));
    }

    @PostMapping("/{id}/members/refresh")
    @SaCheckPermission("bot:group:update")
    public ApiResponse<AdminTaskSubmission> refreshMembers(
            @PathVariable
            long id) {
        return ApiResponse.ok(submitTask(
                "BOT_GROUP_MEMBER_REFRESH:" + id,
                "bot:group:update",
                reporter -> {
                    service.refreshMembers(id, reporter);
                    return service.members(id);
                }));
    }

    private AdminTaskSubmission submitTask(
            String taskType,
            String permission,
            AdminTaskService.AdminTaskWork work) {
        return taskService.findActive(taskType)
                .map(existing -> new AdminTaskSubmission(existing, taskType))
                .orElseGet(() -> taskService.submit(taskType, permission, work));
    }

    @PostMapping
    @SaCheckPermission("bot:group:update")
    public ApiResponse<Long> create(
            @Valid
            @RequestBody
    BotGroupCreateRequest request) {
        return ApiResponse.ok(service.create(request));
    }

    @PutMapping("/{id}")
    @SaCheckPermission("bot:group:update")
    public ApiResponse<Void> update(
            @PathVariable
            long id,
            @Valid
            @RequestBody
            BotGroupUpdateRequest request) {
        service.update(id, request);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("bot:group:delete")
    public ApiResponse<Void> delete(
            @PathVariable
            long id) {
        service.delete(id);
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/bots")
    @SaCheckPermission("bot:group:update")
    public ApiResponse<Void> bindBot(
            @PathVariable
            long id,
            @Valid
            @RequestBody
            BotGroupBotSaveRequest request) {
        service.bindBot(id, request);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}/bots/{bindingId}")
    @SaCheckPermission("bot:group:update")
    public ApiResponse<Void> unbindBot(
            @PathVariable
            long id,
            @PathVariable
            long bindingId) {
        service.unbindBot(id, bindingId);
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/members/query")
    @SaCheckPermission("bot:group:view")
    public ApiResponse<List<BotGroupMemberItem>> members(
            @PathVariable
            long id,
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(service.members(id));
    }

    @PutMapping("/{groupId}/members/{memberId}")
    @SaCheckPermission("bot:group:update")
    public ApiResponse<Void> updateMember(
            @PathVariable
            long groupId,
            @PathVariable
            long memberId,
            @Valid
            @RequestBody
            BotGroupMemberUpdateRequest request) {
        service.updateMember(groupId, memberId, request);
        return ApiResponse.ok();
    }

    @PutMapping("/{groupId}/members/{memberId}/points")
    @SaCheckPermission("bot:group:update")
    public ApiResponse<Void> updateMemberPoints(
            @PathVariable
            long groupId,
            @PathVariable
            long memberId,
            @Valid
            @RequestBody
            BotGroupMemberPointsUpdateRequest request) {
        service.updateMemberPoints(groupId, memberId, request);
        return ApiResponse.ok();
    }
}
