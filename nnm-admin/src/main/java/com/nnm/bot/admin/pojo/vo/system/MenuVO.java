package com.nnm.bot.admin.pojo.vo.system;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
/** 前端导航菜单树节点。 */
public record MenuVO(
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
        MenuMetaVO meta,
        /** 子菜单列表。 */
        List<MenuVO> children
) {
}
