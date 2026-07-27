package com.nnm.bot.admin.dto.system;

public record PermissionItem(
        Long id,
        String code,
        String name,
        String type,
        boolean enabled
) {
}
