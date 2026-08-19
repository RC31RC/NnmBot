package com.nnm.bot.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.nnm.bot.admin.dto.auth.UserInfoResponse;
import com.nnm.bot.admin.dto.system.MenuItem;
import com.nnm.bot.admin.dto.system.PermissionItem;
import com.nnm.bot.admin.dto.system.RoleListItem;
import com.nnm.bot.admin.dto.system.UserListItem;
import com.nnm.bot.admin.service.AuthService;
import com.nnm.bot.admin.service.RbacQueryService;
import com.nnm.bot.common.response.ApiResponse;
import com.nnm.bot.common.response.PageResult;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/v1")
public class RbacController {

    @Resource
    private AuthService authService;
    @Resource
    private RbacQueryService rbacQueryService;

    @GetMapping("/me")
    public ApiResponse<UserInfoResponse> me() {
        return ApiResponse.ok(authService.currentUser(StpUtil.getLoginIdAsLong()));
    }

    @GetMapping("/users")
    @SaCheckPermission("system:user:view")
    public ApiResponse<PageResult<UserListItem>> users(
            @RequestParam(required = false) String userName,
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.ok(rbacQueryService.users(StpUtil.getLoginIdAsLong(), userName, current, size));
    }

    @GetMapping("/roles")
    @SaCheckPermission("system:role:view")
    public ApiResponse<PageResult<RoleListItem>> roles(
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) String roleCode,
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size
    ) {
        String keyword = roleName == null || roleName.isBlank() ? roleCode : roleName;
        return ApiResponse.ok(rbacQueryService.roles(keyword, current, size));
    }

    @GetMapping("/permissions")
    @SaCheckPermission("system:role:assign")
    public ApiResponse<List<PermissionItem>> permissions() {
        return ApiResponse.ok(rbacQueryService.permissions());
    }

    @GetMapping("/menus")
    public ApiResponse<List<MenuItem>> menus() {
        return ApiResponse.ok(rbacQueryService.menus(StpUtil.getLoginIdAsLong()));
    }

    @GetMapping("/menus/all")
    @SaCheckPermission("system:menu:view")
    public ApiResponse<List<MenuItem>> allMenus() {
        return ApiResponse.ok(rbacQueryService.allMenus());
    }
}
