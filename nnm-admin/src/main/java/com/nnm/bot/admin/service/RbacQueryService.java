package com.nnm.bot.admin.service;

import com.nnm.bot.admin.dto.system.MenuItem;
import com.nnm.bot.admin.dto.system.PermissionItem;
import com.nnm.bot.admin.dto.system.RoleListItem;
import com.nnm.bot.admin.dto.system.UserListItem;
import com.nnm.bot.common.response.PageResult;

import java.util.List;

public interface RbacQueryService {

    PageResult<UserListItem> users(long operatorId, String username, int current, int size);

    PageResult<RoleListItem> roles(long operatorId, String keyword, int current, int size);

    List<PermissionItem> permissions();

    List<MenuItem> menus(long userId);

    List<MenuItem> allMenus();
}
