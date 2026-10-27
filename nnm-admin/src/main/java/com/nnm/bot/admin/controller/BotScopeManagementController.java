package com.nnm.bot.admin.controller;

import com.nnm.bot.admin.pojo.vo.bot.scope.BotScopeVO;
import com.nnm.bot.admin.pojo.vo.bot.scope.BotScopeMemberVO;
import com.nnm.bot.admin.pojo.vo.bot.scope.BotScopeDiscoveryVO;
import com.nnm.bot.admin.controller.assembler.bot.scope.AdminBotScopeViewAssembler;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeMemberDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeMemberUpdateDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeCreateDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeUpdateDTO;
import com.nnm.bot.admin.pojo.dto.bot.scope.BotScopeDiscoveryDTO;
import com.nnm.bot.admin.service.bot.scope.BotScopeManagementService;
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
@RequestMapping("/api/admin/v1/bot-scopes")
public class BotScopeManagementController {

    @Resource
    private BotScopeManagementService service;

    @PostMapping("/query")
    @SaCheckPermission("bot:group:view")
    public ApiResponse<List<BotScopeVO>> scopes(
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(AdminBotScopeViewAssembler.toBotScopeVO(service.scopes()));
    }

    @PostMapping("/discovery/query")
    @SaCheckPermission("bot:group:view")
    public ApiResponse<List<BotScopeDiscoveryVO>> discovery(
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(AdminBotScopeViewAssembler.toBotScopeDiscoveryVO(service.discover()));
    }

    @PostMapping
    @SaCheckPermission("bot:group:update")
    public ApiResponse<Long> create(
            @Valid
            @RequestBody
    BotScopeCreateDTO request) {
        return ApiResponse.ok(service.create(request));
    }

    @PutMapping("/{id}")
    @SaCheckPermission("bot:group:update")
    public ApiResponse<Void> update(
            @PathVariable
            long id,
            @Valid
            @RequestBody
            BotScopeUpdateDTO request) {
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

    @PostMapping("/{id}/members/query")
    @SaCheckPermission("bot:group:view")
    public ApiResponse<List<BotScopeMemberVO>> members(
            @PathVariable
            long id,
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(AdminBotScopeViewAssembler.toBotScopeMemberVO(service.members(id)));
    }

    @PutMapping("/{scopeId}/members/{memberId}")
    @SaCheckPermission("bot:group:update")
    public ApiResponse<Void> updateMember(
            @PathVariable
            long scopeId,
            @PathVariable
            long memberId,
            @Valid
            @RequestBody
            BotScopeMemberUpdateDTO request) {
        service.updateMember(scopeId, memberId, request);
        return ApiResponse.ok();
    }
}
