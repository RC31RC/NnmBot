package com.nnm.bot.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nnm.bot.admin.dto.bot.BotInstanceRobotRoleSaveRequest;
import com.nnm.bot.admin.service.BotRobotRoleManagementService;
import com.nnm.bot.common.response.ApiResponse;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 以机器人实例为主体维护能力角色绑定。 */
@RestController
@RequestMapping("/api/admin/v1/bot-instances")
public class BotInstanceRobotRoleController {

    @Resource
    private BotRobotRoleManagementService service;

    @PutMapping("/{id}/robot-roles")
    @SaCheckPermission("bot:command:update")
    public ApiResponse<Void> assignRobotRoles(
            @PathVariable
            long id,
            @Valid
            @RequestBody
            BotInstanceRobotRoleSaveRequest request) {
        service.assignInstanceRoles(id, request.roleIds());
        return ApiResponse.ok();
    }
}
