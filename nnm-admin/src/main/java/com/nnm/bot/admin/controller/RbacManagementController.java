package com.nnm.bot.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.nnm.bot.admin.dto.system.MenuSaveRequest;
import com.nnm.bot.admin.dto.system.PermissionSaveRequest;
import com.nnm.bot.admin.dto.system.RoleGrantRequest;
import com.nnm.bot.admin.dto.system.RoleGrantResponse;
import com.nnm.bot.admin.dto.system.RoleSaveRequest;
import com.nnm.bot.admin.dto.system.UserRoleRequest;
import com.nnm.bot.admin.dto.system.UserSaveRequest;
import com.nnm.bot.admin.dto.system.UserStatusRequest;
import com.nnm.bot.admin.service.RbacManagementService;
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

@RestController
@RequestMapping("/api/admin/v1")
public class RbacManagementController {

    @Resource
    private RbacManagementService managementService;

    @PostMapping("/users")
    @SaCheckPermission("system:user:create")
    public ApiResponse<Long> createUser(
            @Valid
            @RequestBody
    UserSaveRequest request) {
        return ApiResponse.ok(managementService.createUser(StpUtil.getLoginIdAsLong(), request));
    }

    @PutMapping("/users/{userId}")
    @SaCheckPermission("system:user:update")
    public ApiResponse<Void> updateUser(
            @PathVariable
            long userId,
            @Valid
            @RequestBody
            UserSaveRequest request
    ) {
        managementService.updateUser(StpUtil.getLoginIdAsLong(), userId, request);
        return ApiResponse.ok();
    }

    @PutMapping("/users/{userId}/status")
    @SaCheckPermission("system:user:disable")
    public ApiResponse<Void> updateUserStatus(
            @PathVariable
            long userId,
            @Valid
            @RequestBody
            UserStatusRequest request
    ) {
        managementService.updateUserStatus(StpUtil.getLoginIdAsLong(), userId, request);
        return ApiResponse.ok();
    }

    @PutMapping("/users/{userId}/roles")
    @SaCheckPermission("system:user:update")
    public ApiResponse<Void> assignUserRoles(
            @PathVariable
            long userId,
            @Valid
            @RequestBody
            UserRoleRequest request
    ) {
        managementService.assignUserRoles(StpUtil.getLoginIdAsLong(), userId, request);
        return ApiResponse.ok();
    }

    @PostMapping("/roles")
    @SaCheckPermission("system:role:create")
    public ApiResponse<Long> createRole(
            @Valid
            @RequestBody
    RoleSaveRequest request) {
        return ApiResponse.ok(managementService.createRole(StpUtil.getLoginIdAsLong(), request));
    }

    @PutMapping("/roles/{roleId}")
    @SaCheckPermission("system:role:update")
    public ApiResponse<Void> updateRole(
            @PathVariable
            long roleId,
            @Valid
            @RequestBody
            RoleSaveRequest request
    ) {
        managementService.updateRole(StpUtil.getLoginIdAsLong(), roleId, request);
        return ApiResponse.ok();
    }

    @DeleteMapping("/roles/{roleId}")
    @SaCheckPermission("system:role:delete")
    public ApiResponse<Void> deleteRole(
            @PathVariable
            long roleId) {
        managementService.deleteRole(StpUtil.getLoginIdAsLong(), roleId);
        return ApiResponse.ok();
    }

    @PostMapping("/roles/{roleId}/grants/query")
    @SaCheckPermission("system:role:assign")
    public ApiResponse<RoleGrantResponse> roleGrants(
            @PathVariable
            long roleId,
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(managementService.roleGrants(StpUtil.getLoginIdAsLong(), roleId));
    }

    @PutMapping("/roles/{roleId}/grants")
    @SaCheckPermission("system:role:assign")
    public ApiResponse<Void> assignRoleGrants(
            @PathVariable
            long roleId,
            @Valid
            @RequestBody
            RoleGrantRequest request
    ) {
        managementService.assignRoleGrants(StpUtil.getLoginIdAsLong(), roleId, request);
        return ApiResponse.ok();
    }

    @PostMapping("/menus")
    @SaCheckPermission("system:menu:create")
    public ApiResponse<Long> createMenu(
            @Valid
            @RequestBody
    MenuSaveRequest request) {
        return ApiResponse.ok(managementService.createMenu(request));
    }

    @PutMapping("/menus/{menuId}")
    @SaCheckPermission("system:menu:update")
    public ApiResponse<Void> updateMenu(
            @PathVariable
            long menuId,
            @Valid
            @RequestBody
            MenuSaveRequest request
    ) {
        managementService.updateMenu(menuId, request);
        return ApiResponse.ok();
    }

    @DeleteMapping("/menus/{menuId}")
    @SaCheckPermission("system:menu:delete")
    public ApiResponse<Void> deleteMenu(
            @PathVariable
            long menuId) {
        managementService.deleteMenu(menuId);
        return ApiResponse.ok();
    }

    @PostMapping("/permissions")
    @SaCheckPermission("system:menu:create")
    public ApiResponse<Long> createPermission(
            @Valid
            @RequestBody
    PermissionSaveRequest request) {
        return ApiResponse.ok(managementService.createPermission(request));
    }

    @PutMapping("/permissions/{permissionId}")
    @SaCheckPermission("system:menu:update")
    public ApiResponse<Void> updatePermission(
            @PathVariable
            long permissionId,
            @Valid
            @RequestBody
            PermissionSaveRequest request
    ) {
        managementService.updatePermission(permissionId, request);
        return ApiResponse.ok();
    }

    @DeleteMapping("/permissions/{permissionId}")
    @SaCheckPermission("system:menu:delete")
    public ApiResponse<Void> deletePermission(
            @PathVariable
            long permissionId) {
        managementService.deletePermission(permissionId);
        return ApiResponse.ok();
    }
}
