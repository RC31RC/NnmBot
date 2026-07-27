package com.nnm.bot.admin.dto.system;

import java.util.List;
import java.util.Set;

public record RoleGrantResponse(
        List<MenuItem> menuTree,
        Set<Long> menuIds,
        Set<Long> permissionIds
) {
}
