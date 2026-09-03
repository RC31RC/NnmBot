package com.nnm.bot.admin.dto.system;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** 保存后台菜单的请求。 */
public record MenuSaveRequest(
        /** 父菜单主键。 */
        Long parentId,
        /** 前端路由名称。 */
        @NotBlank @Size(max = 100) String name,
        /** 前端路由路径。 */
        @NotBlank @Size(max = 255) String path,
        /** 页面组件路径。 */
        @Size(max = 255) String component,
        /** 菜单标题。 */
        @NotBlank @Size(max = 100) String title,
        /** 菜单图标。 */
        @Size(max = 100) String icon,
        /** 同级菜单排序值。 */
        int sortOrder,
        /** 是否缓存页面。 */
        @NotNull Boolean keepAlive,
        /** 是否显示菜单。 */
        @NotNull Boolean visible,
        /** 是否启用菜单。 */
        @NotNull Boolean enabled
) {
}
