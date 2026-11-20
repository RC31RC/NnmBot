package com.nnm.bot.admin.service.system;

import com.nnm.bot.admin.pojo.dto.system.MenuDTO;
import com.nnm.bot.admin.pojo.dto.system.PermissionDTO;
import com.nnm.bot.admin.pojo.dto.system.RoleListDTO;
import com.nnm.bot.admin.pojo.dto.system.UserListDTO;
import com.nnm.bot.common.response.PageResult;

import java.util.List;

/** 按当前 RBAC 可见范围查询用户、角色、菜单和权限点。 */
public interface RbacQueryService {

    /**
     * 分页查询用户。
     *
     * @param operatorId 操作人主键
     * @param username 用户名筛选条件
     * @param current 从 1 开始的页码
     * @param size 每页记录数
     * @return 用户分页结果
     */
    PageResult<UserListDTO> users(long operatorId, String username, int current, int size);

    /**
     * 分页查询角色。
     *
     * @param operatorId 操作人主键
     * @param keyword 角色编码或名称筛选条件
     * @param current 从 1 开始的页码
     * @param size 每页记录数
     * @return 角色分页结果
     */
    PageResult<RoleListDTO> roles(long operatorId, String keyword, int current, int size);

    /**
     * 查询全部可用于授权的权限点。
     *
     * @return 权限点列表
     */
    List<PermissionDTO> permissions();

    /**
     * 查询用户可见的导航菜单。
     *
     * @param userId 用户主键
     * @return 菜单列表
     */
    List<MenuDTO> menus(long userId);

    /**
     * 查询管理端配置的全部菜单。
     *
     * @return 菜单列表
     */
    List<MenuDTO> allMenus();
}
