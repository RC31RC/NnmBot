package com.nnm.bot.admin.controller.assembler.auth;

import com.nnm.bot.admin.pojo.dto.auth.LoginTokenDTO;
import com.nnm.bot.admin.pojo.dto.auth.UserInfoDTO;
import com.nnm.bot.admin.pojo.vo.auth.LoginTokenVO;
import com.nnm.bot.admin.pojo.vo.auth.UserInfoVO;

/** 在 HTTP 展示边界转换数据，保持服务传输合同独立。 */
public final class AdminAuthViewAssembler {
    private AdminAuthViewAssembler() {
    }

    public static LoginTokenVO toLoginTokenVO(LoginTokenDTO value) {
        if (value == null) return null;
        return new LoginTokenVO(
                value.token());
    }

    public static UserInfoVO toUserInfoVO(UserInfoDTO value) {
        if (value == null) return null;
        return new UserInfoVO(
                value.buttons(),
                value.roles(),
                value.userId(),
                value.userName(),
                value.email(),
                value.avatar());
    }

}
