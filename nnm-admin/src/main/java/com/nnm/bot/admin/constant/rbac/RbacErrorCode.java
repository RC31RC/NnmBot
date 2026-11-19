package com.nnm.bot.admin.constant.rbac;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 用户、角色、权限及菜单管理的稳定错误码。 */
@Getter
@RequiredArgsConstructor
public enum RbacErrorCode implements ErrorCode {
    USERNAME_CONFLICT(409201, "用户名已存在", HttpStatus.CONFLICT),
    ROLE_CODE_CONFLICT(409202, "角色编码已存在", HttpStatus.CONFLICT),
    SUPER_ROLE_PROTECTED(400201, "超级管理员角色不能删除", HttpStatus.BAD_REQUEST),
    ROLE_IN_USE(409203, "角色仍被用户使用，不能删除", HttpStatus.CONFLICT),
    MENU_CODE_CONFLICT(409204, "菜单标识已存在", HttpStatus.CONFLICT),
    MENU_HAS_CHILDREN(409205, "请先删除子菜单", HttpStatus.CONFLICT),
    MENU_HAS_PERMISSIONS(409206, "请先删除菜单下的按钮权限", HttpStatus.CONFLICT),
    PERMISSION_CODE_CONFLICT(409207, "权限标识已存在", HttpStatus.CONFLICT),
    ROLE_CODE_UNKNOWN(400202, "包含不存在的角色编码", HttpStatus.BAD_REQUEST),
    MENU_SELF_PARENT(400203, "菜单不能将自己设为父级", HttpStatus.BAD_REQUEST),
    MENU_PARENT_CYCLE(400204, "菜单父级关系不能形成循环", HttpStatus.BAD_REQUEST),
    CANNOT_DISABLE_SELF(400205, "不能禁用当前登录账号", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED(400206, "新增用户必须设置密码", HttpStatus.BAD_REQUEST),
    CANNOT_MANAGE_USER(403201, "权限不足，无法操作当前用户或同级及更高级别用户", HttpStatus.FORBIDDEN),
    CANNOT_MANAGE_ROLE(403202, "权限不足，无法管理同级或更高级别角色", HttpStatus.FORBIDDEN),
    CANNOT_ASSIGN_ROLE(403203, "不能分配未启用、同级或更高级别角色", HttpStatus.FORBIDDEN),
    AUTHORITY_LEVEL_DENIED(403204, "角色等级必须低于当前操作者等级", HttpStatus.FORBIDDEN),
    CANNOT_ASSIGN_MENU(403205, "不能分配当前操作者未拥有的菜单", HttpStatus.FORBIDDEN),
    CANNOT_ASSIGN_PERMISSION(403206, "不能分配当前操作者未拥有的权限", HttpStatus.FORBIDDEN),
    ONLINE_SESSION_NOT_FOUND(404201, "在线会话不存在", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(404202, "用户不存在", HttpStatus.NOT_FOUND), ROLE_NOT_FOUND(404203, "角色不存在", HttpStatus.NOT_FOUND),
    MENU_NOT_FOUND(404204, "菜单不存在", HttpStatus.NOT_FOUND), PERMISSION_NOT_FOUND(404205, "权限不存在", HttpStatus.NOT_FOUND),
    INVALID_REFERENCE_LIST(400207, "列表包含不存在的数据", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
