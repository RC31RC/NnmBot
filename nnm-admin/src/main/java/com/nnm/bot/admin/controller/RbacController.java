package com.nnm.bot.admin.controller;

import cn.hutool.core.util.StrUtil;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.nnm.bot.admin.dto.auth.UserInfoResponse;
import com.nnm.bot.admin.dto.system.MenuItem;
import com.nnm.bot.admin.dto.system.PermissionItem;
import com.nnm.bot.admin.dto.system.RoleQueryRequest;
import com.nnm.bot.admin.dto.system.RoleListItem;
import com.nnm.bot.admin.dto.system.UserQueryRequest;
import com.nnm.bot.admin.dto.system.UserListItem;
import com.nnm.bot.admin.service.AuthService;
import com.nnm.bot.admin.service.RbacQueryService;
import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.common.web.EmptyRequest;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/v1")
public class RbacController {

    @Resource
    private AuthService authService;
    @Resource
    private RbacQueryService rbacQueryService;

    @PostMapping("/me")
    public ApiResponse<UserInfoResponse> me(
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(authService.currentUser(StpUtil.getLoginIdAsLong()));
    }

    @PostMapping("/users/query")
    @SaCheckPermission("system:user:view")
    public ApiResponse<PageResult<UserListItem>> users(
            @RequestBody
            UserQueryRequest request
    ) {
        return ApiResponse.ok(rbacQueryService.users(
                StpUtil.getLoginIdAsLong(),
                request.userName(),
                request.current() == null ? 1 : request.current(),
                request.size() == null ? 20 : request.size()
        ));
    }

    @PostMapping("/roles/query")
    @SaCheckPermission("system:role:view")
    public ApiResponse<PageResult<RoleListItem>> roles(
            @RequestBody
            RoleQueryRequest request
    ) {
        String keyword = StrUtil.isBlank(request.roleName()) ? request.roleCode() : request.roleName();
        return ApiResponse.ok(rbacQueryService.roles(
                StpUtil.getLoginIdAsLong(),
                keyword,
                request.current() == null ? 1 : request.current(),
                request.size() == null ? 20 : request.size()
        ));
    }

    @PostMapping("/permissions/query")
    @SaCheckPermission("system:role:assign")
    public ApiResponse<List<PermissionItem>> permissions(
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(rbacQueryService.permissions());
    }

    @PostMapping("/menus/query")
    public ApiResponse<List<MenuItem>> menus(
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(rbacQueryService.menus(StpUtil.getLoginIdAsLong()));
    }

    @PostMapping("/menus/all/query")
    @SaCheckPermission("system:menu:view")
    public ApiResponse<List<MenuItem>> allMenus(
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(rbacQueryService.allMenus());
    }
}
