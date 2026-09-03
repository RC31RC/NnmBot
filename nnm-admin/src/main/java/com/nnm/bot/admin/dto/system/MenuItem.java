package com.nnm.bot.admin.dto.system;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
/** 前端导航菜单树节点。 */
public record MenuItem(
        /** 菜单主键。 */
        Long id,
        /** 父菜单主键。 */
        Long parentId,
        /** 前端路由路径。 */
        String path,
        /** 前端路由名称。 */
        String name,
        /** 页面组件路径。 */
        String component,
        /** 菜单显示元数据。 */
        MenuMeta meta,
        /** 子菜单列表。 */
        List<MenuItem> children
) {
}
