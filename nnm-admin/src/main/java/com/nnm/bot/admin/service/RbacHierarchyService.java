package com.nnm.bot.admin.service;

import com.nnm.bot.admin.domain.SysRole;
import com.nnm.bot.admin.domain.SysUser;
import com.nnm.bot.admin.domain.SysMenu;
import com.nnm.bot.admin.domain.SysPermission;
import com.nnm.bot.admin.repository.SysUserRepository;
import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.validation.BusinessAssert;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RbacHierarchyService {

    @Resource
    private SysUserRepository userRepository;

    public SysUser operator(long operatorId) {
        return this.userRepository.findWithAuthoritiesById(operatorId)
                .filter(SysUser::isEnabled)
                .orElseThrow(() -> new BusinessException(
                        CommonErrorCode.AUTHENTICATION_REQUIRED,
                        "账号不可用"
                ));
    }

    public boolean canManageUser(SysUser operator, SysUser target) {
        return !operator.getId().equals(target.getId())
                && effectiveLevel(operator) > assignedLevel(target);
    }

    public int effectiveLevel(long userId) {
        return effectiveLevel(operator(userId));
    }

    public Map<Long, Integer> assignedLevels(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }
        return userRepository.findAllWithRolesByIdIn(userIds).stream()
                .collect(Collectors.toUnmodifiableMap(
                        SysUser::getId,
                        this::assignedLevel,
                        Math::max));
    }

    public boolean canManageRole(SysUser operator, SysRole role) {
        return effectiveLevel(operator) > role.getAuthorityLevel();
    }

    public boolean canAssignRole(SysUser operator, SysRole role) {
        return role.isEnabled() && canManageRole(operator, role);
    }

    public void checkCanManageUser(SysUser operator, SysUser target) {
        BusinessAssert.isTrue(
                canManageUser(operator, target),
                CommonErrorCode.PERMISSION_DENIED,
                "权限不足，无法操作当前用户或同级及更高级别用户"
        );
    }

    public void checkCanManageRole(SysUser operator, SysRole role) {
        BusinessAssert.isTrue(
                canManageRole(operator, role),
                CommonErrorCode.PERMISSION_DENIED,
                "权限不足，无法管理同级或更高级别角色"
        );
    }

    public void checkCanAssignRoles(SysUser operator, Collection<SysRole> roles) {
        BusinessAssert.isTrue(
                roles.stream().allMatch(role -> canAssignRole(operator, role)),
                CommonErrorCode.PERMISSION_DENIED,
                "不能分配未启用、同级或更高级别角色"
        );
    }

    public void checkRequestedLevel(SysUser operator, int authorityLevel) {
        BusinessAssert.isTrue(
                authorityLevel >= 0 && authorityLevel < effectiveLevel(operator),
                CommonErrorCode.PERMISSION_DENIED,
                "角色等级必须低于当前操作者等级"
        );
    }

    public void checkCanAssignMenus(SysUser operator, Collection<SysMenu> menus) {
        if (isSuperAdmin(operator)) {
            return;
        }
        Set<Long> manageableMenuIds = operator.getRoles().stream()
                .filter(SysRole::isEnabled)
                .flatMap(role -> role.getMenus().stream())
                .map(SysMenu::getId)
                .collect(Collectors.toSet());
        BusinessAssert.isTrue(
                menus.stream().map(SysMenu::getId).allMatch(manageableMenuIds::contains),
                CommonErrorCode.PERMISSION_DENIED,
                "不能分配当前操作者未拥有的菜单"
        );
    }

    public void checkCanAssignPermissions(SysUser operator, Collection<SysPermission> permissions) {
        if (isSuperAdmin(operator)) {
            return;
        }
        Set<String> manageablePermissionCodes = operator.getRoles().stream()
                .filter(SysRole::isEnabled)
                .flatMap(role -> role.getPermissions().stream())
                .filter(SysPermission::isEnabled)
                .map(SysPermission::getCode)
                .collect(Collectors.toSet());
        BusinessAssert.isTrue(
                permissions.stream()
                        .map(SysPermission::getCode)
                        .allMatch(manageablePermissionCodes::contains),
                CommonErrorCode.PERMISSION_DENIED,
                "不能分配当前操作者未拥有的权限"
        );
    }

    private boolean isSuperAdmin(SysUser user) {
        return user.getRoles().stream()
                .filter(SysRole::isEnabled)
                .anyMatch(role -> "R_SUPER".equals(role.getCode()));
    }

    private int effectiveLevel(SysUser user) {
        return user.getRoles().stream()
                .filter(SysRole::isEnabled)
                .mapToInt(SysRole::getAuthorityLevel)
                .max()
                .orElse(0);
    }

    private int assignedLevel(SysUser user) {
        // 已停用的高等级角色仍保护其关联账号，避免通过停用角色绕过层级约束。
        return user.getRoles().stream()
                .mapToInt(SysRole::getAuthorityLevel)
                .max()
                .orElse(0);
    }
}
