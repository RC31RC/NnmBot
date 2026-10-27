package com.nnm.bot.admin.pojo.dto.system;

import java.time.LocalDateTime;
import java.util.List;

/** 后台用户列表项。 */
public record UserListDTO(
        /** 用户主键。 */
        Long id,
        /** 头像地址。 */
        String avatar,
        /** 用户状态。 */
        String status,
        /** 登录用户名。 */
        String userName,
        /** 性别。 */
        String userGender,
        /** 昵称。 */
        String nickName,
        /** 手机号。 */
        String userPhone,
        /** 邮箱。 */
        String userEmail,
        /** 角色名称列表。 */
        List<String> userRoles,
        /** 当前操作者是否可管理该用户。 */
        boolean manageable,
        /** 创建人。 */
        String createBy,
        /** 创建时间。 */
        LocalDateTime createTime,
        /** 更新人。 */
        String updateBy,
        /** 最后更新时间。 */
        LocalDateTime updateTime
) {
}
