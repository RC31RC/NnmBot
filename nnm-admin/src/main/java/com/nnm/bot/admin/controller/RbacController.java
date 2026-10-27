package com.nnm.bot.admin.controller;

import com.nnm.bot.admin.pojo.vo.system.UserListVO;
import com.nnm.bot.admin.pojo.vo.system.RoleListVO;
import com.nnm.bot.admin.pojo.vo.system.PermissionVO;
import com.nnm.bot.admin.pojo.vo.system.MenuVO;
import com.nnm.bot.admin.pojo.vo.auth.UserInfoVO;
import com.nnm.bot.admin.controller.assembler.system.AdminSystemViewAssembler;
import com.nnm.bot.admin.controller.assembler.auth.AdminAuthViewAssembler;
import cn.hutool.core.util.StrUtil;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.nnm.bot.admin.pojo.dto.auth.UserInfoDTO;
import com.nnm.bot.admin.pojo.dto.system.MenuDTO;
import com.nnm.bot.admin.pojo.dto.system.PermissionDTO;
import com.nnm.bot.admin.pojo.query.system.RoleQuery;
import com.nnm.bot.admin.pojo.dto.system.RoleListDTO;
import com.nnm.bot.admin.pojo.query.system.UserQuery;
import com.nnm.bot.admin.pojo.dto.system.UserListDTO;
import com.nnm.bot.admin.service.auth.AuthService;
import com.nnm.bot.admin.service.system.RbacQueryService;
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
    public ApiResponse<UserInfoVO> me(
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(AdminAuthViewAssembler.toUserInfoVO(authService.currentUser(StpUtil.getLoginIdAsLong())));
    }

    @PostMapping("/users/query")
    @SaCheckPermission("system:user:view")
    public ApiResponse<PageResult<UserListVO>> users(
            @RequestBody
            UserQuery request
    ) {
        return ApiResponse.ok(AdminSystemViewAssembler.toUserListVO(rbacQueryService.users(
                StpUtil.getLoginIdAsLong(),
                request.userName(),
                request.current() == null ? 1 : request.current(),
                request.size() == null ? 20 : request.size()
        )));
    }

    @PostMapping("/roles/query")
    @SaCheckPermission("system:role:view")
    public ApiResponse<PageResult<RoleListVO>> roles(
            @RequestBody
            RoleQuery request
    ) {
        String keyword = StrUtil.isBlank(request.roleName()) ? request.roleCode() : request.roleName();
        return ApiResponse.ok(AdminSystemViewAssembler.toRoleListVO(rbacQueryService.roles(
                StpUtil.getLoginIdAsLong(),
                keyword,
                request.current() == null ? 1 : request.current(),
                request.size() == null ? 20 : request.size()
        )));
    }

    @PostMapping("/permissions/query")
    @SaCheckPermission("system:role:assign")
    public ApiResponse<List<PermissionVO>> permissions(
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(AdminSystemViewAssembler.toPermissionVO(rbacQueryService.permissions()));
    }

    @PostMapping("/menus/query")
    public ApiResponse<List<MenuVO>> menus(
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(AdminSystemViewAssembler.toMenuVO(rbacQueryService.menus(StpUtil.getLoginIdAsLong())));
    }

    @PostMapping("/menus/all/query")
    @SaCheckPermission("system:menu:view")
    public ApiResponse<List<MenuVO>> allMenus(
            @RequestBody(required = false)
            EmptyRequest ignored) {
        return ApiResponse.ok(AdminSystemViewAssembler.toMenuVO(rbacQueryService.allMenus()));
    }
}
