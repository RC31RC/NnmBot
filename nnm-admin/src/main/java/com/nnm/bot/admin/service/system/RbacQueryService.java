package com.nnm.bot.admin.service.system;

import com.nnm.bot.admin.pojo.dto.system.MenuDTO;
import com.nnm.bot.admin.pojo.dto.system.PermissionDTO;
import com.nnm.bot.admin.pojo.dto.system.RoleListDTO;
import com.nnm.bot.admin.pojo.dto.system.UserListDTO;
import com.nnm.bot.common.response.PageResult;

import java.util.List;

public interface RbacQueryService {

    PageResult<UserListDTO> users(long operatorId, String username, int current, int size);

    PageResult<RoleListDTO> roles(long operatorId, String keyword, int current, int size);

    List<PermissionDTO> permissions();

    List<MenuDTO> menus(long userId);

    List<MenuDTO> allMenus();
}
