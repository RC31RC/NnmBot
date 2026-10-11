package com.nnm.bot.admin.dto.system;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

/** 保存后台用户的请求。 */
public record UserSaveRequest(
        /** 登录用户名。 */
        @NotBlank
        @Size(max = 50)
        String userName,
        /** 明文密码，仅在创建或修改密码时提供。 */
        @Size(min = 6, max = 100)
        String password,
        /** 用户昵称。 */
        @Size(max = 100)
        String nickName,
        /** 手机号。 */
        @Size(max = 30)
        String userPhone,
        /** 邮箱。 */
        @Email
        @Size(max = 120)
        String userEmail,
        /** 性别。 */
        @Size(max = 20)
        String userGender,
        /** 头像地址。 */
        @Size(max = 500)
        String avatar,
        /** 是否启用。 */
        Boolean enabled,
        /** 要分配的角色编码。 */
        Set<String> roleCodes
) {
}
