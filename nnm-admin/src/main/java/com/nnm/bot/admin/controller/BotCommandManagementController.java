package com.nnm.bot.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nnm.bot.admin.dto.bot.BotCommandItem;
import com.nnm.bot.admin.dto.bot.BotCommandChannelOutputItem;
import com.nnm.bot.admin.dto.bot.BotCommandChannelOutputSaveRequest;
import com.nnm.bot.admin.dto.bot.BotCommandCostItem;
import com.nnm.bot.admin.dto.bot.BotCommandCostSaveRequest;
import com.nnm.bot.admin.dto.bot.BotCommandRoleItem;
import com.nnm.bot.admin.dto.bot.BotCommandRoleSaveRequest;
import com.nnm.bot.admin.dto.bot.BotCommandSaveRequest;
import com.nnm.bot.admin.dto.bot.BotCommandSystemRoleSaveRequest;
import com.nnm.bot.admin.service.BotCommandManagementService;
import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.common.web.EmptyRequest;
import com.nnm.bot.core.constant.BotPlatform;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/v1/bot-commands")
public class BotCommandManagementController {

    @Resource
    private BotCommandManagementService service;

    @PostMapping("/query")
    @SaCheckPermission("bot:command:view")
    public ApiResponse<List<BotCommandItem>> commands(
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(service.commands());
    }

    @PutMapping("/{code}")
    @SaCheckPermission("bot:command:update")
    public ApiResponse<Void> update(
            @PathVariable
            String code,
                                    @Valid
                                    @RequestBody
                                    BotCommandSaveRequest request) {
        service.update(code, request);
        return ApiResponse.ok();
    }

    @PostMapping("/{code}/outputs/query")
    @SaCheckPermission("bot:command:view")
    public ApiResponse<List<BotCommandChannelOutputItem>> channelOutputs(
            @PathVariable
            String code,
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(service.channelOutputs(code));
    }

    @PutMapping("/{code}/outputs/{platform}")
    @SaCheckPermission("bot:command:update")
    public ApiResponse<Void> updateChannelOutput(
            @PathVariable
            String code,
            @PathVariable
            BotPlatform platform,
            @Valid
            @RequestBody
            BotCommandChannelOutputSaveRequest request) {
        service.updateChannelOutput(code, platform, request);
        return ApiResponse.ok();
    }

    @PostMapping("/{code}/robot-roles/query")
    @SaCheckPermission("bot:command:view")
    public ApiResponse<List<BotCommandRoleItem>> roles(
            @PathVariable
            String code,
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(service.roles(code));
    }

    @PutMapping("/{code}/robot-roles")
    @SaCheckPermission("bot:command:update")
    public ApiResponse<Void> updateRoles(
            @PathVariable
            String code,
            @Valid
            @RequestBody
            BotCommandRoleSaveRequest request) {
        service.updateRoles(code, request);
        return ApiResponse.ok();
    }

    @PostMapping("/{code}/system-role-ids/query")
    @SaCheckPermission("bot:command:view")
    public ApiResponse<List<Long>> systemRoleIds(
            @PathVariable
            String code,
            @RequestBody(required = false)
            EmptyRequest ignored) {
        // 角色详情复用系统管理的 /roles；这里仅返回命令已绑定的主键。
        return ApiResponse.ok(service.systemRoleIds(code));
    }

    @PutMapping("/{code}/system-roles")
    @SaCheckPermission("bot:command:update")
    public ApiResponse<Void> updateSystemRoles(
            @PathVariable
            String code,
            @Valid
            @RequestBody
            BotCommandSystemRoleSaveRequest request) {
        service.updateSystemRoles(code, request);
        return ApiResponse.ok();
    }

    @PostMapping("/{code}/cost/query")
    @SaCheckPermission("bot:command:view")
    public ApiResponse<BotCommandCostItem> cost(
            @PathVariable
            String code,
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(service.cost(code));
    }

    @PutMapping("/{code}/cost")
    @SaCheckPermission("bot:command:update")
    public ApiResponse<Void> updateCost(
            @PathVariable
            String code,
            @Valid
            @RequestBody
            BotCommandCostSaveRequest request) {
        service.updateCost(code, request);
        return ApiResponse.ok();
    }
}
