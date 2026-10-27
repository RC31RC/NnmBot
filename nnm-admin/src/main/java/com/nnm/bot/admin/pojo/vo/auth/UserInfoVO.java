package com.nnm.bot.admin.pojo.vo.auth;

import java.util.List;

/** 当前登录用户及前端权限信息。 */
public record UserInfoVO(
        /** 可见的按钮权限码。 */
        List<String> buttons,
        /** 用户拥有的角色编码。 */
        List<String> roles,
        /** 系统用户主键。 */
        Long userId,
        /** 登录用户名。 */
        String userName,
        /** 用户邮箱。 */
        String email,
        /** 用户头像地址。 */
        String avatar
) {
}
