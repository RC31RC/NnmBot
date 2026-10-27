package com.nnm.bot.admin.controller;

import com.nnm.bot.admin.pojo.vo.bot.role.BotRobotRoleVO;
import com.nnm.bot.admin.controller.assembler.bot.role.AdminBotRoleViewAssembler;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nnm.bot.admin.pojo.dto.bot.role.BotRobotRoleDTO;
import com.nnm.bot.admin.pojo.dto.bot.role.BotRobotRoleSaveDTO;
import com.nnm.bot.admin.service.bot.role.BotRobotRoleManagementService;
import com.nnm.bot.common.response.ApiResponse;
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
@RequestMapping("/api/admin/v1/bot-robot-roles")
public class BotRobotRoleManagementController {

    @Resource
    private BotRobotRoleManagementService service;

    @PostMapping("/query")
    @SaCheckPermission("bot:command:view")
    public ApiResponse<List<BotRobotRoleVO>> list(
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(AdminBotRoleViewAssembler.toBotRobotRoleVO(service.list()));
    }

    @PostMapping
    @SaCheckPermission("bot:command:update")
    public ApiResponse<Long> create(
            @Valid
            @RequestBody
    BotRobotRoleSaveDTO request) {
        return ApiResponse.ok(service.create(request));
    }

    @PutMapping("/{id}")
    @SaCheckPermission("bot:command:update")
    public ApiResponse<Void> update(
            @PathVariable
            long id,
            @Valid
            @RequestBody
            BotRobotRoleSaveDTO request) {
        service.update(id, request);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("bot:command:update")
    public ApiResponse<Void> delete(
            @PathVariable
            long id) {
        service.delete(id);
        return ApiResponse.ok();
    }
}
