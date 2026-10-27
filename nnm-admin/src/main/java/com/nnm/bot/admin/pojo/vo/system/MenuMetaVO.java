package com.nnm.bot.admin.pojo.vo.system;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
/** 前端菜单显示和路由行为配置。 */
public record MenuMetaVO(
        /** 菜单标题。 */
        String title,
        /** 菜单图标。 */
        String icon,
        /** 是否缓存页面。 */
        Boolean keepAlive,
        /** 是否固定标签页。 */
        Boolean fixedTab,
        /** 是否隐藏标签页。 */
        Boolean isHideTab,
        /** 是否全屏显示。 */
        Boolean isFullPage,
        /** 是否使用 iframe。 */
        Boolean isIframe,
        /** 外部链接。 */
        String link,
        /** 激活时高亮的路径。 */
        String activePath,
        /** 文字徽标。 */
        String showTextBadge,
        /** 菜单排序值。 */
        Integer sort,
        /** 是否启用菜单。 */
        Boolean isEnable,
        /** 是否隐藏菜单。 */
        Boolean isHide,
        /** 菜单下的按钮权限。 */
        List<AuthVO> authList
) {
}
