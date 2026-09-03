package com.nnm.bot.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nnm.bot.admin.dto.bot.BotScopeItem;
import com.nnm.bot.admin.dto.bot.BotScopeMemberItem;
import com.nnm.bot.admin.dto.bot.BotScopeMemberUpdateRequest;
import com.nnm.bot.admin.dto.bot.BotScopeCreateRequest;
import com.nnm.bot.admin.dto.bot.BotScopeUpdateRequest;
import com.nnm.bot.admin.dto.bot.BotScopeDiscoveryItem;
import com.nnm.bot.admin.service.BotScopeManagementService;
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
    public ApiResponse<List<BotScopeItem>> scopes(
            @RequestBody(required = false) EmptyRequest ignored) {
        return ApiResponse.ok(service.scopes());
    }

    @PostMapping("/discovery/query")
    @SaCheckPermission("bot:group:view")
    public ApiResponse<List<BotScopeDiscoveryItem>> discovery(
            @RequestBody(required = false) EmptyRequest ignored) {
        return ApiResponse.ok(service.discover());
    }

    @PostMapping
    @SaCheckPermission("bot:group:update")
    public ApiResponse<Long> create(@Valid @RequestBody BotScopeCreateRequest request) {
        return ApiResponse.ok(service.create(request));
    }

    @PutMapping("/{id}")
    @SaCheckPermission("bot:group:update")
    public ApiResponse<Void> update(
            @PathVariable long id,
            @Valid @RequestBody BotScopeUpdateRequest request) {
        service.update(id, request);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("bot:group:delete")
    public ApiResponse<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/members/query")
    @SaCheckPermission("bot:group:view")
    public ApiResponse<List<BotScopeMemberItem>> members(
            @PathVariable long id,
            @RequestBody(required = false) EmptyRequest ignored) {
        return ApiResponse.ok(service.members(id));
    }

    @PutMapping("/{scopeId}/members/{memberId}")
    @SaCheckPermission("bot:group:update")
    public ApiResponse<Void> updateMember(
            @PathVariable long scopeId,
            @PathVariable long memberId,
            @Valid @RequestBody BotScopeMemberUpdateRequest request) {
        service.updateMember(scopeId, memberId, request);
        return ApiResponse.ok();
    }
}
