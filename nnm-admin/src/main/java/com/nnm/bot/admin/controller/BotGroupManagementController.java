package com.nnm.bot.admin.controller;

import com.nnm.bot.admin.pojo.vo.bot.scope.BotScopeDiscoveryVO;
import com.nnm.bot.admin.pojo.vo.bot.notification.BotNotificationPreviewVO;
import com.nnm.bot.admin.pojo.vo.bot.notification.BotNotificationJobVO;
import com.nnm.bot.admin.pojo.vo.bot.group.BotGroupVO;
import com.nnm.bot.admin.pojo.vo.bot.group.BotGroupMemberVO;
import com.nnm.bot.admin.pojo.vo.bot.group.BotGroupBindingPreviewVO;
import com.nnm.bot.admin.controller.assembler.bot.scope.AdminBotScopeViewAssembler;
import com.nnm.bot.admin.controller.assembler.bot.notification.AdminBotNotificationViewAssembler;
import com.nnm.bot.admin.controller.assembler.bot.group.AdminBotGroupViewAssembler;
import com.nnm.bot.common.task.AdminTaskService.AdminTaskWork;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.nnm.bot.admin.pojo.dto.bot.notification.BotNotificationPreviewDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupBotSaveDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupBindingPreviewDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupBotUpdateDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupCreateDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupDTO;
import com.nnm.bot.admin.pojo.dto.bot.notification.BotGroupNotificationDTO;
import com.nnm.bot.admin.service.bot.notification.BotGroupNotificationService;
import com.nnm.bot.admin.service.bot.notification.BotNotificationJobService;
import com.nnm.bot.admin.pojo.dto.bot.notification.BotNotificationJobDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupMemberDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupMemberPointsUpdateDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupMemberUpdateDTO;
import com.nnm.bot.admin.pojo.dto.bot.group.BotGroupUpdateDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeDiscoveryDTO;
import com.nnm.bot.admin.service.bot.group.BotGroupManagementService;
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
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/admin/v1/bot-groups")
public class BotGroupManagementController {

    @Resource
    private BotGroupManagementService service;
    @Resource
    private AdminTaskService taskService;
    @Resource
    private BotGroupNotificationService notifications;
    @Resource
    private BotNotificationJobService notificationJobs;

    @PostMapping("/query")
    @SaCheckPermission("bot:group:view")
    public ApiResponse<List<BotGroupVO>> list(
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(AdminBotGroupViewAssembler.toBotGroupVO(service.list()));
    }

    @PostMapping("/discovery/query")
    @SaCheckPermission("bot:group:view")
    public ApiResponse<List<BotScopeDiscoveryVO>> discovery(
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(AdminBotScopeViewAssembler.toBotScopeDiscoveryVO(service.discover()));
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
            AdminTaskWork work) {
        return taskService.findActive(taskType)
                .map(existing -> new AdminTaskSubmission(existing, taskType))
                .orElseGet(() -> taskService.submit(taskType, permission, work));
    }

    @PostMapping("/notifications")
    @SaCheckPermission("bot:notification:send")
    public ApiResponse<BotNotificationJobVO> sendNotification(
            @Valid
            @RequestBody
            BotGroupNotificationDTO request) {
        return ApiResponse.ok(AdminBotNotificationViewAssembler.toBotNotificationJobVO(notificationJobs.submit(StpUtil.getLoginIdAsLong(), request)));
    }

    @GetMapping("/notifications")
    @SaCheckPermission("bot:notification:send")
    public ApiResponse<List<BotNotificationJobVO>> notificationJobs() {
        return ApiResponse.ok(AdminBotNotificationViewAssembler.toBotNotificationJobVO(notificationJobs.list(StpUtil.getLoginIdAsLong())));
    }

    @PostMapping("/notifications/{id}/cancel")
    @SaCheckPermission("bot:notification:send")
    public ApiResponse<BotNotificationJobVO> cancelNotification(
            @PathVariable
            UUID id) {
        return ApiResponse.ok(AdminBotNotificationViewAssembler.toBotNotificationJobVO(notificationJobs.cancel(StpUtil.getLoginIdAsLong(), id)));
    }

    @PostMapping("/notifications/preview")
    @SaCheckPermission("bot:notification:preview")
    public ApiResponse<List<BotNotificationPreviewVO>> previewNotification(
            @Valid
            @RequestBody
            BotGroupNotificationDTO request) {
        return ApiResponse.ok(AdminBotNotificationViewAssembler.toBotNotificationPreviewVO(notifications.preview(StpUtil.getLoginIdAsLong(), request)));
    }

    @PostMapping
    @SaCheckPermission("bot:group:update")
    public ApiResponse<Long> create(
            @Valid
            @RequestBody
    BotGroupCreateDTO request) {
        return ApiResponse.ok(service.create(request));
    }

    @PostMapping("/{id}/preview")
    @SaCheckPermission("bot:group:update")
    public ApiResponse<BotGroupBindingPreviewVO> previewGroupUpdate(
            @PathVariable
            long id,
            @Valid
            @RequestBody
            BotGroupUpdateDTO request) {
        return ApiResponse.ok(AdminBotGroupViewAssembler.toBotGroupBindingPreviewVO(service.previewGroupUpdate(id, request)));
    }

    @PutMapping("/{id}")
    @SaCheckPermission("bot:group:update")
    public ApiResponse<Void> update(
            @PathVariable
            long id,
            @Valid
            @RequestBody
            BotGroupUpdateDTO request) {
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

    @PostMapping("/{id}/bots/preview")
    @SaCheckPermission("bot:group:update")
    public ApiResponse<BotGroupBindingPreviewVO> previewBinding(
            @PathVariable
            long id,
            @Valid
            @RequestBody
            BotGroupBotSaveDTO request) {
        return ApiResponse.ok(AdminBotGroupViewAssembler.toBotGroupBindingPreviewVO(service.previewBinding(id, request)));
    }

    @PostMapping("/{id}/bots/{bindingId}/preview")
    @SaCheckPermission("bot:group:update")
    public ApiResponse<BotGroupBindingPreviewVO> previewBindingUpdate(
            @PathVariable
            long id,
            @PathVariable
            long bindingId,
            @Valid
            @RequestBody
            BotGroupBotUpdateDTO request) {
        return ApiResponse.ok(AdminBotGroupViewAssembler.toBotGroupBindingPreviewVO(service.previewBindingUpdate(id, bindingId, request)));
    }

    @PostMapping("/{id}/bots")
    @SaCheckPermission("bot:group:update")
    public ApiResponse<Void> bindBot(
            @PathVariable
            long id,
            @Valid
            @RequestBody
            BotGroupBotSaveDTO request) {
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

    @PutMapping("/{id}/bots/{bindingId}")
    @SaCheckPermission("bot:group:update")
    public ApiResponse<Void> updateBot(
            @PathVariable
            long id,
            @PathVariable
            long bindingId,
            @Valid
            @RequestBody
            BotGroupBotUpdateDTO request) {
        service.updateBot(id, bindingId, request);
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/members/query")
    @SaCheckPermission("bot:group:view")
    public ApiResponse<List<BotGroupMemberVO>> members(
            @PathVariable
            long id,
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(AdminBotGroupViewAssembler.toBotGroupMemberVO(service.members(id)));
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
            BotGroupMemberUpdateDTO request) {
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
            BotGroupMemberPointsUpdateDTO request) {
        service.updateMemberPoints(groupId, memberId, request);
        return ApiResponse.ok();
    }
}
