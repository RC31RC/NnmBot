package com.nnm.bot.admin.pojo.vo.system;

/** 菜单下的按钮权限摘要。 */
public record AuthVO(
        /** 权限主键。 */
        Long id,
        /** 权限显示名称。 */
        String title,
        /** 前端按钮权限码。 */
        String authMark,
        /** 是否启用。 */
        boolean enabled
) {
}
