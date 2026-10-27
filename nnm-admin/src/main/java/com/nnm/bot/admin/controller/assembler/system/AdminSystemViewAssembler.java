package com.nnm.bot.admin.controller.assembler.system;

import com.nnm.bot.admin.pojo.dto.system.AuthDTO;
import com.nnm.bot.admin.pojo.dto.system.MenuDTO;
import com.nnm.bot.admin.pojo.dto.system.MenuMetaDTO;
import com.nnm.bot.admin.pojo.dto.system.OperationLogDTO;
import com.nnm.bot.admin.pojo.dto.system.PermissionDTO;
import com.nnm.bot.admin.pojo.dto.system.RoleGrantDTO;
import com.nnm.bot.admin.pojo.dto.system.RoleListDTO;
import com.nnm.bot.admin.pojo.dto.system.SessionDTO;
import com.nnm.bot.admin.pojo.dto.system.UserListDTO;
import com.nnm.bot.admin.pojo.vo.system.AuthVO;
import com.nnm.bot.admin.pojo.vo.system.MenuMetaVO;
import com.nnm.bot.admin.pojo.vo.system.MenuVO;
import com.nnm.bot.admin.pojo.vo.system.OperationLogVO;
import com.nnm.bot.admin.pojo.vo.system.PermissionVO;
import com.nnm.bot.admin.pojo.vo.system.RoleGrantVO;
import com.nnm.bot.admin.pojo.vo.system.RoleListVO;
import com.nnm.bot.admin.pojo.vo.system.SessionVO;
import com.nnm.bot.admin.pojo.vo.system.UserListVO;
import com.nnm.bot.common.response.PageResult;
import java.util.List;

/** 在 HTTP 展示边界转换数据，保持服务传输合同独立。 */
public final class AdminSystemViewAssembler {
    private AdminSystemViewAssembler() {
    }

    public static AuthVO toAuthVO(AuthDTO value) {
        if (value == null) return null;
        return new AuthVO(
                value.id(),
                value.title(),
                value.authMark(),
                value.enabled());
    }

    public static MenuVO toMenuVO(MenuDTO value) {
        if (value == null) return null;
        return new MenuVO(
                value.id(),
                value.parentId(),
                value.path(),
                value.name(),
                value.component(),
                AdminSystemViewAssembler.toMenuMetaVO(value.meta()),
                value.children() == null ? null : value.children().stream().map(AdminSystemViewAssembler::toMenuVO).toList());
    }

    public static List<MenuVO> toMenuVO(List<MenuDTO> values) {
        return values.stream().map(AdminSystemViewAssembler::toMenuVO).toList();
    }

    public static MenuMetaVO toMenuMetaVO(MenuMetaDTO value) {
        if (value == null) return null;
        return new MenuMetaVO(
                value.title(),
                value.icon(),
                value.keepAlive(),
                value.fixedTab(),
                value.isHideTab(),
                value.isFullPage(),
                value.isIframe(),
                value.link(),
                value.activePath(),
                value.showTextBadge(),
                value.sort(),
                value.isEnable(),
                value.isHide(),
                value.authList() == null ? null : value.authList().stream().map(AdminSystemViewAssembler::toAuthVO).toList());
    }

    public static OperationLogVO toOperationLogVO(OperationLogDTO value) {
        if (value == null) return null;
        return new OperationLogVO(
                value.id(),
                value.operatorId(),
                value.operatorName(),
                value.action(),
                value.targetType(),
                value.targetId(),
                value.detail(),
                value.ipAddress(),
                value.createdAt());
    }

    public static List<OperationLogVO> toOperationLogVO(List<OperationLogDTO> values) {
        return values.stream().map(AdminSystemViewAssembler::toOperationLogVO).toList();
    }

    public static PageResult<OperationLogVO> toOperationLogVO(PageResult<OperationLogDTO> page) {
        return PageResult.of(toOperationLogVO(page.records()), page.current(), page.size(), page.total());
    }

    public static PermissionVO toPermissionVO(PermissionDTO value) {
        if (value == null) return null;
        return new PermissionVO(
                value.id(),
                value.code(),
                value.name(),
                value.type(),
                value.enabled());
    }

    public static List<PermissionVO> toPermissionVO(List<PermissionDTO> values) {
        return values.stream().map(AdminSystemViewAssembler::toPermissionVO).toList();
    }

    public static RoleGrantVO toRoleGrantVO(RoleGrantDTO value) {
        if (value == null) return null;
        return new RoleGrantVO(
                value.menuTree() == null ? null : value.menuTree().stream().map(AdminSystemViewAssembler::toMenuVO).toList(),
                value.menuIds(),
                value.permissionIds());
    }

    public static RoleListVO toRoleListVO(RoleListDTO value) {
        if (value == null) return null;
        return new RoleListVO(
                value.roleId(),
                value.roleName(),
                value.roleCode(),
                value.description(),
                value.enabled(),
                value.dataScope(),
                value.authorityLevel(),
                value.manageable(),
                value.assignable(),
                value.createTime());
    }

    public static List<RoleListVO> toRoleListVO(List<RoleListDTO> values) {
        return values.stream().map(AdminSystemViewAssembler::toRoleListVO).toList();
    }

    public static PageResult<RoleListVO> toRoleListVO(PageResult<RoleListDTO> page) {
        return PageResult.of(toRoleListVO(page.records()), page.current(), page.size(), page.total());
    }

    public static SessionVO toSessionVO(SessionDTO value) {
        if (value == null) return null;
        return new SessionVO(
                value.token(),
                value.userId(),
                value.userName(),
                value.deviceType(),
                value.loginTime(),
                value.timeout());
    }

    public static List<SessionVO> toSessionVO(List<SessionDTO> values) {
        return values.stream().map(AdminSystemViewAssembler::toSessionVO).toList();
    }

    public static UserListVO toUserListVO(UserListDTO value) {
        if (value == null) return null;
        return new UserListVO(
                value.id(),
                value.avatar(),
                value.status(),
                value.userName(),
                value.userGender(),
                value.nickName(),
                value.userPhone(),
                value.userEmail(),
                value.userRoles(),
                value.manageable(),
                value.createBy(),
                value.createTime(),
                value.updateBy(),
                value.updateTime());
    }

    public static List<UserListVO> toUserListVO(List<UserListDTO> values) {
        return values.stream().map(AdminSystemViewAssembler::toUserListVO).toList();
    }

    public static PageResult<UserListVO> toUserListVO(PageResult<UserListDTO> page) {
        return PageResult.of(toUserListVO(page.records()), page.current(), page.size(), page.total());
    }
}
