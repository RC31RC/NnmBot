package com.nnm.bot.admin.dto.system;

import java.time.LocalDateTime;

public record RoleListItem(
        Long roleId,
        String roleName,
        String roleCode,
        String description,
        boolean enabled,
        String dataScope,
        LocalDateTime createTime
) {
}
