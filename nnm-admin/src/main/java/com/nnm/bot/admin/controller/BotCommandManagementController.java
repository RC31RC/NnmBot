package com.nnm.bot.admin.controller;

import com.nnm.bot.admin.pojo.vo.bot.command.BotCommandVO;
import com.nnm.bot.admin.pojo.vo.bot.command.BotCommandRoleVO;
import com.nnm.bot.admin.pojo.vo.bot.command.BotCommandCostVO;
import com.nnm.bot.admin.controller.assembler.bot.command.AdminBotCommandViewAssembler;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandCostDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandCostSaveDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandRoleDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandRoleSaveDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandSaveDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandSystemRoleSaveDTO;
import com.nnm.bot.admin.service.bot.command.BotCommandManagementService;
import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.common.web.EmptyRequest;
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
    public ApiResponse<List<BotCommandVO>> commands(
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(AdminBotCommandViewAssembler.toBotCommandVO(service.commands()));
    }

    @PutMapping("/{code}")
    @SaCheckPermission("bot:command:update")
    public ApiResponse<Void> update(
            @PathVariable
            String code,
                                    @Valid
                                    @RequestBody
                                    BotCommandSaveDTO request) {
        service.update(code, request);
        return ApiResponse.ok();
    }

    @PostMapping("/{code}/robot-roles/query")
    @SaCheckPermission("bot:command:view")
    public ApiResponse<List<BotCommandRoleVO>> roles(
            @PathVariable
            String code,
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(AdminBotCommandViewAssembler.toBotCommandRoleVO(service.roles(code)));
    }

    @PutMapping("/{code}/robot-roles")
    @SaCheckPermission("bot:command:update")
    public ApiResponse<Void> updateRoles(
            @PathVariable
            String code,
            @Valid
            @RequestBody
            BotCommandRoleSaveDTO request) {
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
            BotCommandSystemRoleSaveDTO request) {
        service.updateSystemRoles(code, request);
        return ApiResponse.ok();
    }

    @PostMapping("/{code}/cost/query")
    @SaCheckPermission("bot:command:view")
    public ApiResponse<BotCommandCostVO> cost(
            @PathVariable
            String code,
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(AdminBotCommandViewAssembler.toBotCommandCostVO(service.cost(code)));
    }

    @PutMapping("/{code}/cost")
    @SaCheckPermission("bot:command:update")
    public ApiResponse<Void> updateCost(
            @PathVariable
            String code,
            @Valid
            @RequestBody
            BotCommandCostSaveDTO request) {
        service.updateCost(code, request);
        return ApiResponse.ok();
    }
}
